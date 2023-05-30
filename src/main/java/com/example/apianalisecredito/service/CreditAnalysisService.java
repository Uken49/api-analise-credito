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
import com.example.apianalisecredito.util.LoggerUtil;
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
    private final BigDecimal limitPercentageToWithdraw = BigDecimal.valueOf(0.1);
    private final BigDecimal interestPerYearPercentage = BigDecimal.valueOf(15, 0);
    private final BigDecimal maxAmountOfMonthlyIncomeConsidered = BigDecimal.valueOf(50_000_00, 2);
    private final BigDecimal ifRequestedValueIsGreaterThan50 = BigDecimal.valueOf(0.15);
    private final BigDecimal ifTheRequestedValueIsLessThanOrEqual50 = BigDecimal.valueOf(0.3);
    private final BigDecimal percentageForCreditAnalysis = BigDecimal.valueOf(0.5);
    private final int equalToHalfTheValue = 0;
    private final int decimalScale = 2;

    public CreditAnalysisResponse creditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {

        LoggerUtil.logInfo("Mapeando \"request\" para \"model\"", this.getClass());
        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);
        final String analysisType = "id";

        consultId(analysisType, creditAnalysisModel.clientId().toString());

        final CreditAnalysisModel creditAnalysisModelUpdated;
        final CreditAnalysisEntity creditAnalysisEntity;
        final CreditAnalysisEntity creditAnalysisEntitySaved;

        final BigDecimal requestedAmount = creditAnalysisModel.requestedAmount();
        final BigDecimal monthlyIncome = creditAnalysisModel.monthlyIncome();

        final boolean approved;
        final BigDecimal approvedLimit;
        final BigDecimal withdraw;
        final BigDecimal annualInterest;
        final BigDecimal monthlyIncomeConsideredValue;

        if (isRequestAmountGreaterThanMonthlyIncome(requestedAmount, monthlyIncome)) {
            LoggerUtil.logInfo("Análise não aprovada", this.getClass());
            approved = false;
            withdraw = BigDecimal.valueOf(0);
            annualInterest = BigDecimal.valueOf(0);
            approvedLimit = BigDecimal.valueOf(0);
        } else {
            LoggerUtil.logInfo("Análise aprovada", this.getClass());
            approved = true;
            monthlyIncomeConsideredValue = returnConsideredValueOfMonthlyIncome(monthlyIncome);
            approvedLimit = returnApprovedLimit(requestedAmount, monthlyIncomeConsideredValue);
            withdraw = returnWithdraw(approvedLimit);
            annualInterest = interestPerYearPercentage;
        }

        creditAnalysisModelUpdated = creditAnalysisModel.creditAnalysisUpdate(
                approved,
                approvedLimit.setScale(decimalScale, RoundingMode.HALF_UP),
                withdraw.setScale(decimalScale, RoundingMode.HALF_UP),
                annualInterest
        );

        LoggerUtil.logInfo("Mapeando \"model\" para \"entity\"", this.getClass());
        creditAnalysisEntity = mapper.fromEntity(creditAnalysisModelUpdated);

        LoggerUtil.logInfo("Usando método \"save\"", this.getClass());
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
            LoggerUtil.logInfo("Variável \"identifier\" é um CPF", this.getClass());
            analysisType = "cpf";
            final ApiClientDto clientById = consultId(analysisType, identifier);
            id = clientById.id();
        } else {
            LoggerUtil.logInfo("Variável \"identifier\" é um Id", this.getClass());
            analysisType = "id";
            id = UUID.fromString(identifier);
        }

        LoggerUtil.logInfo("Variável \"identifier\" é um Id", this.getClass());

        LoggerUtil.logInfo("Buscando análises pelo ID do cliente", this.getClass());
        creditAnalysisEntity = repository.findAllByClientId(id);

        if (creditAnalysisEntity.isEmpty()) {
            LoggerUtil.logInfo("Buscando análise pelo ID da análise", this.getClass());

            creditAnalysisEntity.add(repository.findById(id).orElseThrow(() -> {
                LoggerUtil.logError("Lançando exception de análise não encontrada", this.getClass());
                throw new CreditAnalysisNotFoundException("Análise com %s: %s não foi encontrada".formatted(analysisType, identifier));
            }));
        }

        return creditAnalysisEntity.stream().map(mapper::fromResponse).toList();
    }

    public List<CreditAnalysisResponse> getAllCreditAnalysis() {
        LoggerUtil.logError("Buscando todas as análises", this.getClass());
        return repository.findAll().stream().map(mapper::fromResponse).toList();
    }

    private ApiClientDto consultId(String analysisType, String identifier) {
        try {
            LoggerUtil.logInfo("Consultando id do cliente na outra API", this.getClass());
            return apiClient.getClientByIdOrCpf(identifier);
        } catch (FeignException fe) {
            LoggerUtil.logError("Lançando exception de cliente não encontrado", this.getClass());
            throw new ClientNotFoundException("Cliente com %s: %s não foi encontrado".formatted(analysisType, identifier));
        }
    }

    private boolean isRequestAmountGreaterThanMonthlyIncome(BigDecimal requestedAmount, BigDecimal monthlyIncome) {
        LoggerUtil.logInfo("Analisando se \"requestAmount\" é maior que \"monthlyIncome\"", this.getClass());
        return requestedAmount.compareTo(monthlyIncome) > equalToHalfTheValue;
    }

    private BigDecimal returnConsideredValueOfMonthlyIncome(BigDecimal monthlyIncome) {
        final BigDecimal monthlyIncomeConsideredValue;

        if (monthlyIncome.compareTo(maxAmountOfMonthlyIncomeConsidered) > equalToHalfTheValue) {
            monthlyIncomeConsideredValue = maxAmountOfMonthlyIncomeConsidered;
        } else {
            monthlyIncomeConsideredValue = monthlyIncome;
        }

        LoggerUtil.logInfo("Valor de monthlyIncome para análise será: %s".formatted(monthlyIncomeConsideredValue), this.getClass());
        return monthlyIncomeConsideredValue;
    }

    private BigDecimal returnApprovedLimit(BigDecimal requestedAmount, BigDecimal monthlyIncome) {
        final BigDecimal approvedLimit;

        if (requestedAmount.compareTo(monthlyIncome.multiply(percentageForCreditAnalysis)) > equalToHalfTheValue) {
            approvedLimit = monthlyIncome.multiply(ifRequestedValueIsGreaterThan50);
        } else {
            approvedLimit = monthlyIncome.multiply(ifTheRequestedValueIsLessThanOrEqual50);
        }

        LoggerUtil.logInfo("Valor de approvedLimit é: %s".formatted(approvedLimit), this.getClass());
        return approvedLimit;
    }

    private BigDecimal returnWithdraw(BigDecimal approvedLimit) {
        final BigDecimal withdraw = approvedLimit.multiply(limitPercentageToWithdraw);

        LoggerUtil.logInfo("Valor de withdraw é: %s".formatted(withdraw), this.getClass());
        return withdraw;
    }

}
