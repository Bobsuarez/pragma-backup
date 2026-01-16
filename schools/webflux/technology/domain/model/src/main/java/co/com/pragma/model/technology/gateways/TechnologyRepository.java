package co.com.pragma.model.technology.gateways;

import co.com.pragma.model.technology.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Port to persist and query Technologies.
 */
public interface TechnologyRepository {

    /**
     * Persists a new Technology.
     */
    Mono<Technology> save(Technology technology);

    /**
     * Checks if there is already a Technology registered with the given name.
     */
    Mono<Boolean> existsByName(String name);

    /**
     * Finds all Technologies by their IDs.
     * @param ids list of technology IDs to search for
     * @return Flux of Technologies matching the provided IDs
     */
    Flux<Technology> findAllByIds(List<Long> ids);

    /**
     * Deletes a Technology by its ID.
     * @param id technology ID to delete
     * @return Mono<Void> that completes when the technology is deleted
     */
    Mono<Void> deleteById(Long id);
}


