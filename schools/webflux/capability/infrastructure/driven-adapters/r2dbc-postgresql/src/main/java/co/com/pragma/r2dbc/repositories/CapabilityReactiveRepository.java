package co.com.pragma.r2dbc.repositories;

import co.com.pragma.r2dbc.entity.CapabilityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapabilityReactiveRepository extends ReactiveCrudRepository<CapabilityEntity, Long> {


    @Query("SELECT COUNT(*) FROM capability")
    Mono<Long> countAll();
}


