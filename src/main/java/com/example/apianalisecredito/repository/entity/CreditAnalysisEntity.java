package com.example.apianalisecredito.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Immutable
public class CreditAnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    Boolean approved;
    BigDecimal approvedLimit;
    BigDecimal requestedAmount;
    BigDecimal withdraw;
    BigDecimal monthly_income;
    BigDecimal requested_amount;
    BigDecimal annualInterest;
    UUID clientId;
    LocalDateTime date;

    public UUID getId() {
        return id;
    }

    public Boolean getApproved() {
        return approved;
    }

    public BigDecimal getApprovedLimit() {
        return approvedLimit;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public BigDecimal getAnnualInterest() {
        return annualInterest;
    }

    public UUID getClientId() {
        return clientId;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
