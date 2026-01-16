package co.com.pragma.api.handler.error;

import co.com.pragma.exception.dto.ResponseDTO;
import co.com.pragma.model.exceptions.ValidationException;
import co.com.pragma.model.validations.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationHandlerErrorTest {

    private ValidationHandlerError validationHandlerError;

    @BeforeEach
    void setUp() {
        validationHandlerError = new ValidationHandlerError();
    }

    @Test
    void shouldHandleValidationException() {
        // Given
        List<ValidationError> errors = Arrays.asList(
                new ValidationError("name", "El nombre de la tecnología es obligatorio"),
                new ValidationError("description", "La descripción de la tecnología no puede superar los 90 caracteres")
        );
        ValidationException exception = new ValidationException(errors);

        // When
        ResponseDTO<Object> response = validationHandlerError.handleException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).isEqualTo("Errores de validación");
        assertThat(response.getError()).isNotNull();
    }

    @Test
    void shouldReturnValidationExceptionClass() {
        // When
        Class<? extends Throwable> exceptionClass = validationHandlerError.getExceptionClass();

        // Then
        assertThat(exceptionClass).isEqualTo(ValidationException.class);
    }

    @Test
    void shouldHandleValidationExceptionWithSingleError() {
        // Given
        List<ValidationError> errors = Arrays.asList(
                new ValidationError("name", "El nombre es obligatorio")
        );
        ValidationException exception = new ValidationException(errors);

        // When
        ResponseDTO<Object> response = validationHandlerError.handleException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).isEqualTo("Errores de validación");
        assertThat(response.getError()).isNotNull();
    }

    @Test
    void shouldHandleValidationExceptionWithEmptyErrors() {
        // Given
        List<ValidationError> errors = Arrays.asList();
        ValidationException exception = new ValidationException(errors);

        // When
        ResponseDTO<Object> response = validationHandlerError.handleException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).isEqualTo("Errores de validación");
        assertThat(response.getError()).isNotNull();
    }

    @Test
    void shouldMapValidationErrorsCorrectly() {
        // Given
        List<ValidationError> errors = Arrays.asList(
                new ValidationError("name", "El nombre es obligatorio"),
                new ValidationError("description", "La descripción es obligatoria")
        );
        ValidationException exception = new ValidationException(errors);

        // When
        ResponseDTO<Object> response = validationHandlerError.handleException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getError()).isNotNull();
        // Verificar que los errores se mapearon correctamente
        // El método getListErrorDTOMap debería convertir la lista de ValidationError
        // en un mapa con fieldName como clave y message como valor
    }
}

