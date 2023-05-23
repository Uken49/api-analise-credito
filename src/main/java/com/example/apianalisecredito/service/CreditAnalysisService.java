package com.example.apianalisecredito.service;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.handler.exception.CreditAnalysisNotFoundException;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.model.CreditAnalysisModel;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
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
    private final BigDecimal interestPerYearPercentage = new BigDecimal(15);
    private final BigDecimal maxAmountOfMonthlyIncomeConsidered = BigDecimal.valueOf(50_000.00);
    private final BigDecimal ifRequestedValueIsGreaterThan50 = BigDecimal.valueOf(0.15);
    private final BigDecimal ifTheRequestedValueIsLessThanOrEqual50 = BigDecimal.valueOf(0.3);
    private final BigDecimal percentageForCreditAnalysis = BigDecimal.valueOf(0.5);
    private final int equalToHalfTheValue = 0;

    public CreditAnalysisResponse requestCreditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {

        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);
        apiClient.getClientByIdOrCpf(creditAnalysisModel.clientId().toString());

        final CreditAnalysisModel creditAnalysisModelUpdated;
        final CreditAnalysisEntity creditAnalysisEntity;
        final CreditAnalysisEntity creditAnalysisEntitySaved;

        final BigDecimal requestedAmount = creditAnalysisModel.requestedAmount().setScale(2, RoundingMode.HALF_UP);
        final BigDecimal monthlyIncome = creditAnalysisModel.monthlyIncome().setScale(2, RoundingMode.HALF_UP);

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

    public CreditAnalysisResponse getCreditAnalysisById(String identifier) {
        final int cpfSizeWithoutPunctuation = 11;
        final int cpfSizeWithPunctuation = 14;
        final CreditAnalysisEntity creditAnalysisEntity;
        final UUID id;
        final String analysisType;

        if (identifier.length() == cpfSizeWithoutPunctuation || identifier.length() == cpfSizeWithPunctuation) {
            final ApiClientDto clientById = apiClient.getClientByIdOrCpf(identifier);
            id = clientById.id();
            analysisType = "cpf";
        } else {
            id = UUID.fromString(identifier);
            analysisType = "id";
        }

        creditAnalysisEntity = repository.findById(id)
                .orElseThrow(() -> new CreditAnalysisNotFoundException("Análise com %s: %s não foi encontrada".formatted(analysisType, identifier)));

        return mapper.fromResponse(creditAnalysisEntity);
    }

    public List<CreditAnalysisResponse> getAllCreditAnalysis() {
        return repository.findAll().stream()
                .map(mapper::fromResponse)
                .toList();
    }

}
