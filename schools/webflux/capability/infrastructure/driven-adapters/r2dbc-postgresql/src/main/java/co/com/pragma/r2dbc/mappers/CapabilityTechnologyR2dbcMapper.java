package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.capablity.CapabilityAndTechnology;
import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CapabilityTechnologyR2dbcMapper {

    List<CapabilityTechnologyEntity> toEntityList(List<Long> technologyIds);

    @Mapping(target = "technologyId", source = "id")
    @Mapping(target = "capabilityId", ignore = true) // El ID de capacidad se pone en el Service
    CapabilityTechnologyEntity toEntity(Long id);


    CapabilityAndTechnology toDomain(CapabilityTechnologyEntity entity);
}


