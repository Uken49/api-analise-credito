package com.example.apianalisecredito.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreditAnalysisModel(
        Boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        BigDecimal monthlyIncome,
        BigDecimal requestedAmount,
        BigDecimal annualInterest,
        UUID clientId,
        String cpf
) {

    public CreditAnalysisModel creditAnalysisUpdate(Boolean approved, BigDecimal approvedLimit, BigDecimal withdraw, BigDecimal annualInterest) {
        return this.toBuilder()
                .approved(approved)
                .approvedLimit(approvedLimit)
                .withdraw(withdraw)
                .monthlyIncome(this.monthlyIncome)
                .requestedAmount(this.requestedAmount)
                .annualInterest(annualInterest)
                .clientId(this.clientId)
                .build();
    }
}
