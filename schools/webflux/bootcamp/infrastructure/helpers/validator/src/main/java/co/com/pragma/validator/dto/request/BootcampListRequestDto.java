package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Schema(description = "DTO para listar bootcamps con paginación y ordenamiento")
public class BootcampListRequestDto {
    
    @Schema(description = "Número de página (inicia en 0)", example = "0", minimum = "0", defaultValue = "0")
    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    private Integer page = 0;

    @Schema(description = "Tamaño de la página", example = "10", minimum = "1", maximum = "100", defaultValue = "10")
    @Min(value = 1, message = "El tamaño debe ser mayor o igual a 1")
    @Max(value = 100, message = "El tamaño debe ser menor o igual a 100")
    private Integer size = 10;

    @Schema(description = "Campo por el cual ordenar", example = "NAME", allowableValues = {"NAME", "CAPABILITIES_COUNT"}, defaultValue = "NAME")
    private String sortField = "NAME"; // NAME o CAPABILITIES_COUNT

    @Schema(description = "Dirección del ordenamiento", example = "ASC", allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
    private String sortDirection = "ASC"; // ASC o DESC
}

