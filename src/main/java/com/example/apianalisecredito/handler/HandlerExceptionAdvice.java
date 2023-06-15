package com.example.apianalisecredito.handler;

import com.example.apianalisecredito.handler.exception.ClientNotFoundException;
import com.example.apianalisecredito.handler.exception.CreditAnalysisNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class HandlerExceptionAdvice {

    private ProblemDetail builderProblemDetail(String title, HttpStatus status, String detail) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);

        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(""));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ProblemDetail clientNotFoundExceptionHandler(ClientNotFoundException cnfe) {

        return builderProblemDetail("Cliente não encontrado", HttpStatus.UNPROCESSABLE_ENTITY, cnfe.getMessage());
    }

    @ExceptionHandler(CreditAnalysisNotFoundException.class)
    public ProblemDetail creditAnalysisNotFoundHandler(CreditAnalysisNotFoundException canfe) {

        return builderProblemDetail("Análise não encontrada", HttpStatus.NOT_FOUND, canfe.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail methodArgumentTypeMismatchHandler(MethodArgumentTypeMismatchException matme) {

        return builderProblemDetail("Incompatibilidade no tipo de argumento", HttpStatus.BAD_REQUEST, matme.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgumentNotValidHandler(MethodArgumentNotValidException manve) {

        final String detail = manve.getFieldError().getField() + " " + manve.getFieldError().getDefaultMessage();

        return builderProblemDetail("Argumento inválido", HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail constraintViolationHandler(ConstraintViolationException cve) {

        return builderProblemDetail("Informação inválida", HttpStatus.BAD_REQUEST, cve.getMessage());
    }
}
