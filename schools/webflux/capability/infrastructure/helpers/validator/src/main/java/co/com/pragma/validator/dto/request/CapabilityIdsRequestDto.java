package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para consultar capacidades por lista de IDs")
public class CapabilityIdsRequestDto {

    @Schema(
            description = "Lista de IDs de capacidades a consultar",
            example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "La lista de IDs de capacidades no puede estar vacía")
    public List<@NotNull(message = "El id de capacidad no puede ser nulo") Long> capabilityIds;
}
