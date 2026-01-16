package co.com.pragma.validator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitar el registro de una nueva tecnología")
public class TechnologyRequestDto {

    @NotBlank(message = "El nombre de la tecnología es obligatorio")
    @Size(max = 50, message = "El nombre de la tecnología no puede superar los 50 caracteres")
    @Schema(description = "Nombre de la tecnología", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    public String name;

    @NotBlank(message = "La descripción de la tecnología es obligatoria")
    @Size(max = 90, message = "La descripción de la tecnología no puede superar los 90 caracteres")
    @Schema(description = "Descripción de la tecnología", example = "Lenguaje de programación orientado a objetos", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 90)
    public String description;
}


