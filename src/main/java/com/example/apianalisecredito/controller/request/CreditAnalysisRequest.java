package com.example.apianalisecredito.controller.request;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.validator.constraints.br.CPF;

public record CreditAnalysisRequest(
        UUID clientId,
        @Min(0)
        BigDecimal monthlyIncome,
        @Min(0)
        BigDecimal requestedAmount,
        @CPF
        String cpf
) {
}
