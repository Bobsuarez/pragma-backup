package co.com.pragma.model.capability;

import co.com.pragma.model.technology.Technology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapabilityListResult {

    private Long id;
    private String name;
    private String description;
    private List<Technology> technologies;
}

