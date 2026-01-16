package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.r2dbc.entity.CapabilityEntity;
import co.com.pragma.r2dbc.mappers.CapabilityR2dbcMapper;
import co.com.pragma.r2dbc.mappers.CapabilityTechnologyR2dbcMapper;
import co.com.pragma.r2dbc.repositories.CapabilityReactiveRepository;
import co.com.pragma.r2dbc.repositories.CapabilityTechnologyReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CapabilityAdapter implements CapabilityRepository {

    private final CapabilityReactiveRepository capabilityReactiveRepository;
    private final CapabilityTechnologyReactiveRepository capabilityTechnologyReactiveRepository;
    private final CapabilityR2dbcMapper technologyR2dbcMapper;
    private final CapabilityTechnologyR2dbcMapper capabilityTechnologyR2dbcMapper;

    @Override
    @Transactional
    public Mono<Capability> save(Capability capability) {

        log.debug("Saving capability to database, capabilityName={}", capability.getName());

        CapabilityEntity entity = technologyR2dbcMapper.toEntity(capability);

        return capabilityReactiveRepository.save(entity)
                .doOnNext(saved -> log.debug("Capability entity saved with id={}, capabilityName={}", saved.getId(), capability.getName()))
                .flatMap(saved -> saveTechnologies(capability, saved.getId())
                        .doOnSuccess(v -> log.debug("Technologies saved for capability, capabilityId={}, technologyCount={}", 
                                saved.getId(), capability.getTechnologyIds().size()))
                        .thenReturn(capability.toBuilder()
                                            .id(saved.getId())
                                            .build())
                )
                .doOnSuccess(saved -> log.info("Capability saved successfully, capabilityId={}, capabilityName={}", 
                        saved.getId(), saved.getName()))
                .doOnError(error -> log.error("Error saving capability to database, capabilityName={}", capability.getName(), error));
    }

    @Override
    public Mono<Boolean> findById(Long id) {
        return capabilityReactiveRepository.existsById(id)
                .switchIfEmpty(Mono.just(Boolean.FALSE))
                .doOnNext(capabilityEntity -> log.info("Capability found with id{}" , id))
                .doOnError(error -> log.error("Error finding capability by id={}", id, error));
    }

    @Override
    public Mono<Capability> findCapabilityById(Long id) {
        return capabilityReactiveRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Capability not found with id=" + id)))
                .map(technologyR2dbcMapper::toDomain)
                .doOnNext(capabilityEntity -> log.info("Capability found with id{}" , id))
                .doOnError(error -> log.error("Error finding capability by id={}", id, error));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(Long id) {
        log.debug("Deleting capability with id={}", id);
        
        // Primero eliminar las relaciones en capability_technology
        return capabilityTechnologyReactiveRepository.findAllByCapabilityId(id)
                .flatMap(capabilityTechnologyReactiveRepository::delete)
                .then()
                .then(capabilityReactiveRepository.deleteById(id))
                .doOnSuccess(v -> log.info("Capability deleted successfully, capabilityId={}", id))
                .doOnError(error -> log.error("Error deleting capability with id={}", id, error));
    }

    @Transactional
    private Mono<Void> saveTechnologies(Capability capability, Long capabilityId) {
        log.debug("Saving technologies for capability, capabilityId={}, technologyCount={}", 
                capabilityId, capability.getTechnologyIds().size());

        return Flux.fromIterable(capabilityTechnologyR2dbcMapper.toEntityList(capability.getTechnologyIds()))
                .map(tech -> tech.toBuilder()
                        .capabilityId(capabilityId)
                        .build())
                .as(capabilityTechnologyReactiveRepository::saveAll)
                .doOnComplete(() -> log.debug("All technologies saved for capability, capabilityId={}", capabilityId))
                .then()
                .doOnError(error -> log.error("Error saving technologies for capability, capabilityId={}", capabilityId, error));
    }
}


