package com.example.apianalisecredito.controller.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreditAnalysisRequest(
        UUID clientId,
        @DecimalMax(value = "${validatedValue.requestedAmount}", inclusive = false, message = "monthlyIncome deve ser maior que requestedAmount")
        @Positive(message = "monthlyIncome deve ser positivo")
        BigDecimal monthlyIncome,
        @Positive(message = "requestedAmount deve ser positivo")
        BigDecimal requestedAmount
) {
}
