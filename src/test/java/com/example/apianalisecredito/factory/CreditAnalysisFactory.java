package com.example.apianalisecredito.factory;

import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.math.BigDecimal;
import java.util.UUID;

public class CreditAnalysisFactory {
    public static CreditAnalysisRequest requestAmountGreaterThanMonthlyIncome(
    ) {
        return new CreditAnalysisRequest(
                UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"),
                BigDecimal.valueOf(10_000.00),
                BigDecimal.valueOf(12_150.49),
                "08505694007"
        );
    }

    public static CreditAnalysisRequest requestAmountLessThanMonthlyIncome(
    ) {
        return new CreditAnalysisRequest(
                UUID.fromString("9a3ce119-293a-4fcd-8d4f-08bbb345c022"),
                BigDecimal.valueOf(2_000.00),
                BigDecimal.valueOf(800.00),
                "08505694007"
        );
    }

    public static CreditAnalysisEntity entityNotApproved() {
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

    public static CreditAnalysisEntity entityApproved() {
        return CreditAnalysisEntity.builder()
                .approved(true)
                .approvedLimit(BigDecimal.valueOf(600.00))
                .withdraw(BigDecimal.valueOf(60.00))
                .monthlyIncome(BigDecimal.valueOf(800.00))
                .annualInterest(BigDecimal.valueOf(15))
                .clientId(UUID.fromString("9a3ce119-293a-4fcd-8d4f-08bbb345c022"))
                .build();
    }
}
