package com.example.apianalisecredito.mapper;

import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.model.CreditAnalysisModel;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditAnalysisMapper {

    CreditAnalysisModel fromModel(CreditAnalysisRequest creditAnalysisRequest);

    CreditAnalysisEntity fromEntity(CreditAnalysisModel creditAnalysisModel);

    CreditAnalysisResponse fromRequest(CreditAnalysisEntity creditAnalysisEntity);

}
