package co.com.pragma.r2dbc.repositories;

import co.com.pragma.r2dbc.entity.CapabilityTechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CapabilityTechnologyReactiveRepository extends ReactiveCrudRepository<CapabilityTechnologyEntity, Long> {


    Flux<CapabilityTechnologyEntity> findAllByCapabilityId(Long capabilityId);

}


