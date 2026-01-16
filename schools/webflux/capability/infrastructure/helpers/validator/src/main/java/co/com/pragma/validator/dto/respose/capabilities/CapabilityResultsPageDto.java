package co.com.pragma.validator.dto.respose.capabilities;

import co.com.pragma.validator.dto.request.query.QueryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta paginada de capacidades")
public class CapabilityResultsPageDto<T> {

    @Schema(description = "Información de paginación")
    @JsonProperty("paginacion")
    private QueryDto queryDto;

    @Schema(description = "Lista de registros de capacidades")
    @JsonProperty("registros")
    private List<T> responseDtoList;
}
