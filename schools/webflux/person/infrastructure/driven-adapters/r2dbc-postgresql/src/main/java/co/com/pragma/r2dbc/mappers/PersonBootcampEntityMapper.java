package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.Bootcamp;
import co.com.pragma.model.Person;
import co.com.pragma.model.PersonBootcamp;
import co.com.pragma.r2dbc.entity.BootcampEntity;
import co.com.pragma.r2dbc.entity.PersonBootcampEntity;
import co.com.pragma.r2dbc.entity.PersonEntity;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface PersonBootcampEntityMapper {
    
    PersonBootcampEntity toEntity(PersonBootcamp personBootcamp);
    
    default PersonBootcamp toDomain(PersonBootcampEntity entity, BootcampEntity bootcampEntity) {
        if (entity == null) {
            return null;
        }
        
        Bootcamp bootcamp = null;
        if (bootcampEntity != null) {
            bootcamp = Bootcamp.builder()
                    .id(bootcampEntity.getId())
                    .name(bootcampEntity.getName())
                    .description(bootcampEntity.getDescription())
                    .launchDate(parseStringToLocalDate(bootcampEntity.getLaunchDate()))
                    .durationMonths(bootcampEntity.getDurationMonths())
                    .build();
        }
        
        return PersonBootcamp.builder()
                .id(entity.getId())
                .personId(entity.getPersonId())
                .bootcampId(entity.getBootcampId())
                .bootcamp(bootcamp)
                .build();
    }
    
    default Bootcamp toBootcampDomain(BootcampEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Bootcamp.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .launchDate(parseStringToLocalDate(entity.getLaunchDate()))
                .durationMonths(entity.getDurationMonths())
                .build();
    }
    
    default Person toPersonDomain(PersonEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Person.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }
    
    default String formatLocalDateToString(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
    
    default LocalDate parseStringToLocalDate(String date) {
        return date != null ? LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
}

