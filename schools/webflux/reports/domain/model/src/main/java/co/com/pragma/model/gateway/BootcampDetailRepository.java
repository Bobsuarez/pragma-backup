package co.com.pragma.model.gateway;

import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampDetailRepository {
    
    Mono<BootcampDetail> getBootcampDetail(Long bootcampId);
    
    record BootcampDetail(
            Long id,
            String name,
            String description,
            String launchDate,
            Integer durationMonths,
            List<CapabilityDetail> capabilities,
            List<EnrolledPerson> enrolledPeople
    ) {}
    
    record CapabilityDetail(
            Long id,
            String name,
            String description,
            List<Technology> technologies
    ) {}
    
    record Technology(
            Long id,
            String name
    ) {}
    
    record EnrolledPerson(
            Long id,
            String name,
            String email
    ) {}
}
