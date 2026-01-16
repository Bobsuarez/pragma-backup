package co.com.pragma.model.gateway;

import reactor.core.publisher.Mono;

public interface BootcampMetricsRepository {

    Mono<Integer> countCapabilitiesByBootcampId(Long bootcampId);

    Mono<Integer> countTechnologiesByBootcampId(Long bootcampId);

    Mono<Integer> countEnrolledPeopleByBootcampId(Long bootcampId);

    Mono<BootcampInfo> getBootcampInfo(Long bootcampId);
    
    record BootcampInfo(
        Long id,
        String name,
        String description,
        String launchDate,
        Integer durationMonths
    ) {}
}

