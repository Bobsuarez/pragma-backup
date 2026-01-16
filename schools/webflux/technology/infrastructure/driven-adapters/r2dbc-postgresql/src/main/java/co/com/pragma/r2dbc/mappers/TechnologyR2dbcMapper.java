package co.com.pragma.r2dbc.mappers;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.r2dbc.entity.TechnologyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyR2dbcMapper {

    TechnologyEntity toEntity(Technology technology);

    Technology toDomain(TechnologyEntity entity);
}


