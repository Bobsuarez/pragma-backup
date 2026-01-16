package co.com.pragma.exception.commands.handlers.impl;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebInputException;

import static co.com.pragma.exception.utils.ResponseUtils.getErrorDTOMap;

@Component
public class ServerWebInputExceptionHandler implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable exception) {
        ServerWebInputException ex = (ServerWebInputException) exception;

        return ResponseDTO.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .error(getErrorDTOMap( ex.getClass().getSimpleName(), ex.getMessage()))
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return ServerWebInputException.class;
    }
}
