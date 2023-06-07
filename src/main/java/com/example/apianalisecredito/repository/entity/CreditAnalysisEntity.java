package com.example.apianalisecredito.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Table(name = "CREDITY_ANALYSIS")
public class CreditAnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    Boolean approved;
    BigDecimal approvedLimit;
    BigDecimal withdraw;
    BigDecimal monthlyIncome;
    BigDecimal requestedAmount;
    BigDecimal annualInterest;
    UUID clientId;
    LocalDateTime date;

    @Generated
    public CreditAnalysisEntity() {
    }

    @Builder(toBuilder = true)
    public CreditAnalysisEntity(Boolean approved, BigDecimal approvedLimit, BigDecimal withdraw, BigDecimal monthlyIncome, BigDecimal requestedAmount,
                                BigDecimal annualInterest, UUID clientId) {
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.monthlyIncome = monthlyIncome;
        this.requestedAmount = requestedAmount;
        this.annualInterest = annualInterest;
        this.clientId = clientId;
        this.date = LocalDateTime.now();
    }

}
