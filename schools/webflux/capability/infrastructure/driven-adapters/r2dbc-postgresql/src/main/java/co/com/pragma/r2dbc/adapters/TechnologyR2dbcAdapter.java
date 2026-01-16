package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import co.com.pragma.model.technology.gateway.TechnologyRepository;
import co.com.pragma.r2dbc.repositories.CapabilityTechnologyReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository("technologyR2dbcRepository")
@Slf4j
@RequiredArgsConstructor
public class TechnologyR2dbcAdapter implements TechnologyRepository {

    private final CapabilityTechnologyReactiveRepository capabilityTechnologyReactiveRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Flux<Long> findTechnologyIdsByCapabilityId(Long capabilityId) {
        log.debug("Finding technology IDs by capabilityId={}", capabilityId);

        return capabilityTechnologyReactiveRepository.findAllByCapabilityId(capabilityId)
                .map(entity -> entity.getTechnologyId())
                .doOnNext(technologyId -> log.debug("Found technology ID for capability, capabilityId={}, technologyId={}", 
                        capabilityId, technologyId))
                .doOnError(error -> log.error("Error finding technology IDs by capabilityId={}", capabilityId, error));
    }

    @Override
    public Mono<Long> getUsageCount(Long technologyId) {
        log.debug("Getting usage count for technologyId={}", technologyId);

        String sql = """
                SELECT COUNT(*) as usage_count
                FROM capability_technology
                WHERE technology_id = :technologyId
                """;

        return databaseClient.sql(sql)
                .bind("technologyId", technologyId)
                .map((row, metadata) -> row.get("usage_count", Long.class))
                .one()
                .defaultIfEmpty(0L)
                .doOnNext(count -> log.debug("Usage count for technologyId={} is {}", technologyId, count))
                .doOnError(error -> log.error("Error getting usage count for technologyId={}", technologyId, error));
    }

    @Override
    public Flux<Technology> findAllByIds(TechnologyIds ids) {
        // Este método se implementa en TechnologyAdapter (rest-consumer)
        return Flux.error(new UnsupportedOperationException("This method should be implemented by TechnologyAdapter in rest-consumer"));
    }
}
