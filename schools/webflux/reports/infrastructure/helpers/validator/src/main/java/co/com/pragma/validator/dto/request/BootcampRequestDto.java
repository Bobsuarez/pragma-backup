package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
@Schema(description = "DTO para crear un nuevo bootcamp")
public class BootcampRequestDto {

    private Integer id;
    
    @Schema(description = "Nombre del bootcamp", example = "Bootcamp Full Stack Developer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre 1 y 255 caracteres")
    private String name;
    
    @Schema(description = "Descripción del bootcamp", example = "Bootcamp completo para desarrolladores full stack", maxLength = 1000)
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    @Schema(description = "Fecha de lanzamiento del bootcamp", example = "2024-03-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La fecha de lanzamiento es requerida")
    private String launchDate;
    
    @Schema(description = "Duración del bootcamp en meses", example = "6", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "La duración en meses es requerida")
    @Positive(message = "La duración en meses debe ser un número positivo")
    private Integer durationMonths;
}

