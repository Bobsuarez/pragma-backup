package co.com.pragma.validator.mappers;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.validator.dto.respose.TechnologyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyApiMapper {

    TechnologyDto toResponseDto(Technology technology);
}
