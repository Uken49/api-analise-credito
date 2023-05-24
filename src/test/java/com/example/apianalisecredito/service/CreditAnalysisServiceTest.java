package com.example.apianalisecredito.service;

import static com.example.apianalisecredito.factory.ApiClientDtoFactory.*;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.entityApproved;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.entityNotApproved;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.requestAmountGreaterThanMonthlyIncome;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.requestAmountLessThanMonthlyIncome;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.factory.ApiClientDtoFactory;
import com.example.apianalisecredito.factory.CreditAnalysisFactory;
import com.example.apianalisecredito.handler.exception.ClientNotFoundException;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.mapper.CreditAnalysisMapperImpl;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import feign.FeignException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreditAnalysisServiceTest {

    @Mock
    private CreditAnalysisRepository repository;
    @Mock
    private ApiClient apiClient;
    @Spy
    private CreditAnalysisMapper mapper = new CreditAnalysisMapperImpl();
    @InjectMocks
    private CreditAnalysisService service;
    @Captor
    private ArgumentCaptor<String> idArgumentCaptor;
    @Captor
    private ArgumentCaptor<CreditAnalysisEntity> creditAnalysisEntityArgumentCaptor;


    @Test
    void deve_verificar_a_integridade_dos_dados_durante_a_analise_de_credito() {
        final CreditAnalysisRequest creditAnalysisRequest = requestAmountLessThanMonthlyIncome();
        final CreditAnalysisEntity creditAnalysisEntity = entityApproved();
        final ApiClientDto apiClientDto = dtoWithId();

        when(apiClient.getClientByIdOrCpf(idArgumentCaptor.capture())).thenReturn(apiClientDto);
        when(repository.save(creditAnalysisEntityArgumentCaptor.capture())).thenReturn(creditAnalysisEntity);

        final CreditAnalysisResponse creditAnalysisResponse = service.requestCreditAnalysis(creditAnalysisRequest);
        final CreditAnalysisEntity creditAnalysisEntityCapture = creditAnalysisEntityArgumentCaptor.getValue();

        assertNotNull(creditAnalysisResponse);
        assertEquals(idArgumentCaptor.getValue(), creditAnalysisRequest.clientId().toString());
        assertEquals(creditAnalysisRequest.clientId(), creditAnalysisEntityCapture.getClientId());
        assertEquals(creditAnalysisRequest.monthlyIncome(), creditAnalysisEntityCapture.getMonthlyIncome());
        assertEquals(creditAnalysisRequest.requestedAmount(), creditAnalysisEntityCapture.getRequestedAmount());

        assertEquals(creditAnalysisRequest.clientId(), creditAnalysisResponse.clientId());
        assertTrue(creditAnalysisResponse.approved());
        assertEquals(BigDecimal.valueOf(600.00), creditAnalysisResponse.approvedLimit());
        assertEquals(BigDecimal.valueOf(60), creditAnalysisResponse.withdraw());
        assertEquals(BigDecimal.valueOf(15), creditAnalysisResponse.annualInterest());
    }

    @Test
    void should_refuse_credit_analysis_with_requestetAmount_greater_than_monthlyIncome() {
        final CreditAnalysisRequest creditAnalysisRequest = requestAmountGreaterThanMonthlyIncome();
        final CreditAnalysisEntity creditAnalysisEntity = entityNotApproved();
        final ApiClientDto apiClientDto = dtoWithId();

        when(apiClient.getClientByIdOrCpf(idArgumentCaptor.capture())).thenReturn(apiClientDto);
        when(repository.save(creditAnalysisEntityArgumentCaptor.capture())).thenReturn(creditAnalysisEntity);

        final CreditAnalysisResponse creditAnalysisResponse = service.requestCreditAnalysis(creditAnalysisRequest);
        final CreditAnalysisEntity creditAnalysisEntityCapture = creditAnalysisEntityArgumentCaptor.getValue();

        assertNotNull(creditAnalysisResponse);
        assertEquals(idArgumentCaptor.getValue(), creditAnalysisRequest.clientId().toString());
        assertEquals(creditAnalysisRequest.clientId(), creditAnalysisEntityCapture.getClientId());
        assertEquals(creditAnalysisRequest.monthlyIncome(), creditAnalysisEntityCapture.getMonthlyIncome());
        assertEquals(creditAnalysisRequest.requestedAmount(), creditAnalysisEntityCapture.getRequestedAmount());

        assertEquals(creditAnalysisRequest.clientId(), creditAnalysisResponse.clientId());
        assertFalse(creditAnalysisResponse.approved());
        assertEquals(BigDecimal.ZERO, creditAnalysisResponse.approvedLimit());
        assertEquals(BigDecimal.ZERO, creditAnalysisResponse.withdraw());
        assertEquals(BigDecimal.valueOf(15), creditAnalysisResponse.annualInterest());
    }

    @Test
    void should_throw_ClientNotFoundException_when_idCliente_is_not_found() {
        final CreditAnalysisRequest creditAnalysisRequest = requestAmountGreaterThanMonthlyIncome();

        when(apiClient.getClientByIdOrCpf(idArgumentCaptor.capture())).thenThrow(FeignException.class);

        assertThrows(ClientNotFoundException.class,
                () -> service.requestCreditAnalysis(creditAnalysisRequest));
    }
}