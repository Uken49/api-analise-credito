package com.example.apianalisecredito.util;

import com.example.apianalisecredito.controller.CreditAnalysisController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditAnalysisController.class);

    public LoggerUtil() throws IllegalAccessException {
        throw new IllegalAccessException("Class is utility");
    }

    public static void logInfo(String message, Class<?> c) {
        LOGGER.info(message, c);
    }

    public static void logError(String message, Class<?> c) {
        LOGGER.error(message, c);
    }

}
