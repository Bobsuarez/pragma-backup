package co.com.pragma.exception.commands.handlers.impl;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;
import java.util.stream.Collectors;

import static co.com.pragma.exception.utils.ResponseUtils.getErrorDTOFromMap;

@Slf4j
@Component
public class WebExchangeBindExceptionHandler implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable exception) {
        final WebExchangeBindException ex = (WebExchangeBindException) exception;

        log.info("WebExchangeBindException errors: {}", ex.getFieldErrors());

        final Map<String, String> errors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                                 FieldError::getField,
                                 FieldError::getDefaultMessage
                         )
                );

        log.info("WebExchangeBindException handled errors: {}", errors);

        return ResponseDTO.builder()
                .code(ex.getStatusCode()
                              .value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .error(getErrorDTOFromMap(errors))
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return WebExchangeBindException.class;
    }
}