package co.com.pragma.validator.mappers;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityIds;
import co.com.pragma.model.capablity.CapabilityStatus;
import co.com.pragma.validator.dto.request.CapabilityIdsRequestDto;
import co.com.pragma.validator.dto.respose.CapabilityResponseDto;
import co.com.pragma.validator.dto.respose.CapabilityStatusResponseDto;
import co.com.pragma.validator.dto.respose.CapabilityWithTechnologyResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CapabilityStatusMapper {

    CapabilityStatusResponseDto toResponse(CapabilityStatus capabilityStatus);


    @Mapping(target = "technologyIds", ignore = true)
    List<CapabilityWithTechnologyResponseDto> toDtoList(List<Capability> capabilities);
}


