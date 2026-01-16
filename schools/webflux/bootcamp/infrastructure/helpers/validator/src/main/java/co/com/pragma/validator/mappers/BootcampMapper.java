package co.com.pragma.validator.mappers;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.capability.Capability;
import co.com.pragma.validator.dto.request.BootcampRequestDto;
import co.com.pragma.validator.dto.request.CapabilityDto;
import co.com.pragma.validator.dto.response.BootcampResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capabilities", source = "capabilities")
    Bootcamp toDomain(BootcampRequestDto dto);

    BootcampResponseDto toResponseDto(Bootcamp bootcamp);

    Capability toCapabilityDomain(CapabilityDto dto);

    CapabilityDto toCapabilityDto(Capability capability);

    List<Capability> toCapabilityDomainList(List<CapabilityDto> dtos);

    List<CapabilityDto> toCapabilityDtoList(List<Capability> capabilities);
}

