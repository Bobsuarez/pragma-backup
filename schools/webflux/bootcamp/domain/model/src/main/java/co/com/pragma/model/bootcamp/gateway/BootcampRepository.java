package co.com.pragma.model.bootcamp.gateway;

import co.com.pragma.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampRepository {

    Mono<Bootcamp> save(Bootcamp bootcamp);

    Mono<Bootcamp> findById(Long id);
}

