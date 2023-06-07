package com.example.apianalisecredito.repository;

import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface CreditAnalysisRepository extends CrudRepository<CreditAnalysisEntity, UUID> {
}
