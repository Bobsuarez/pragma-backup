package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.capability.Capability;
import co.com.pragma.r2dbc.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampEntityMapper {

    /**
     * Convierte un modelo de dominio Bootcamp a BootcampEntity
     * @param bootcamp Modelo de dominio
     * @return Entidad de persistencia
     */
    BootcampEntity toEntity(Bootcamp bootcamp);

    /**
     * Convierte una BootcampEntity a modelo de dominio Bootcamp
     * @param entity Entidad de persistencia
     * @param capabilities Lista de capacidades asociadas
     * @return Modelo de dominio
     */
    default Bootcamp toDomain(BootcampEntity entity, List<Long> capabilities) {
        if (entity == null) {
            return null;
        }

        List<Capability> capabilityList = capabilities != null
                ? capabilities.stream()
                        .map(this::toCapabilityDomain)
                        .toList()
                : List.of();

        return Bootcamp.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .launchDate(entity.getLaunchDate())
                .durationMonths(entity.getDurationMonths())
                .capabilities(capabilityList)
                .build();
    }

    /**
     * Convierte CapabilityEntity a modelo de dominio Capability
     */
    default Capability toCapabilityDomain(Long ids) {
        return Capability.builder()
                .id(ids)
                .build();
    }

    /**
     * Formatea LocalDate a String (ISO_LOCAL_DATE)
     */
    default String formatLocalDateToString(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Parsea String a LocalDate (ISO_LOCAL_DATE)
     */
    default LocalDate parseStringToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }
}

