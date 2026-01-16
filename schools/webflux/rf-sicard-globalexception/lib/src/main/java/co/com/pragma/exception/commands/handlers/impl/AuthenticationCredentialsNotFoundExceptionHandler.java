package co.com.pragma.exception.commands.handlers.impl;//package co.com.pragma.exception.commands.handlers.impl;


import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import static co.com.pragma.exception.utils.ResponseUtils.getErrorDTOMap;

@Component
public class AuthenticationCredentialsNotFoundExceptionHandler implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable exception) {
        AuthenticationCredentialsNotFoundException ex = (AuthenticationCredentialsNotFoundException) exception;

        return ResponseDTO.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .error(getErrorDTOMap(ex.getClass().getSimpleName(), ex.getMessage()))
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return AuthenticationCredentialsNotFoundException.class;
    }
}
