package com.example.apianalisecredito.service;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.model.CreditAnalysisModel;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
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
    private final Double withdrawalLimitPercentage = 0.1;
    private final Double interestPerYearPercentage = 0.15;
    private final BigDecimal maximumAmountOfMonthlyIncomeConsidered = BigDecimal.valueOf(50_000.00);
    private final double percentageApprovedValue;
    private final double ifRequestedValueIsGreaterThan50 = 0.15;
    private final double ifTheRequestedValueIsLessThanOrEqual50 = 0.3;

    public CreditAnalysisResponse requestCreditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {

        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);
        final ApiClientDto clientById = apiClient.getClientById(creditAnalysisModel.clientId());

        final Integer requestAmountGreaterThanMonthlyIncome = -1; // 1 significa que o requestAmount é maior que o monthlyIncome
        if (creditAnalysisModel.requestedAmount().compareTo(creditAnalysisModel.monthlyIncome()) == requestAmountGreaterThanMonthlyIncome) {
            return null;
        }

        return null;
    }
}
