package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityAndTechnology;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.model.capablity.gateway.CapabilityTechnologyRepository;
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
public class CapabilityAndTechnologyAdapter implements CapabilityTechnologyRepository {

    private final CapabilityTechnologyReactiveRepository capabilityTechnologyReactiveRepository;
    private final CapabilityTechnologyR2dbcMapper capabilityTechnologyR2dbcMapper;

    @Override
    public Flux<CapabilityAndTechnology> findTechnologyAndCapabilityById(Long id) {

        return capabilityTechnologyReactiveRepository.findAllByCapabilityId(id)
                .flatMap( entity -> {
                    log.info("Mapping CapabilityAndTechnologyEntity to CapabilityAndTechnology domain object");
                    return Mono.just(capabilityTechnologyR2dbcMapper.toDomain(entity));
                });
    }
}


