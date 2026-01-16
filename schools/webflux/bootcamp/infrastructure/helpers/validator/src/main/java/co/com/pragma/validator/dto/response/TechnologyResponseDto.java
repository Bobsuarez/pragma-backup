package co.com.pragma.validator.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Información de una tecnología")
public class TechnologyResponseDto {
    
    @Schema(description = "ID de la tecnología", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de la tecnología", example = "Java")
    private String name;
}

