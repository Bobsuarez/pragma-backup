package co.com.pragma.validator.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "DTO de respuesta simplificado con id y name de la tecnología")
public class TechnologySimpleResponseDto {

    @Schema(description = "Identificador único de la tecnología", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Nombre de la tecnología", example = "Java")
    private String name;
}
