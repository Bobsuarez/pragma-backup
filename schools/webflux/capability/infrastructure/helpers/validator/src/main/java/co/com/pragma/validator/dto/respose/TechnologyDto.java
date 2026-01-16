package co.com.pragma.validator.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Registro de Tecnology con su listado de tecnologias")
public class TechnologyDto {

    @Schema(description = "Identificador único de la tecnologia", example = "1")
    private Long id;

    @Schema(description = "Nombre de la tecnologia", example = "Java Developer")
    private String name;
}
