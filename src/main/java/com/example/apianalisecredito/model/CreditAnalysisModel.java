package com.example.apianalisecredito.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditAnalysisModel(
        Boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        BigDecimal monthlyIncome,
        BigDecimal requestedAmount,
        BigDecimal annualInterest,
        UUID clientId
) {
}
