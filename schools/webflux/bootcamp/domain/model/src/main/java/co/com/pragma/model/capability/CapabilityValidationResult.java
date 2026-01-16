package co.com.pragma.model.capability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityValidationResult {

    private Boolean isValidate;

    private String message;
}

