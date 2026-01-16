package co.com.pragma.validator.mappers;

import co.com.pragma.model.Bootcamp;
import co.com.pragma.model.PersonBootcamp;
import co.com.pragma.validator.dto.request.PersonBootcampRequestDto;
import co.com.pragma.validator.dto.response.BootcampResponseDto;
import co.com.pragma.validator.dto.response.PersonBootcampResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface PersonBootcampMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bootcamp", ignore = true)
    PersonBootcamp toDomain(PersonBootcampRequestDto dto);
    
    default PersonBootcampResponseDto toResponseDto(PersonBootcamp personBootcamp) {
        if (personBootcamp == null) {
            return null;
        }
        
        BootcampResponseDto bootcampDto = null;
        if (personBootcamp.getBootcamp() != null) {
            bootcampDto = toBootcampResponseDto(personBootcamp.getBootcamp());
        }
        
        return PersonBootcampResponseDto.builder()
                .id(personBootcamp.getId())
                .personId(personBootcamp.getPersonId())
                .bootcampId(personBootcamp.getBootcampId())
                .bootcamp(bootcampDto)
                .build();
    }
    
    @Mapping(target = "launchDate", expression = "java(formatLocalDateToString(bootcamp.getLaunchDate()))")
    BootcampResponseDto toBootcampResponseDto(Bootcamp bootcamp);
    
    default String formatLocalDateToString(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
    
    default LocalDate parseStringToLocalDate(String date) {
        return date != null ? LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
}

