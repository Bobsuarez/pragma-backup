package co.com.pragma.model.capablity;

import co.com.pragma.model.technology.Technology;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Capability {

    private Long id;
    private String name;
    private String description;
    private List<Long> technologyIds;
    private List<Technology> technologies;
}