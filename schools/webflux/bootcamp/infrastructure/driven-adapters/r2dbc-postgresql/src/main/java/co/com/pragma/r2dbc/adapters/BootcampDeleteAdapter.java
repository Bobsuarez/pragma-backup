package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.bootcamp.gateway.BootcampDeleteRepository;
import co.com.pragma.r2dbc.providers.BootcampSQLProvider;
import co.com.pragma.r2dbc.repositories.BootcampR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BootcampDeleteAdapter implements BootcampDeleteRepository {

    private final BootcampR2dbcRepository repository;
    private final DatabaseClient databaseClient;
    private final BootcampSQLProvider sqlProvider;

    @Override
    @Transactional
    public Mono<Void> deleteById(Long bootcampId) {
        // Solo eliminar las relaciones bootcamp-capability y el bootcamp
        // Las capacidades y tecnologías se eliminan en el UseCase a través de APIs externas
        return deleteBootcampRelationships(bootcampId)
                .then(deleteBootcamp(bootcampId));
    }

    private Mono<Long> countBootcampsByCapabilityId(Long capabilityId, Long bootcampId) {
        String sql = sqlProvider.countBootcampsByCapabilityId();
        return databaseClient.sql(sql)
                .bind("capability_id", capabilityId)
                .bind("bootcamp_id", bootcampId)
                .map((row, metadata) -> {
                    Long count = row.get("count", Long.class);
                    return count != null ? count : 0L;
                })
                .first();
    }

    private Mono<Void> deleteBootcampRelationships(Long bootcampId) {
        return deleteBootcampCapabilityRelationships(bootcampId);
    }

    private Mono<Void> deleteBootcampCapabilityRelationships(Long bootcampId) {
        String sql = sqlProvider.deleteBootcampCapabilityRelationships();
        return databaseClient.sql(sql)
                .bind("bootcamp_id", bootcampId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    private Mono<Void> deleteBootcamp(Long bootcampId) {
        return repository.deleteById(bootcampId)
                .then();
    }

    @Override
    public Flux<Long> getCapabilityIdsByBootcampId(Long bootcampId) {
        String sql = sqlProvider.findCapabilityIdsByBootcampId();
        return databaseClient.sql(sql)
                .bind("bootcamp_id", bootcampId)
                .map((row, metadata) -> row.get("capability_id", Long.class))
                .all();
    }

    @Override
    public Mono<Boolean> isCapabilityUsedByOtherBootcamps(Long capabilityId, Long bootcampId) {
        return countBootcampsByCapabilityId(capabilityId, bootcampId)
                .map(count -> count > 0);
    }
}

