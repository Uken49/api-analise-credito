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
import feign.FeignException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditAnalysisService {

    private final CreditAnalysisRepository repository;
    private final CreditAnalysisMapper mapper;
    private final ApiClient apiClient;

    // Business rules
    private final BigDecimal withdrawalLimitPercentage = BigDecimal.valueOf(0.1);
    private final BigDecimal interestPerYearPercentage = BigDecimal.valueOf(15, 0);
    private final BigDecimal maxAmountOfMonthlyIncomeConsidered = BigDecimal.valueOf(50_000_00, 2);
    private final BigDecimal ifRequestedValueIsGreaterThan50 = BigDecimal.valueOf(0.15);
    private final BigDecimal ifTheRequestedValueIsLessThanOrEqual50 = BigDecimal.valueOf(0.3);
    private final BigDecimal percentageForCreditAnalysis = BigDecimal.valueOf(0.5);
    private final int equalToHalfTheValue = 0;
    private final int decimalScale = 2;

    public CreditAnalysisResponse creditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {

        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);
        final String analysisType = "id";
        consultId(analysisType, creditAnalysisModel.clientId().toString());

        final CreditAnalysisModel creditAnalysisModelUpdated;
        final CreditAnalysisEntity creditAnalysisEntity;
        final CreditAnalysisEntity creditAnalysisEntitySaved;

        final BigDecimal requestedAmount = creditAnalysisModel.requestedAmount().setScale(decimalScale, RoundingMode.HALF_UP);
        final BigDecimal monthlyIncome = creditAnalysisModel.monthlyIncome().setScale(decimalScale, RoundingMode.HALF_UP);

        final BigDecimal monthlyIncomeConsideredValue;

        final boolean approved;
        final BigDecimal approvedLimit;
        final BigDecimal withdraw;
        final BigDecimal annualInterest;

        if (requestedAmount.compareTo(monthlyIncome) > equalToHalfTheValue) {
            approved = false;
            withdraw = BigDecimal.valueOf(0);
            annualInterest = BigDecimal.valueOf(0);
            approvedLimit = BigDecimal.valueOf(0);
        } else {

            if (monthlyIncome.compareTo(maxAmountOfMonthlyIncomeConsidered) > equalToHalfTheValue) {
                monthlyIncomeConsideredValue = maxAmountOfMonthlyIncomeConsidered;
            } else {
                monthlyIncomeConsideredValue = monthlyIncome;
            }

            if (requestedAmount.compareTo(monthlyIncomeConsideredValue.multiply(percentageForCreditAnalysis)) > equalToHalfTheValue) {
                approvedLimit = monthlyIncomeConsideredValue.multiply(ifRequestedValueIsGreaterThan50).setScale(decimalScale, RoundingMode.HALF_UP);
            } else {
                approvedLimit =
                        monthlyIncomeConsideredValue.multiply(ifTheRequestedValueIsLessThanOrEqual50).setScale(decimalScale, RoundingMode.HALF_UP);
            }

            approved = true;
            withdraw = approvedLimit.multiply(withdrawalLimitPercentage).setScale(decimalScale, RoundingMode.HALF_UP);
            annualInterest = interestPerYearPercentage.setScale(0, RoundingMode.HALF_UP);
        }

        creditAnalysisModelUpdated = creditAnalysisModel.creditAnalysisUpdate(approved, approvedLimit, withdraw, annualInterest);
        creditAnalysisEntity = mapper.fromEntity(creditAnalysisModelUpdated);
        creditAnalysisEntitySaved = repository.save(creditAnalysisEntity);

        return mapper.fromResponse(creditAnalysisEntitySaved);
    }

    public List<CreditAnalysisResponse> getCreditAnalysisById(String identifier) {
        final int cpfSizeWithoutPunctuation = 11;
        final int cpfSizeWithPunctuation = 14;
        final List<CreditAnalysisEntity> creditAnalysisEntity;
        final UUID id;
        final String analysisType;

        if (identifier.length() == cpfSizeWithoutPunctuation || identifier.length() == cpfSizeWithPunctuation) {
            analysisType = "cpf";
            final ApiClientDto clientById = consultId(analysisType, identifier);
            id = clientById.id();
        } else {
            analysisType = "id";
            id = UUID.fromString(identifier);
        }

        creditAnalysisEntity = repository.findAllByClientId(id);

        if (creditAnalysisEntity.isEmpty()) {
            creditAnalysisEntity.add(repository.findById(id)
                    .orElseThrow(() -> new CreditAnalysisNotFoundException("Análise com %s: %s não foi encontrada"
                            .formatted(analysisType, identifier))));
        }

        return creditAnalysisEntity.stream()
                .map(mapper::fromResponse)
                .toList();
    }

    public List<CreditAnalysisResponse> getAllCreditAnalysis() {
        return repository.findAll().stream()
                .map(mapper::fromResponse)
                .toList();
    }

    private ApiClientDto consultId(String analysisType, String identifier) {
        try {
            return apiClient.getClientByIdOrCpf(identifier);
        } catch (FeignException fe) {
            throw new ClientNotFoundException("Cliente com %s: %s não foi encontrado".formatted(analysisType, identifier));
        }
    }

}
