package com.example.apianalisecredito.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class AnalysisEntity {
    @Id
    private UUID id;
    private boolean approved;

}
