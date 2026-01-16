package co.com.pragma.model.technology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model that represents a Technology to be used by capabilities.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Technology {

    private String id;
    private String name;
    private String description;
}


