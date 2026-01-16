package co.com.pragma.validator.mappers;

import co.com.pragma.model.Bootcamp;
import co.com.pragma.validator.dto.request.BootcampRequestDto;
import co.com.pragma.validator.dto.response.BootcampResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    Bootcamp toDomain(BootcampRequestDto dto);
    
    BootcampResponseDto toResponseDto(Bootcamp bootcamp);
}

