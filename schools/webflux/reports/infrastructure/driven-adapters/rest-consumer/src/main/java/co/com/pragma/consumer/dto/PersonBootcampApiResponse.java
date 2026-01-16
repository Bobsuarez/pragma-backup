package co.com.pragma.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonBootcampApiResponse {
    private String traceId;
    private PersonBootcampData data;
    private String status;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonBootcampData {
        @com.fasterxml.jackson.annotation.JsonProperty("bootcamp-register")
        private Integer bootcampRegister;
    }
}
