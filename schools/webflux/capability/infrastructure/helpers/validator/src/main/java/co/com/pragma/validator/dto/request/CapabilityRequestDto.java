package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la creación de una nueva Capacidad")
public class CapabilityRequestDto {

    @Schema(
            description = "Nombre de la capacidad",
            example = "Desarrollo Backend",
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "El nombre de la tecnología es obligatorio")
    @Size(max = 50, message = "El nombre de la tecnología no puede superar los 50 caracteres")
    public String name;

    @Schema(
            description = "Descripción detallada de la capacidad",
            example = "Capacidad para desarrollar aplicaciones backend usando tecnologías modernas",
            maxLength = 90,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "La descripción de la tecnología es obligatoria")
    @Size(max = 90, message = "La descripción de la tecnología no puede superar los 90 caracteres")
    public String description;

    @Schema(
            description = "Lista de IDs de tecnologías asociadas a la capacidad. Debe contener entre 2 y 20 tecnologías, sin duplicados",
            example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Es necesario una tecnología asociada")
    @Size(min = 3, max = 20, message = "Se requieren al menos tres maximo 20 tecnologías asociadas")
    public List<@NotNull(message = "El id de tecnología no puede ser nulo") Long> technologyIds;
}


