package co.com.pragma.r2dbc.repositories;

import co.com.pragma.r2dbc.entity.BootcampEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BootcampR2dbcRepository extends ReactiveCrudRepository<BootcampEntity, Long> {

    @Query("SELECT COUNT(*) FROM bootcamp")
    Mono<Long> countAll();
}

