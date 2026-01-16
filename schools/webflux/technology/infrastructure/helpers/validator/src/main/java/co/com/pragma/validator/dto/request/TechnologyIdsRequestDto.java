package co.com.pragma.validator.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyIdsRequestDto {

    @NotEmpty(message = "La lista de IDs no puede estar vacía")
    @Schema(description = "Lista de IDs de tecnologías a buscar", example = "[\"id1\", \"id2\", \"id3\"]", required = true)
    private List<Long> technologyIds;

}
