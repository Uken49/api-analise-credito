package com.example.apianalisecredito.service;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.model.CreditAnalysisModel;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.math.BigDecimal;
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
    private final BigDecimal interestPerYearPercentage = BigDecimal.valueOf(0.15);
    private final BigDecimal maxAmountOfMonthlyIncomeConsidered = BigDecimal.valueOf(50_000.00);
    private final BigDecimal ifRequestedValueIsGreaterThan50 = BigDecimal.valueOf(0.15);
    private final BigDecimal ifTheRequestedValueIsLessThanOrEqual50 = BigDecimal.valueOf(0.3);
    private final BigDecimal percentageForCreditAnalysis = BigDecimal.valueOf(0.5);
    private final int equalToHalfTheValue = 0;

    public CreditAnalysisResponse requestCreditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {

        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);
        final ApiClientDto clientId = apiClient.getClientByIdOrCpf(creditAnalysisModel.clientId().toString());

        final CreditAnalysisModel creditAnalysisModelUpdated;
        final CreditAnalysisEntity creditAnalysisEntity;
        final CreditAnalysisEntity creditAnalysisEntitySaved;

        final BigDecimal requestedAmount = creditAnalysisModel.requestedAmount();
        final BigDecimal monthlyIncome = creditAnalysisModel.monthlyIncome();

        final BigDecimal consideredValue;

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

            if (requestedAmount.compareTo(monthlyIncome.multiply(maxAmountOfMonthlyIncomeConsidered)) > equalToHalfTheValue) {
                consideredValue = maxAmountOfMonthlyIncomeConsidered;
            } else {
                consideredValue = requestedAmount;
            }

            if (requestedAmount.multiply(percentageForCreditAnalysis).compareTo(monthlyIncome) > equalToHalfTheValue) {
                approvedLimit = consideredValue.multiply(ifRequestedValueIsGreaterThan50);
            } else {
                approvedLimit = consideredValue.multiply(ifTheRequestedValueIsLessThanOrEqual50);
            }

            approved = true;
            withdraw = approvedLimit.multiply(withdrawalLimitPercentage);
            annualInterest = interestPerYearPercentage;
        }

        creditAnalysisModelUpdated = creditAnalysisModel.creditAnalysisUpdate(approved, approvedLimit, withdraw, annualInterest);
        creditAnalysisEntity = mapper.fromEntity(creditAnalysisModelUpdated);
        creditAnalysisEntitySaved = repository.save(creditAnalysisEntity);

        return mapper.fromResponse(creditAnalysisEntitySaved);
    }
}
