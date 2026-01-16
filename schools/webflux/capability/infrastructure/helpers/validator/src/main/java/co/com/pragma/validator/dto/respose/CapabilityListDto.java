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
@Schema(description = "Registro de capacidades con su listado de tecnologias")
public class CapabilityListDto {

    @Schema(description = "Identificador único de la capacidad", example = "1")
    private Long id;

    @Schema(description = "Nombre de la capacidad", example = "Java Developer")
    private String name;
}
