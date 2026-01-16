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
@Schema(description = "Información de un bootcamp con sus capacidades y tecnologías")
public class BootcampListResponseDto {
    
    @Schema(description = "ID del bootcamp", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del bootcamp", example = "Bootcamp Full Stack")
    private String name;
    
    @Schema(description = "Descripción del bootcamp", example = "Bootcamp completo de desarrollo Full Stack")
    private String description;
    
    @Schema(description = "Lista de capacidades asociadas al bootcamp")
    private List<CapabilityResponseDto> capabilities;

}

