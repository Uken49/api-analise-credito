package com.example.apianalisecredito.model;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreditAnalysisModel(
        Boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        @Positive(message = "monthlyIncome deve ser positivo")
        BigDecimal monthlyIncome,
        @Positive(message = "requestedAmount deve ser positivo")
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
