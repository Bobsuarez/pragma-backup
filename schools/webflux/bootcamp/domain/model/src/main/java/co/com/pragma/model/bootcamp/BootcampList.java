package co.com.pragma.model.bootcamp;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class BootcampList {

    private List<Capability> capabilities;

    private List<Long> idCapabilities;

    private Long id;

    private String description;

    private String name;
}

