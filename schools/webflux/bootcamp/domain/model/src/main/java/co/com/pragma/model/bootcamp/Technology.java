package co.com.pragma.model.bootcamp;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Technology {
    private Long id;
    private String name;
}

