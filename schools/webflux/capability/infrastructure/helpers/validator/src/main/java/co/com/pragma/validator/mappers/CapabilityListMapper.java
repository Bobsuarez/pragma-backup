package co.com.pragma.validator.mappers;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityIds;
import co.com.pragma.validator.dto.request.CapabilityIdsRequestDto;
import co.com.pragma.validator.dto.request.CapabilityRequestDto;
import co.com.pragma.validator.dto.respose.CapabilityResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapabilityListMapper {

    CapabilityIds toDomain(CapabilityIdsRequestDto request);

    CapabilityResponseDto toResponse(Capability technology);
}


