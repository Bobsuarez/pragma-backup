package co.com.pragma.api.handler.error;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static co.com.pragma.exception.utils.ResponseUtils.getErrorDTOMap;

@Component
public class DuplicateKeysHandlerError implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable ex) {

        DuplicateKeyException exception = (DuplicateKeyException) ex;

        return ResponseDTO.builder()
                .code(HttpStatus.PRECONDITION_FAILED.value())
                .message("La data enviada ya se encuentra registrada en el sistema")
                .error(getErrorDTOMap(ex.getClass().getSimpleName(), ex.getMessage()))
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return DuplicateKeyException.class;
    }
}
