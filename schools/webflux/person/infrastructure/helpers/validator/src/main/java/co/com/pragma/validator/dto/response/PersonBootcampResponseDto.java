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
@Schema(description = "DTO de respuesta con la información de la inscripción de una persona en un bootcamp")
public class PersonBootcampResponseDto {
    
    @Schema(description = "ID de la inscripción", example = "1")
    private Long id;
    
    @Schema(description = "ID de la persona inscrita", example = "1")
    private Long personId;
    
    @Schema(description = "ID del bootcamp", example = "2")
    private Long bootcampId;
    
    @Schema(description = "Información detallada del bootcamp")
    private BootcampResponseDto bootcamp;
}

