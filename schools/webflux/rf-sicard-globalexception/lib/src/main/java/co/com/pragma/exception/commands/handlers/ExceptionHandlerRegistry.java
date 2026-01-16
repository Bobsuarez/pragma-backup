package co.com.pragma.exception.commands.handlers;

import co.com.pragma.exception.dto.ResponseDTO;

public interface ExceptionHandlerRegistry {

    ResponseDTO<Object> handleException(Throwable exception);

    /**
     * Define qué clase de excepción maneja este handler.
     */
    Class<? extends Throwable> getExceptionClass();
}
