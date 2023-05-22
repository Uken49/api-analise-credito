package com.example.apianalisecredito.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "CREDITY_ANALYSIS")
public class CreditAnalysisEntity {
    @Id
    UUID id;
    Boolean approved;
    BigDecimal approvedLimit;
    BigDecimal withdraw;
    BigDecimal monthlyIncome;
    BigDecimal requestedAmount;
    BigDecimal annualInterest;
    UUID clientId;
    LocalDateTime date;

    public CreditAnalysisEntity() {
        this.id = UUID.randomUUID();
    }

    @Builder(toBuilder = true)
    public CreditAnalysisEntity(Boolean approved, BigDecimal approvedLimit, BigDecimal withdraw, BigDecimal monthlyIncome, BigDecimal requestedAmount,
                                BigDecimal annualInterest, UUID clientId) {
        this.id = UUID.randomUUID();
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.monthlyIncome = monthlyIncome;
        this.requestedAmount = requestedAmount;
        this.annualInterest = annualInterest;
        this.clientId = clientId;
        this.date = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Boolean getApproved() {
        return approved;
    }

    public BigDecimal getApprovedLimit() {
        return approvedLimit;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
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
