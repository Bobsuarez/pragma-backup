package co.com.pragma.validator.dto.respose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CapabilityWithTechnologyResponseDto {

    private Long id;
    private String name;
    private String description;
    private List<TechnologyDto> technologies;
}