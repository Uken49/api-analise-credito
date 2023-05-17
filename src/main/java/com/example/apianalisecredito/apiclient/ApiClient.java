package com.example.apianalisecredito.apiclient;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "apiClient", url = "http://localhost:8080/clients")
public interface ApiClient {

    @GetMapping("/{id}")
    ApiClientDto getClientById(@PathVariable UUID id);

    @GetMapping("/{cpf}")
    ApiClientDto getClientByCpf(@PathVariable Integer cpf);

}
