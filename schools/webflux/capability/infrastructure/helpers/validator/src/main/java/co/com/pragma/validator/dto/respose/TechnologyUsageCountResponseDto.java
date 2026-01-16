package co.com.pragma.validator.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "DTO de respuesta con el conteo de uso de una Tecnología")
public class TechnologyUsageCountResponseDto {

    @Schema(
            description = "Identificador único de la tecnología",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long technologyId;

    @Schema(
            description = "Número de veces que la tecnología está siendo utilizada (en capacidades)",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long usageCount;
}
