package com.example.apianalisecredito.apiclient;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ApiClient", url = "localhost:8080/clients")
public interface ApiClient {

    @GetMapping("/{id}")
    ApiClientDto getClientById(UUID id);

    @GetMapping("/{cpf}")
    ApiClientDto getClientByCpf(Integer cpf);

}
