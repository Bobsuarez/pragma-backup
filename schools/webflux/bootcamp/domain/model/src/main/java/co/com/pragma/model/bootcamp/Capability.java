package co.com.pragma.model.bootcamp;

import co.com.pragma.model.technology.Technology;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Capability {

    private Long id;
    private String name;
    private String description;
    private List<Technology> technologies;

}

