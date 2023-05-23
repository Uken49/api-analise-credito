package com.example.apianalisecredito.factory;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.math.BigDecimal;
import java.util.UUID;

public class CreditAnalysisFactory {
    public static CreditAnalysisRequest creditAnalysisRequestFactory(
    ) {
        return new CreditAnalysisRequest(
                UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"),
                BigDecimal.valueOf(10_000.00),
                BigDecimal.valueOf(12_150.49),
                "08505694007"
        );
    }

    public static ApiClientDto apiClientDtoFactory(){
        return new ApiClientDto(UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"));
    }

    public static CreditAnalysisEntity entityNotApprovedFactory(){
        return CreditAnalysisEntity
                .builder()
                .approved(false)
                .approvedLimit(BigDecimal.valueOf(0))
                .withdraw(BigDecimal.valueOf(0))
                .monthlyIncome(BigDecimal.valueOf(0))
                .annualInterest(BigDecimal.valueOf(15))
                .clientId(UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"))
                .build();
    }
}
