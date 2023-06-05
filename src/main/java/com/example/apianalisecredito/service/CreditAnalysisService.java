package com.example.apianalisecredito.service;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.handler.exception.ClientNotFoundException;
import com.example.apianalisecredito.handler.exception.CreditAnalysisNotFoundException;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.model.CreditAnalysisModel;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import com.example.apianalisecredito.util.LoggerUtil;
import feign.FeignException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditAnalysisService {

    private final CreditAnalysisRepository repository;
    private final CreditAnalysisMapper mapper;
    private final ApiClient apiClient;
    // Business rules

    // Nomes de constrantes fora do padrão uppercase
    private final BigDecimal limitPercentageToWithdraw = BigDecimal.valueOf(0.1);
    private final BigDecimal interestPerYearPercentage = BigDecimal.valueOf(15, 0);
    private final BigDecimal maxAmountOfMonthlyIncomeConsidered = BigDecimal.valueOf(50_000_00, 2);
     // melhor colocar no nome apenas o que o campo significa, caso contrario fica confuso
    private final BigDecimal ifRequestedValueIsGreaterThan50 = BigDecimal.valueOf(0.15);
    private final BigDecimal ifTheRequestedValueIsLessThanOrEqual50 = BigDecimal.valueOf(0.3);
    private final BigDecimal percentageForCreditAnalysis = BigDecimal.valueOf(0.5);
    // Esta constante é necessaria?
    private final int equalToHalfTheValue = 0;
    private final int decimalScale = 2;

    public CreditAnalysisResponse creditAnalysis(CreditAnalysisRequest creditAnalysisRequest) {
        // Este log é desnecessario
        LoggerUtil.logInfo("Mapeando \"request\" para \"model\"", this.getClass());
        final CreditAnalysisModel creditAnalysisModel = mapper.fromModel(creditAnalysisRequest);
        final String analysisType = "id";

        //crie métodos especificos, isso deixa o codigo confuso e acoplado
        //consulta o que por id? se é por id pq passar o o tipo id?
        consultId(analysisType, creditAnalysisModel.clientId().toString());

        // Declare os atributos onde serão utilizados, facilita a leitura do codigo
        final CreditAnalysisModel creditAnalysisModelUpdated;
        final CreditAnalysisEntity creditAnalysisEntity;
        final CreditAnalysisEntity creditAnalysisEntitySaved;

        final BigDecimal requestedAmount = creditAnalysisModel.requestedAmount();
        final BigDecimal monthlyIncome = creditAnalysisModel.monthlyIncome();

        final boolean approved;
        final BigDecimal approvedLimit;
        final BigDecimal withdraw;
        final BigDecimal annualInterest;
        final BigDecimal monthlyIncomeConsideredValue;

        if (isRequestAmountGreaterThanMonthlyIncome(requestedAmount, monthlyIncome)) {
            // Este log é desnecessário
            LoggerUtil.logInfo("Análise não aprovada", this.getClass());
            approved = false;
            withdraw = BigDecimal.valueOf(0);
            annualInterest = BigDecimal.valueOf(0);
            approvedLimit = BigDecimal.valueOf(0);
        } else {
            // Este log é desnecessário
            LoggerUtil.logInfo("Análise aprovada", this.getClass());
            approved = true;
            monthlyIncomeConsideredValue = returnConsideredValueOfMonthlyIncome(monthlyIncome);
            approvedLimit = returnApprovedLimit(requestedAmount, monthlyIncomeConsideredValue);
            withdraw = returnWithdraw(approvedLimit);
            annualInterest = interestPerYearPercentage;
        }

        creditAnalysisModelUpdated = creditAnalysisModel.creditAnalysisUpdate(
                approved,
                approvedLimit.setScale(decimalScale, RoundingMode.HALF_UP),
                withdraw.setScale(decimalScale, RoundingMode.HALF_UP),
                annualInterest
        );

        // Este log é desnecessário
        LoggerUtil.logInfo("Mapeando \"model\" para \"entity\"", this.getClass());
        creditAnalysisEntity = mapper.fromEntity(creditAnalysisModelUpdated);

        // Este log é desnecessário
        LoggerUtil.logInfo("Usando método \"save\"", this.getClass());
        creditAnalysisEntitySaved = repository.save(creditAnalysisEntity);

        return mapper.fromResponse(creditAnalysisEntitySaved);
    }

    public List<CreditAnalysisResponse> getCreditAnalysisById(String identifier) {
        final int cpfSizeWithoutPunctuation = 11;
        final int cpfSizeWithPunctuation = 14;
        final List<CreditAnalysisEntity> creditAnalysisEntity;
        final UUID id;
        final String analysisType;

        // Não faça isso rs, toda essa logica é para o problema de um campo ter dois significados
        // não há uma validação de cpf
        if (identifier.length() == cpfSizeWithoutPunctuation || identifier.length() == cpfSizeWithPunctuation) {
            // Este log é desnecessario
            LoggerUtil.logInfo("Variável \"identifier\" é um CPF", this.getClass());
            analysisType = "cpf";
            final ApiClientDto clientById = consultId(analysisType, identifier);
            id = clientById.id();
        } else {
            // Este log é desnecessario
            LoggerUtil.logInfo("Variável \"identifier\" é um Id", this.getClass());
            analysisType = "id";
            // se receber um id invalido seu codigo vai quebrar aqui
            id = UUID.fromString(identifier);
        }

        // Este log é desnecessario
        LoggerUtil.logInfo("Variável \"identifier\" é um Id", this.getClass());

        // Este log é desnecessario
        LoggerUtil.logInfo("Buscando análises pelo ID do cliente", this.getClass());
        creditAnalysisEntity = repository.findAllByClientId(id);

        if (creditAnalysisEntity.isEmpty()) {
            LoggerUtil.logInfo("Buscando análise pelo ID da análise", this.getClass());
            // Se o id passado for da analise tera feito uma consulta por nada, uma das operações mais caras em um sistema é o IO
            creditAnalysisEntity.add(repository.findById(id).orElseThrow(() -> {
                LoggerUtil.logError("Lançando exception de análise não encontrada", this.getClass());
                throw new CreditAnalysisNotFoundException("Análise com %s: %s não foi encontrada".formatted(analysisType, identifier));
            }));
        }

        // É incorreto retornar uma lista quando é feita uma consulta pelo id da analise
        return creditAnalysisEntity.stream().map(mapper::fromResponse).toList();
    }

    public List<CreditAnalysisResponse> getAllCreditAnalysis() {
        // Este log é desnecessário
        LoggerUtil.logError("Buscando todas as análises", this.getClass());
        return repository.findAll().stream().map(mapper::fromResponse).toList();
    }

    private ApiClientDto consultId(String analysisType, String identifier) {
        try {
            // Este log é desnecessário
            LoggerUtil.logInfo("Consultando id do cliente na outra API", this.getClass());
            return apiClient.getClientByIdOrCpf(identifier);
        } catch (FeignException fe) {
            // Este log é desnecessário
            LoggerUtil.logError("Lançando exception de cliente não encontrado", this.getClass());
            throw new ClientNotFoundException("Cliente com %s: %s não foi encontrado".formatted(analysisType, identifier));
        }
    }

    private boolean isRequestAmountGreaterThanMonthlyIncome(BigDecimal requestedAmount, BigDecimal monthlyIncome) {
        // Este log é desnecessário
        LoggerUtil.logInfo("Analisando se \"requestAmount\" é maior que \"monthlyIncome\"", this.getClass());
        return requestedAmount.compareTo(monthlyIncome) > equalToHalfTheValue;
    }

    // Evite utilizar palavras reservadas nos nomes de metodos
    private BigDecimal returnConsideredValueOfMonthlyIncome(BigDecimal monthlyIncome) {
        final BigDecimal monthlyIncomeConsideredValue;
        // Aqui não precisa do if else
        if (monthlyIncome.compareTo(maxAmountOfMonthlyIncomeConsidered) > equalToHalfTheValue) {
            monthlyIncomeConsideredValue = maxAmountOfMonthlyIncomeConsidered;
        } else {
            monthlyIncomeConsideredValue = monthlyIncome;
        }

        // Este log é desnecessário
        LoggerUtil.logInfo("Valor de monthlyIncome para análise será: %s".formatted(monthlyIncomeConsideredValue), this.getClass());
        return monthlyIncomeConsideredValue;
    }

    // metodo calculaLimteCredito
    private BigDecimal returnApprovedLimit(BigDecimal requestedAmount, BigDecimal monthlyIncome) {
        final BigDecimal approvedLimit;

        if (requestedAmount.compareTo(monthlyIncome.multiply(percentageForCreditAnalysis)) > equalToHalfTheValue) {
            approvedLimit = monthlyIncome.multiply(ifRequestedValueIsGreaterThan50);
        } else {
            approvedLimit = monthlyIncome.multiply(ifTheRequestedValueIsLessThanOrEqual50);
        }
        // log desnecessario
        LoggerUtil.logInfo("Valor de approvedLimit é: %s".formatted(approvedLimit), this.getClass());
        return approvedLimit;
    }

    private BigDecimal returnWithdraw(BigDecimal approvedLimit) {
        final BigDecimal withdraw = approvedLimit.multiply(limitPercentageToWithdraw);
        // log desnecessario
        LoggerUtil.logInfo("Valor de withdraw é: %s".formatted(withdraw), this.getClass());
        return withdraw;
    }

}
