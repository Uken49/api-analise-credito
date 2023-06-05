package com.example.apianalisecredito.apiclient;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "apiClient", url = "http://localhost:8080/clients")
public interface ApiClient {

    // Aqui o path varialbe deve ser referente ao recurso, no caso cliente, e é estraho um atributo representar tanto um id como um cpf
    // Como a api do cliente sabe se esta recebendo um id ou um cpf?
    @GetMapping("/{idOrCpf}")
    ApiClientDto getClientByIdOrCpf(@PathVariable String idOrCpf);

}
