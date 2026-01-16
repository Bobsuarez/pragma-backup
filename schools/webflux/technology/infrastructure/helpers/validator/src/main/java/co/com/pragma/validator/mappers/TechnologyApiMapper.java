package co.com.pragma.validator.mappers;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.validator.dto.request.TechnologyRequestDto;
import co.com.pragma.validator.dto.respose.TechnologyResponseDto;
import co.com.pragma.validator.dto.respose.TechnologySimpleResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyApiMapper {

    Technology toDomain(TechnologyRequestDto request);

    TechnologyResponseDto toResponse(Technology technology);

    TechnologySimpleResponseDto toSimpleResponse(Technology technology);
}


