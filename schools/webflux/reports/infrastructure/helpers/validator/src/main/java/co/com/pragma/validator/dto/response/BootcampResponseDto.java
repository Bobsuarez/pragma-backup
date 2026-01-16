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
@Schema(description = "DTO de respuesta con la información del bootcamp creado")
public class BootcampResponseDto {
    
    @Schema(description = "ID único del bootcamp", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del bootcamp", example = "Bootcamp Full Stack Developer")
    private String name;
    
    @Schema(description = "Descripción del bootcamp", example = "Bootcamp completo para desarrolladores full stack")
    private String description;
    
    @Schema(description = "Fecha de lanzamiento del bootcamp", example = "2024-03-01")
    private String launchDate;
    
    @Schema(description = "Duración del bootcamp en meses", example = "6")
    private Integer durationMonths;
}

