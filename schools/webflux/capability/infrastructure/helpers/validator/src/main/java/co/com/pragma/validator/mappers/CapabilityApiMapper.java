package co.com.pragma.validator.mappers;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.validator.dto.request.CapabilityRequestDto;
import co.com.pragma.validator.dto.respose.CapabilityResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapabilityApiMapper {

    Capability toDomain(CapabilityRequestDto request);

    CapabilityResponseDto toResponse(Capability technology);
}


