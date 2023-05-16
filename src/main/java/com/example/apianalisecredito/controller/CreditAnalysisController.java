package com.example.apianalisecredito.controller;

import com.example.apianalisecredito.controller.request.AnalysisCreditRequest;
import com.example.apianalisecredito.controller.response.AnalysisCreditResponse;
import com.example.apianalisecredito.service.CreditAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/credit/analysis")
@RequiredArgsConstructor
public class CreditAnalysisController {

    private final CreditAnalysisService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnalysisCreditResponse requestCreditAnalysis(
            @RequestBody AnalysisCreditRequest dataToAnalyze
    ){
        return service.requestCreditAnalysis(dataToAnalyze);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AnalysisCreditResponse listCreditAnalysis(){
        return null;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AnalysisCreditResponse getCreditAnalysisById(@PathVariable UUID id){
        return null;
    }

    @GetMapping("/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    public AnalysisCreditResponse getCreditAnalysisByCpf(@PathVariable Integer cpf){
        return null;
    }
}
