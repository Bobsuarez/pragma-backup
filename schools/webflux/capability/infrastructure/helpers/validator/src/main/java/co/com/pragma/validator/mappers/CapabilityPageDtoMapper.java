package co.com.pragma.validator.mappers;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.utils.PaginatedResponse;
import co.com.pragma.validator.dto.respose.CapabilityListDto;
import co.com.pragma.validator.dto.respose.capabilities.CapabilityResultsPageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapabilityPageDtoMapper {

    @Mapping(target = "queryDto.totalRecords", source = "totalRecords")
    @Mapping(target = "queryDto.page", source = "page")
    @Mapping(target = "queryDto.size", source = "size")
    @Mapping(target = "queryDto.totalPages", source = "totalPages")
    @Mapping(target = "responseDtoList", source = "items")
    CapabilityResultsPageDto<CapabilityListDto> toDto(PaginatedResponse<Technology> response);
}

