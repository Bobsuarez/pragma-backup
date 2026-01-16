package co.com.pragma.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampReport {
    private Long id;
    private Long bootcampId;
    private String bootcampName;
    private String bootcampDescription;
    private String launchDate;
    private Integer durationMonths;
    private Integer capabilitiesCount;
    private Integer technologiesCount;
    private Integer enrolledPeopleCount;
    private LocalDateTime createdAt;
}

