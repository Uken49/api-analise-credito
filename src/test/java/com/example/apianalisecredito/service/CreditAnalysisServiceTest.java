package com.example.apianalisecredito.service;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.apiClientDtoFactory;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.creditAnalysisRequestFactory;
import static com.example.apianalisecredito.factory.CreditAnalysisFactory.entityNotApprovedFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.apianalisecredito.apiclient.ApiClient;
import com.example.apianalisecredito.controller.request.CreditAnalysisRequest;
import com.example.apianalisecredito.controller.response.CreditAnalysisResponse;
import com.example.apianalisecredito.handler.exception.ClientNotFoundException;
import com.example.apianalisecredito.handler.exception.CreditAnalysisNotFoundException;
import com.example.apianalisecredito.mapper.CreditAnalysisMapper;
import com.example.apianalisecredito.mapper.CreditAnalysisMapperImpl;
import com.example.apianalisecredito.repository.CreditAnalysisRepository;
import com.example.apianalisecredito.repository.entity.CreditAnalysisEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.Disabled;
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
    @Captor ArgumentCaptor<CreditAnalysisEntity> creditAnalysisEntityArgumentCaptor;

    @Test
    void should_refuse_credit_analysis_with_requestetAmount_greater_than_monthlyIncome() {
        final CreditAnalysisRequest creditAnalysisRequest = creditAnalysisRequestFactory();
        final CreditAnalysisEntity creditAnalysisEntity = entityNotApprovedFactory();

        when(apiClient.getClientByIdOrCpf(idArgumentCaptor.capture())).thenReturn(apiClientDtoFactory());
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
    @Disabled("Working")
    void deve_retornar_exception_quando_idCliente_nao_for_encontrado(){
        final CreditAnalysisRequest creditAnalysisRequest = creditAnalysisRequestFactory();

        when(apiClient.getClientByIdOrCpf(idArgumentCaptor.capture()))
                .thenThrow(
                        new ClientNotFoundException("Cliente com id: %s não foi encontrado"
                                .formatted("234c443e-5853-4830-8f1b-dd55ca65c48a"))
                );

        assertThrows(ClientNotFoundException.class, () -> service.requestCreditAnalysis(creditAnalysisRequest));
    }
}