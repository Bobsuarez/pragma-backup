package co.com.pragma.model.bootcamp;

import co.com.pragma.model.capability.Capability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Bootcamp {

    private Long id;
    private String name;
    private String description;
    private String launchDate;
    private Integer durationMonths;
    private List<Capability> capabilities;
}

