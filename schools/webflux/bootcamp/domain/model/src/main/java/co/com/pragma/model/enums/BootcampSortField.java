package co.com.pragma.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BootcampSortField {

    NAME ("nombre"),
    CAPABILITIES_COUNT("capacidades"),;

    private String name;
}

