package co.com.pragma.validator.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta paginada de bootcamps")
public class BootcampPageResponseDto {
    
    @Schema(description = "Lista de bootcamps")
    private List<BootcampListResponseDto> content;
    
    @Schema(description = "Número de página actual", example = "0")
    private int page;
    
    @Schema(description = "Tamaño de la página", example = "10")
    private int size;
    
    @Schema(description = "Total de elementos", example = "100")
    private long totalElements;
    
    @Schema(description = "Total de páginas", example = "10")
    private int totalPages;
    
    @Schema(description = "Indica si hay página siguiente", example = "true")
    private boolean hasNext;
    
    @Schema(description = "Indica si hay página anterior", example = "false")
    private boolean hasPrevious;
}

