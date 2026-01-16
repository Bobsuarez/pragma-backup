package co.com.pragma.consumer.mappers;

import co.com.pragma.consumer.dto.BootcampApiResponse;
import co.com.pragma.consumer.dto.CapabilityWithTechnologiesResponse;
import co.com.pragma.consumer.dto.PersonBootcampListResponse;
import co.com.pragma.model.gateway.BootcampDetailRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampDetailConsumerMapper {

    @Mapping(target = "capabilities", ignore = true)
    @Mapping(target = "enrolledPeople", ignore = true)
    BootcampDetailRepository.BootcampDetail toBootcampDetail(BootcampApiResponse.BootcampData source);

    BootcampDetailRepository.CapabilityDetail toCapabilityDetail(CapabilityWithTechnologiesResponse source);

    List<BootcampDetailRepository.CapabilityDetail> toCapabilityDetailList(List<CapabilityWithTechnologiesResponse> source);

    BootcampDetailRepository.Technology toTechnology(CapabilityWithTechnologiesResponse.Technology source);

    List<BootcampDetailRepository.Technology> toTechnologyList(List<CapabilityWithTechnologiesResponse.Technology> source);

    BootcampDetailRepository.EnrolledPerson toEnrolledPerson(PersonBootcampListResponse.PersonData source);

    List<BootcampDetailRepository.EnrolledPerson> toEnrolledPersonList(List<PersonBootcampListResponse.PersonData> source);
}
