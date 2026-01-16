package co.com.pragma.validator.mappers;

import co.com.pragma.model.bootcamp.BootcampList;
import co.com.pragma.model.bootcamp.BootcampPage;
import co.com.pragma.model.bootcamp.Capability;
import co.com.pragma.model.bootcamp.Technology;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import co.com.pragma.validator.dto.request.BootcampListRequestDto;
import co.com.pragma.validator.dto.response.BootcampListResponseDto;
import co.com.pragma.validator.dto.response.BootcampPageResponseDto;
import co.com.pragma.validator.dto.response.CapabilityResponseDto;
import co.com.pragma.validator.dto.response.TechnologyResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampListMapper {

    default BootcampSortField toSortField(String sortField) {
        if (sortField == null) {
            return BootcampSortField.NAME;
        }
        try {
            return BootcampSortField.valueOf(sortField.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BootcampSortField.NAME;
        }
    }

    default SortDirection toSortDirection(String sortDirection) {
        if (sortDirection == null) {
            return SortDirection.ASC;
        }
        try {
            return SortDirection.valueOf(sortDirection.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SortDirection.ASC;
        }
    }

    default BootcampListRequestDto toRequestDto(int page, int size, String sortField, String sortDirection) {
        return BootcampListRequestDto.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .sortDirection(sortDirection)
                .build();
    }

    @Mapping(target = "capabilities", source = "capabilities")
    BootcampListResponseDto toResponseDto(BootcampList bootcampList);

    List<BootcampListResponseDto> toResponseDtoList(List<BootcampList> bootcampLists);

    @Mapping(target = "content", source = "content")
    BootcampPageResponseDto toPageResponseDto(BootcampPage bootcampPage);

    CapabilityResponseDto toCapabilityResponseDto(Capability capability);

    List<CapabilityResponseDto> toCapabilityResponseDtoList(List<Capability> capabilities);

    TechnologyResponseDto toTechnologyResponseDto(Technology technology);

    List<TechnologyResponseDto> toTechnologyResponseDtoList(List<Technology> technologies);
}

