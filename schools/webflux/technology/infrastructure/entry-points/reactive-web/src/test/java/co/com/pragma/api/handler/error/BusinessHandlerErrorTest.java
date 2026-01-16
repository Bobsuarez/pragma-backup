package co.com.pragma.api.handler.error;

import co.com.pragma.exception.dto.ResponseDTO;
import co.com.pragma.model.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessHandlerErrorTest {

    private BusinessHandlerError businessHandlerError;

    @BeforeEach
    void setUp() {
        businessHandlerError = new BusinessHandlerError();
    }

    @Test
    void shouldHandleBusinessException() {
        // Given
        String errorMessage = "El nombre de la tecnología ya existe";
        BusinessException exception = new BusinessException(errorMessage);

        // When
        ResponseDTO<Object> response = businessHandlerError.handleException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getError()).isNotNull();
    }

    @Test
    void shouldReturnBusinessExceptionClass() {
        // When
        Class<? extends Throwable> exceptionClass = businessHandlerError.getExceptionClass();

        // Then
        assertThat(exceptionClass).isEqualTo(BusinessException.class);
    }

    @Test
    void shouldHandleExceptionWithNullMessage() {
        // Given
        BusinessException exception = new BusinessException(null);

        // When
        ResponseDTO<Object> response = businessHandlerError.handleException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).isNull();
        assertThat(response.getError()).isNotNull();
    }
}

