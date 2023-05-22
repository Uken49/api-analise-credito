package com.example.apianalisecredito.controller.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditAnalysisRequest(UUID clientId, BigDecimal monthlyIncome, BigDecimal requestedAmount) {
}
