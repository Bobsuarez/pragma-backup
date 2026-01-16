package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.capability.Capability;
import co.com.pragma.model.bootcamp.gateway.BootcampRepository;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.r2dbc.entity.BootcampEntity;
import co.com.pragma.r2dbc.mappers.BootcampEntityMapper;
import co.com.pragma.r2dbc.providers.BootcampSQLProvider;
import co.com.pragma.r2dbc.repositories.BootcampR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BootcampR2dbcAdapter implements BootcampRepository {

    private final BootcampR2dbcRepository bootcampR2dbcRepository;
    private final DatabaseClient databaseClient;
    private final BootcampEntityMapper bootcampEntityMapper;
    private final BootcampSQLProvider bootcampSQLProvider;

    @Override
    @Transactional
    public Mono<Bootcamp> save(Bootcamp bootcamp) {

        log.info("Saving bootcamp: {}", bootcamp.getName());

        // Convertir dominio a entidad usando mapper
        BootcampEntity entity = bootcampEntityMapper.toEntity(bootcamp);

        // Guardar bootcamp
        return bootcampR2dbcRepository.save(entity)
                .flatMap(savedEntity -> {
                    // Guardar relaciones con capacidades
                    List<Mono<Void>> relationshipSaves = bootcamp.getCapabilities().stream()
                            .map(capability -> saveBootcampCapabilityRelationship(
                                    savedEntity.getId(),
                                    capability.getId()
                            ))
                            .toList();

                    return Flux.concat(relationshipSaves)
                            .then(Mono.just(savedEntity));
                })
                .map(savedEntity -> {

                    List<Long> capabilityIds = bootcamp.getCapabilities().stream()
                            .map(Capability::getId)
                            .toList();

                    return bootcampEntityMapper.toDomain(savedEntity, capabilityIds);
                })
                .doOnSuccess(saved -> log.info("Bootcamp saved successfully with id: {}", saved.getId()))
                .doOnError(error -> log.error("Error saving bootcamp", error));
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        log.info("Finding bootcamp by id: {}", id);

        return bootcampR2dbcRepository.findById(id)
                .flatMap(entity -> {
                    // Obtener IDs de capacidades asociadas
                    return getCapabilityIdsByBootcampId(id)
                            .collectList()
                            .map(capabilityIds -> bootcampEntityMapper.toDomain(entity, capabilityIds));
                })
                .doOnSuccess(bootcamp -> log.info("Bootcamp found successfully with id: {}", id))
                .doOnError(error -> log.error("Error finding bootcamp by id: {}", id, error))
                .switchIfEmpty(Mono.error(new BusinessException("Bootcamp no encontrado con id: " + id)));
    }

    private Flux<Long> getCapabilityIdsByBootcampId(Long bootcampId) {
        String sql = bootcampSQLProvider.findCapabilityIdsByBootcampId();
        return databaseClient.sql(sql)
                .bind("bootcamp_id", bootcampId)
                .map((row, metadata) -> row.get("capability_id", Long.class))
                .all();
    }

    private Mono<Void> saveBootcampCapabilityRelationship(Long bootcampId, Long capabilityId) {
        String sql = bootcampSQLProvider.insertBootcampCapabilityRelationship();

        return databaseClient.sql(sql)
                .bind("bootcamp_id", bootcampId)
                .bind("capability_id", capabilityId)
                .fetch()
                .rowsUpdated()
                .then()
                .doOnError(error -> log.error(
                        "Error saving bootcamp-capability relationship: bootcampId={}, capabilityId={}",
                        bootcampId, capabilityId, error
                ));
    }
}

