package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un nuevo bootcamp")
public class BootcampRequestDto {

    @Schema(description = "Nombre del bootcamp", example = "Bootcamp Full Stack Developer", required = true)
    @NotBlank(message = "El nombre del bootcamp es requerido")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre 1 y 255 caracteres")
    private String name;

    @Schema(description = "Descripción del bootcamp", example = "Bootcamp completo para desarrolladores full stack con tecnologías modernas", required = true)
    @NotBlank(message = "La descripción del bootcamp es requerida")
    private String description;

    @Schema(description = "Fecha de lanzamiento del bootcamp", example = "2024-03-15", required = true)
    @NotNull(message = "La fecha de lanzamiento es requerida")
    private String launchDate;

    @Schema(description = "Duración del bootcamp en meses", example = "6", minimum = "1", maximum = "120", required = true)
    @NotNull(message = "La duración en meses es requerida")
    @Min(value = 1, message = "La duración debe ser al menos 1 mes")
    @Max(value = 120, message = "La duración no puede exceder 120 meses")
    private Integer durationMonths;

    @Schema(description = "Lista de capacidades asociadas al bootcamp (mínimo 1, máximo 4)", required = true)
    @NotNull(message = "Las capacidades son requeridas")
    @NotEmpty(message = "Debe tener al menos una capacidad asociada")
    @Size(min = 1, max = 4, message = "Debe tener entre 1 y 4 capacidades asociadas")
    @Valid
    private List<CapabilityDto> capabilities;
}

