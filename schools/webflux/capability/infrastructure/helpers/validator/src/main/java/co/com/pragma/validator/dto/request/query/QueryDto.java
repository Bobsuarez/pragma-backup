package co.com.pragma.validator.dto.request.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto que representa los parámetros para realizar una consulta paginada")
public class QueryDto<F>  {

    @Schema(
            description = "Número de página a consultar. Debe ser mayor o igual a 1.",
            example = "1",
            minimum = "1"
    )
    @JsonProperty("pagina")
    @Valid
    @NotNull(message = "La pagina es obligatoria")
    @Min(value = 1, message = "La página debe ser mayor que cero")
    private Integer page;

    @Schema(
            description = "Tamaño de la página (cantidad de elementos por página). Máximo permitido: 100.",
            example = "20",
            minimum = "1",
            maximum = "100"
    )
    @JsonProperty("tamano")
    @Valid
    @NotNull(message = "El tamano es obligatorio")
    @Min(value = 1, message = "El tamaño debe ser mayor que cero")
    @Max(value = 100, message = "El tamaño máximo permitido es 100 elementos.")
    private Integer size;

    @Schema(
            description = "Filtros para refinar la consulta."
    )
    @JsonProperty("filtros")
    @Valid
    private F filters;

    @Schema(
            description = "Total de registros disponibles en la base de datos. Este campo es de solo lectura.",
            example = "150"
    )
    @JsonProperty("totalRegistros")
    private Long totalRecords;

    @Schema(
            description = "Total de páginas disponibles basado en el tamaño y número de registros. Este campo es de solo lectura.",
            example = "8"
    )
    @JsonProperty("totalPaginas")
    private Long totalPages;
}
