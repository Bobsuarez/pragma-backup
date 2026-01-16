package co.com.pragma.validator.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "DTO para representar una capacidad")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapabilityDto {

    @Schema(description = "ID de la capacidad", example = "1", required = true)
    @NotNull(message = "El ID de la capacidad es requerido")
    private Long id;

    @Schema(description = "Nombre de la capacidad", example = "Java")
    private String name;

    @Schema(description = "Descripción de la capacidad", example = "Programación en Java")
    private String description;
}

