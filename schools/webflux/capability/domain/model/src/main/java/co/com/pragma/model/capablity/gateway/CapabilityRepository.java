package co.com.pragma.model.capablity.gateway;

import co.com.pragma.model.capablity.Capability;
import reactor.core.publisher.Mono;

public interface CapabilityRepository {

    Mono<Capability> save(Capability capability);

    Mono<Boolean> findById(Long id);

    Mono<Capability> findCapabilityById(Long id);

    Mono<Void> deleteById(Long id);

}
