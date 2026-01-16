package co.com.pragma.validator.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta con la información de la tecnología registrada")
public class TechnologyResponseDto {

    @Schema(description = "Identificador único de la tecnología", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Nombre de la tecnología", example = "Java")
    private String name;

    @Schema(description = "Descripción de la tecnología", example = "Lenguaje de programación orientado a objetos")
    private String description;
}


