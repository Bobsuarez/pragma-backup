package co.com.pragma.exception.commands.handlers.impl;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.DecapsulateException;

@Component
public class DefaultExceptionHandler implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable exception) {
        return ResponseDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return Exception.class;
    }
}
