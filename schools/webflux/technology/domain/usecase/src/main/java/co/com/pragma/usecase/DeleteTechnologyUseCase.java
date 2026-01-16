package co.com.pragma.usecase;

import co.com.pragma.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

/**
 * Use case to delete a Technology by its ID.
 */
@Log
@RequiredArgsConstructor
public class DeleteTechnologyUseCase {

    private final TechnologyRepository technologyRepository;

    /**
     * Deletes a Technology by its ID.
     *
     * @param id technology ID to delete
     * @return Mono<Void> that completes when the technology is deleted
     */
    public Mono<Void> execute(Long id) {
        return technologyRepository.deleteById(id)
                .doOnSuccess(unused -> log.info("Technology with ID {} deleted successfully" +id))
                .doOnError(error -> log.severe( "Failed to delete technology " + error.getMessage()));
    }
}
