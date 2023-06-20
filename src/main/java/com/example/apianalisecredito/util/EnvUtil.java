package com.example.apianalisecredito.util;

import org.springframework.util.StringUtils;

public class EnvUtil {

    public static String getEnvOrError(String varName) {
        final String value = System.getenv(varName);
        if (StringUtils.hasText(value)) {
            return value;
        }
        throw new IllegalStateException("Váriavel de ambiente (" + varName + ") não encontrada");
    }

    public static String getEnvOrDefault(String varName, String defaultValue) {
        final String value = System.getenv(varName);
        if (StringUtils.hasText(value)) {
            return value;
        }
        return defaultValue;
    }
}
