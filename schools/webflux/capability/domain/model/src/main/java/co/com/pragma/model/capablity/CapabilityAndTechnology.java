package co.com.pragma.model.capablity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CapabilityAndTechnology {

    private Long capabilityId;

    private Long technologyId;
}