package com.example.apianalisecredito.factory;

import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class CreditAnalysisFactory {
    public static CreditAnalysisRequest requestAmountGreaterThanMonthlyIncome(
            BigDecimal monthlyIncome,
            BigDecimal requestedAmount
    ) {
        return new CreditAnalysisRequest(
                UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"),
                monthlyIncome,
                requestedAmount,
                "08505694007"
        );
    }

    public static CreditAnalysisRequest requestAmountLessThanMonthlyIncome(
            BigDecimal monthlyIncome,
            BigDecimal requestedAmount
    ) {
        return new CreditAnalysisRequest(
                UUID.fromString("9a3ce119-293a-4fcd-8d4f-08bbb345c022"),
                monthlyIncome,
                requestedAmount,
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
                .annualInterest(BigDecimal.valueOf(15).setScale(0, RoundingMode.HALF_UP))
                .clientId(UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"))
                .build();
    }

    public static CreditAnalysisEntity entityRequestAmountGreaterThanMonthlyIncome() {
        return CreditAnalysisEntity.builder()
                .approved(true)
                .approvedLimit(BigDecimal.valueOf(600_00).setScale(2, RoundingMode.HALF_UP))
                .withdraw(BigDecimal.valueOf(60_00).setScale(2, RoundingMode.HALF_UP))
                .monthlyIncome(BigDecimal.valueOf(800_00).setScale(2, RoundingMode.HALF_UP))
                .annualInterest(BigDecimal.valueOf(15).setScale(0, RoundingMode.HALF_UP))
                .clientId(UUID.fromString("9a3ce119-293a-4fcd-8d4f-08bbb345c022"))
                .build();
    }

    public static CreditAnalysisEntity entityRequestAmountGreaterThanMaxAmountOfMonthlyIncomeConsidered(){
        return CreditAnalysisEntity.builder()
                .approved(true)
                .approvedLimit(BigDecimal.valueOf(600_00).setScale(2, RoundingMode.HALF_UP))
                .withdraw(BigDecimal.valueOf(60_00).setScale(2, RoundingMode.HALF_UP))
                .monthlyIncome(BigDecimal.valueOf(800_00).setScale(2, RoundingMode.HALF_UP))
                .annualInterest(BigDecimal.valueOf(15).setScale(0, RoundingMode.HALF_UP))
                .clientId(UUID.fromString("9a3ce119-293a-4fcd-8d4f-08bbb345c022"))
                .build();
    }
}
