package co.com.pragma.usecase;

import co.com.pragma.model.technology.gateway.TechnologyRepository;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Log
public class GetTechnologyUsageCountUseCase {

    private final TechnologyRepository technologyR2dbcRepository;

    public GetTechnologyUsageCountUseCase(
            TechnologyRepository technologyR2dbcRepository) {
        this.technologyR2dbcRepository = technologyR2dbcRepository;
    }

    public Mono<Long> execute(Long technologyId) {
        log.log(Level.INFO, "Executing get technology usage count use case, technologyId={0}", technologyId);

        return technologyR2dbcRepository.getUsageCount(technologyId)
                .doOnNext(count -> log.log(Level.INFO, 
                        "Usage count retrieved, technologyId={0}, count={1}", 
                        new Object[]{technologyId, count}))
                .doOnError(error -> log.severe("Error getting usage count, technologyId=" + technologyId + ", error=" + error.getMessage()));
    }
}
