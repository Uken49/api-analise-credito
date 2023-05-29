package com.example.apianalisecredito.controller;

import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.service.CreditAnalysisService;
import com.example.apianalisecredito.util.LoggerUtil;
import java.util.List;
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
    private final String loggerServiceMessage = "Entrando na service";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditAnalysisResponse requestCreditAnalysis(
            @RequestBody CreditAnalysisRequest creditAnalysisRequest
    ) {
        LoggerUtil.logInfo("", this.getClass());
        return service.creditAnalysis(creditAnalysisRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CreditAnalysisResponse> getAllCreditAnalysis() {
        LoggerUtil.logInfo(loggerServiceMessage, this.getClass());
        return service.getAllCreditAnalysis();
    }

    @GetMapping("/{idOrCpf}")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditAnalysisResponse> getCreditAnalysisById(@PathVariable String idOrCpf) {
        LoggerUtil.logInfo(loggerServiceMessage, this.getClass());
        return service.getCreditAnalysisById(idOrCpf);
    }

}
