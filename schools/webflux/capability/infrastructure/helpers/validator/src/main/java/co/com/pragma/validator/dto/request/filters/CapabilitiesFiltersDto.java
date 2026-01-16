package co.com.pragma.validator.dto.request.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Filtros para consultar capacidades")
public class CapabilitiesFiltersDto {

    @Schema(
            description = "Cantidad de tecnologías que debe tener la capacidad",
            example = "3",
            minimum = "1",
            maximum = "100",
            required = true
    )
    @NotNull(message = "La cantidad de registros es obligatoria")
    @Min(value = 1, message = "Debe solicitar al menos 1 registro")
    @Max(value = 100, message = "No puede solicitar más de 100 registros por página")
    @JsonProperty("cantidadRegistros")
    private Integer capabilityAmount;

    @Schema(
            description = "Nombre de la capacidad a filtrar (solo letras)",
            example = "Java",
            pattern = "^[a-zA-Z]*$"
    )
    @JsonProperty("nombreCapacidad")
    private String capabilityName;

    @Schema(
            description = "Orden de los resultados: ASC (ascendente) o DESC (descendente)",
            example = "ASC",
            pattern = "^(ASC|DESC)$",
            defaultValue = "ASC"
    )
    @Builder.Default
    @Pattern(regexp = "^(ASC|DESC)$",
            message = "El orden debe ser 'ASC' o 'DESC'")
    private String orderBy = "ASC";
}

