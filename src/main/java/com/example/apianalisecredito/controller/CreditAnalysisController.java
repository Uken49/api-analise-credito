package com.example.apianalisecredito.controller;

import com.example.apianalisecredito.controller.request.AnalysisCreditRequest;
import com.example.apianalisecredito.controller.response.AnalysisCreditResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/credit/analysis")
public class CreditAnalysisController {

    @PostMapping
    public AnalysisCreditResponse requestCreditAnalysis(
            @RequestBody AnalysisCreditRequest analysisCreditRequest
    ){
        return null;
    }

    @GetMapping
    public AnalysisCreditResponse listCreditAnalysis(){
        return null;
    }

    @GetMapping("/{id}")
    public AnalysisCreditResponse getCreditAnalysisById(@PathVariable UUID id){
        return null;
    }
    @GetMapping("/{cpf}")
    public AnalysisCreditResponse getCreditAnalysisByCpf(@PathVariable Integer cpf){
        return null;
    }
}
