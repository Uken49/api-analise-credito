package com.example.apianalisecredito.apiclient;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "apiClient", url = "${url.apiclient}")
public interface ApiClient {

    @GetMapping("/{id}")
    ApiClientDto getClientById(@PathVariable UUID id);

    @GetMapping
    List<ApiClientDto> getClientByCpf(@RequestParam("cpf") String cpf);
}
