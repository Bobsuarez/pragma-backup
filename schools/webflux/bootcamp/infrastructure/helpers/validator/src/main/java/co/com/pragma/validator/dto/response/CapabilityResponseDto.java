package co.com.pragma.validator.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una capacidad")
public class CapabilityResponseDto {
    
    @Schema(description = "ID de la capacidad", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la capacidad", example = "Desarrollo Frontend")
    private String name;

    @Schema(description = "Lista de tecnologías asociadas al bootcamp")
    private List<TechnologyResponseDto> technologies;

}

