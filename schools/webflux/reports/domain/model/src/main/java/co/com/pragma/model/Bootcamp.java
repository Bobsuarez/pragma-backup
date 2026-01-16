package co.com.pragma.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bootcamp {
    private Long id;
    private String name;
    private String description;
    private String launchDate;
    private Integer durationMonths;
}

