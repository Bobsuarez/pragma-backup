package co.com.pragma.exception.commands.handlers.impl;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

import static co.com.pragma.exception.utils.ResponseUtils.getErrorDTOMap;

@Component
public class AuthorizationDeniedExceptionHandler implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable exception) {

        AuthorizationDeniedException ex = (AuthorizationDeniedException) exception;

        return ResponseDTO.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                .error(getErrorDTOMap(ex.getClass().getSimpleName(), ex.getMessage()))
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return AuthorizationDeniedException.class;
    }
}