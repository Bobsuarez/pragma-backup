package co.com.pragma.api.handler.error;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static co.com.pragma.exception.utils.ResponseUtils.getErrorDTOMap;

@Slf4j
@Component
public class DuplicateKeysHandlerError implements ExceptionHandlerRegistry {

    @Override
    public ResponseDTO<Object> handleException(Throwable ex) {

        DuplicateKeyException duplicateKeyException = (DuplicateKeyException) ex;

        log.error("DuplicateKeysHandlerError: Handling DuplicateKeyException - {}", duplicateKeyException.getMessage());

        return ResponseDTO.builder()
                .code(HttpStatus.PRECONDITION_FAILED.value())
                .message("La data enviada ya se encuentra registrada en el sistema")
                .error(getErrorDTOMap(
                        ex.getClass()
                                .getSimpleName(), ex.getMessage()
                ))
                .build();
    }

    @Override
    public Class<? extends Throwable> getExceptionClass() {
        return DuplicateKeyException.class;
    }
}
