package com.example.apianalisecredito.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditAnalysisResponse(UUID clientId, BigDecimal monthlyIncome, BigDecimal requestedAmount) {
}
