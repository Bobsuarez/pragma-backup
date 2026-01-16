package co.com.pragma.model.capablity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapabilitiesFilters {

    private Integer techCount;

    private String capabilityName;

    private String orderBy;
}

