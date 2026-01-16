package co.com.pragma.model.capablity.gateway;

import co.com.pragma.model.capablity.CapabilityAndTechnology;
import reactor.core.publisher.Flux;

public interface CapabilityTechnologyRepository {

    Flux<CapabilityAndTechnology> findTechnologyAndCapabilityById(Long id);

}
