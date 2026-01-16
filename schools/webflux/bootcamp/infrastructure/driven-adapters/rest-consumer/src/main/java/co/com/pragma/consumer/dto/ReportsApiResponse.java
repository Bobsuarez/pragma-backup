package co.com.pragma.consumer.dto;

import co.com.pragma.model.bootcamp.Bootcamp;
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
public class ReportsApiResponse {

    private String traceId;

    private Bootcamp data;

    private String status;
}
