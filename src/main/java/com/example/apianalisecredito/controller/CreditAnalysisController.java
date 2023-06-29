package com.example.apianalisecredito.controller;

import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.service.CreditAnalysisService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit/analysis")
@RequiredArgsConstructor
@Validated
public class CreditAnalysisController {

    private final CreditAnalysisService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditAnalysisResponse requestCreditAnalysis(
            @RequestBody @Valid CreditAnalysisRequest creditAnalysisRequest
    ) {
        return service.creditAnalysis(creditAnalysisRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CreditAnalysisResponse getCreditAnalysisById(@PathVariable UUID id) {
        return service.getCreditAnalysisById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CreditAnalysisResponse> getCreditAnalysisByClient(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) @CPF @Valid String cpf
    ) {

        if (Objects.nonNull(id)) {
            return service.getCreditAnalysisByClientId(id);
        } else if (Objects.nonNull(cpf)) {
            return service.getCreditAnalysisByClientCpf(cpf);
        }

        return service.getAllCreditAnalysis();
    }

}
