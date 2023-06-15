package com.example.apianalisecredito.service;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.handler.exception.ClientNotFoundException;
import com.example.apianalisecredito.handler.exception.CreditAnalysisNotFoundException;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.model.CreditAnalysisModel;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditAnalysisService {

    private static final BigDecimal LIMIT_PERCENTAGE_TO_WITHDRAW = BigDecimal.valueOf(0.1);
    private static final BigDecimal INTEREST_PER_YEAR_PERCENTAGE = BigDecimal.valueOf(15, 0);
    private static final BigDecimal MAX_AMOUNT_OF_MONTHLY_INCOME_CONSIDERED = BigDecimal.valueOf(50_000_00, 2);
    private static final BigDecimal REQUESTED_VALUE_IS_GREATER_THAN_50 = BigDecimal.valueOf(0.15);
    private static final BigDecimal REQUESTED_VALUE_IS_LESS_THAN_OR_EQUAL_50 = BigDecimal.valueOf(0.3);
    private static final BigDecimal PERCENTAGE_FOR_CREDIT_ANALYSIS = BigDecimal.valueOf(0.5);
    private static final int DECIMAL_SCALE = 2;
    private final CreditAnalysisRepository repository;
    private final CreditAnalysisMapper mapper;
    private final ApiClient apiClient;

    public CreditAnalysisResponse creditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {
        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);

        consultClientById(creditAnalysisModel.clientId());

        final BigDecimal requestedAmount = creditAnalysisModel.requestedAmount();
        final BigDecimal monthlyIncome = creditAnalysisModel.monthlyIncome();

        final boolean approved;
        final BigDecimal approvedLimit;
        final BigDecimal withdraw;
        final BigDecimal annualInterest;

        if (isRequestAmountGreaterThanMonthlyIncome(requestedAmount, monthlyIncome)) {
            approved = false;
            withdraw = BigDecimal.valueOf(0);
            annualInterest = BigDecimal.valueOf(0);
            approvedLimit = BigDecimal.valueOf(0);
        } else {
            approved = true;

            final BigDecimal monthlyIncomeConsideredValue;

            monthlyIncomeConsideredValue = calculateConsideredValueOfMonthlyIncome(monthlyIncome);
            approvedLimit = calculatesApprovedLimit(requestedAmount, monthlyIncomeConsideredValue);
            withdraw = calculatesWithdraw(approvedLimit);
            annualInterest = INTEREST_PER_YEAR_PERCENTAGE;
        }

        final CreditAnalysisModel creditAnalysisModelUpdated =
                creditAnalysisModel.creditAnalysisUpdate(approved, approvedLimit.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP),
                        withdraw.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP), annualInterest);

        final CreditAnalysisEntity creditAnalysisEntity = mapper.fromEntity(creditAnalysisModelUpdated);

        final CreditAnalysisEntity creditAnalysisEntitySaved = repository.save(creditAnalysisEntity);

        return mapper.fromResponse(creditAnalysisEntitySaved);
    }

    public List<CreditAnalysisResponse> getCreditAnalysisByClientCpf(String cpf) {

        final UUID id = consultClientByCpf(cpf).id();

        final List<CreditAnalysisEntity> creditAnalysisEntities = repository.findAllByClientId(id);

        return creditAnalysisEntities.stream().map(mapper::fromResponse).toList();
    }

    public List<CreditAnalysisResponse> getCreditAnalysisByClientId(UUID id) {

        final List<CreditAnalysisEntity> creditAnalysisEntity = repository.findAllByClientId(id);

        return creditAnalysisEntity.stream().map(mapper::fromResponse).toList();
    }

    public CreditAnalysisResponse getCreditAnalysisById(UUID id) {

        final CreditAnalysisEntity creditAnalysisEntity =
                repository.findById(id).orElseThrow(() -> new CreditAnalysisNotFoundException("Análise com id: %s não foi encontrado".formatted(id)));

        return mapper.fromResponse(creditAnalysisEntity);
    }

    public List<CreditAnalysisResponse> getAllCreditAnalysis() {
        return repository.findAll().stream().map(mapper::fromResponse).toList();
    }

    private ApiClientDto consultClientById(UUID id) {
        final ApiClientDto clientById = apiClient.getClientById(id);

        if (Objects.isNull(clientById)) {
            throw new ClientNotFoundException("Cliente com id: %s não foi encontrado".formatted(id));
        }

        return clientById;
    }

    private ApiClientDto consultClientByCpf(String cpf) {
        final ApiClientDto apiClientDto = apiClient.getClientByCpf(cpf).get(0);

        if (Objects.isNull(apiClientDto)) {
            throw new ClientNotFoundException("Cliente com CPF: %s não foi encontrado".formatted(cpf));
        }

        return apiClientDto;
    }

    private boolean isRequestAmountGreaterThanMonthlyIncome(BigDecimal requestedAmount, BigDecimal monthlyIncome) {
        return requestedAmount.compareTo(monthlyIncome) > 0;
    }

    private BigDecimal calculateConsideredValueOfMonthlyIncome(BigDecimal monthlyIncome) {

        if (monthlyIncome.compareTo(MAX_AMOUNT_OF_MONTHLY_INCOME_CONSIDERED) > 0) {
            return MAX_AMOUNT_OF_MONTHLY_INCOME_CONSIDERED;
        }

        return monthlyIncome;
    }

    private BigDecimal calculatesApprovedLimit(BigDecimal requestedAmount, BigDecimal monthlyIncome) {

        if (requestedAmount.compareTo(monthlyIncome.multiply(PERCENTAGE_FOR_CREDIT_ANALYSIS)) > 0) {
            return monthlyIncome.multiply(REQUESTED_VALUE_IS_GREATER_THAN_50);
        }

        return monthlyIncome.multiply(REQUESTED_VALUE_IS_LESS_THAN_OR_EQUAL_50);
    }

    private BigDecimal calculatesWithdraw(BigDecimal approvedLimit) {
        return approvedLimit.multiply(LIMIT_PERCENTAGE_TO_WITHDRAW);
    }

}
