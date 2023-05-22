package com.example.apianalisecredito.apiclient;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "apiClient", url = "http://localhost:8080/clients")
public interface ApiClient {

    @GetMapping("/{idOrCpf}")
    ApiClientDto getClientByIdOrCpf(@PathVariable String idOrCpf);

}
