package com.example.apianalisecredito.model;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

public record CreditAnalysisModel(
        Boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        @Positive(message = "monthlyIncome deve ser positivo")
        BigDecimal monthlyIncome,
        @Positive(message = "requestedAmount deve ser positivo")
        BigDecimal requestedAmount,
        BigDecimal annualInterest,
        UUID clientId
) {

    @Builder(toBuilder = true)
    public CreditAnalysisModel(Boolean approved, BigDecimal approvedLimit, BigDecimal withdraw, BigDecimal monthlyIncome, BigDecimal requestedAmount,
                               BigDecimal annualInterest, UUID clientId) {
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.monthlyIncome = monthlyIncome;
        this.requestedAmount = requestedAmount;
        this.annualInterest = annualInterest;
        this.clientId = clientId;
    }

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
