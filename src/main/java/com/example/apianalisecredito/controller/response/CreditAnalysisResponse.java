package com.example.apianalisecredito.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditAnalysisResponse(UUID id, Boolean approved, BigDecimal approvedLimit, BigDecimal withdraw, BigDecimal annualInterest,
                                     UUID clientId, LocalDateTime date) {
}
