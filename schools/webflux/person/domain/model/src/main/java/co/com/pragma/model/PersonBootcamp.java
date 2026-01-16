package co.com.pragma.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PersonBootcamp {
    private Long id;
    private Long personId;
    private Long bootcampId;
    private Bootcamp bootcamp;
}

