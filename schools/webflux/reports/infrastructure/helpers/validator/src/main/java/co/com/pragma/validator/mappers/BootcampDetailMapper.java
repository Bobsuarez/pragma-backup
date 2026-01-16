package co.com.pragma.validator.mappers;

import co.com.pragma.model.gateway.BootcampDetailRepository;
import co.com.pragma.validator.dto.response.BootcampDetailResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampDetailMapper {

    BootcampDetailResponseDto toResponseDto(BootcampDetailRepository.BootcampDetail detail);

    BootcampDetailResponseDto.CapabilityDetailDto toCapabilityDetailDto(BootcampDetailRepository.CapabilityDetail capability);

    BootcampDetailResponseDto.TechnologyDto toTechnologyDto(BootcampDetailRepository.Technology technology);

    BootcampDetailResponseDto.EnrolledPersonDto toEnrolledPersonDto(BootcampDetailRepository.EnrolledPerson person);
}
