package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.r2dbc.entity.CapabilityEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapabilityR2dbcMapper {

    CapabilityEntity toEntity(Capability capability);

    Capability toDomain(CapabilityEntity entity);
}


