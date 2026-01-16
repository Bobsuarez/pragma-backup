package co.com.pragma.model.gateway;

import co.com.pragma.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampRepository {

    Mono<Bootcamp> findById(Long id);
}

