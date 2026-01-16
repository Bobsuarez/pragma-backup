package co.com.pragma.model.technology.gateway;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyRepository {

    Flux<Technology> findAllByIds(TechnologyIds ids);

    Flux<Long> findTechnologyIdsByCapabilityId(Long capabilityId);

    Mono<Long> getUsageCount(Long technologyId);
}
