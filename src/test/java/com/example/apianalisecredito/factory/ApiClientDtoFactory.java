package com.example.apianalisecredito.factory;

import com.example.apianalisecredito.apiclient.dto.ApiClientDto;
import java.util.UUID;

public class ApiClientDtoFactory {

    public static ApiClientDto dtoWithId() {
        return new ApiClientDto(UUID.fromString("234c443e-5853-4830-8f1b-dd55ca65c48a"));
    }

}
