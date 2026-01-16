package co.com.pragma.validator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BootcampDetailResponseDto {
    private Long id;
    private String name;
    private String description;
    private String launchDate;
    private Integer durationMonths;
    private List<CapabilityDetailDto> capabilities;
    private List<EnrolledPersonDto> enrolledPeople;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CapabilityDetailDto {
        private Long id;
        private String name;
        private String description;
        private List<TechnologyDto> technologies;
    }
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TechnologyDto {
        private Long id;
        private String name;
    }
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EnrolledPersonDto {
        private Long id;
        private String name;
        private String email;
    }
}
