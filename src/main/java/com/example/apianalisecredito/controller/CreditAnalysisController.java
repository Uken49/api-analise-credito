package com.example.apianalisecredito.controller;

import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.service.CreditAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit/analysis")
@RequiredArgsConstructor
public class CreditAnalysisController {

    private final CreditAnalysisService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditAnalysisResponse requestCreditAnalysis(
            @RequestBody CreditAnalysisRequest creditAnalysisRequest
    ) {
        return service.requestCreditAnalysis(creditAnalysisRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CreditAnalysisResponse listCreditAnalysis() {
        return null;
    }

    @GetMapping("/{idOrCpf}")
    @ResponseStatus(HttpStatus.OK)
    public CreditAnalysisResponse getCreditAnalysisById(@PathVariable String idOrCpf) {
        return service.getCreditAnalysisById(idOrCpf);
    }

}
