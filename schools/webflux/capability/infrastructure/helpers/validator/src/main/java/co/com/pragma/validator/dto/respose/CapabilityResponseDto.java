package co.com.pragma.validator.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "DTO de respuesta con la información de una Capacidad registrada")
public class CapabilityResponseDto {

    @Schema(
            description = "Identificador único de la capacidad",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String id;

    @Schema(
            description = "Nombre de la capacidad",
            example = "Desarrollo Backend",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String name;

    @Schema(
            description = "Descripción detallada de la capacidad",
            example = "Capacidad para desarrollar aplicaciones backend usando tecnologías modernas",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String description;
}


