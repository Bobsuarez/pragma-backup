package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para inscribir una persona en un bootcamp")
public class PersonBootcampRequestDto {
    
    @NotNull(message = "El campo personId es requerido")
    @Schema(description = "ID de la persona a inscribir", example = "1", required = true)
    private Long personId;
    
    @NotNull(message = "El campo bootcampId es requerido")
    @Schema(description = "ID del bootcamp en el que se inscribirá la persona", example = "2", required = true)
    private Long bootcampId;
}

