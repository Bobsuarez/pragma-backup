package co.com.pragma.validator.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO de respuesta con la información de una persona")
public class PersonResponseDto {
    
    @Schema(description = "ID de la persona", example = "15")
    private Long id;
    
    @Schema(description = "Nombre de la persona", example = "Eduardo Suarez")
    private String name;
    
    @Schema(description = "Email de la persona", example = "eduar.suarez001@gmail.com")
    private String email;
}
