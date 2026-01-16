package co.com.pragma.model.capablity.gateway.capabilitylist;

import co.com.pragma.model.capablity.CapabilityPage;
import reactor.core.publisher.Mono;

public interface CapabilityListRepository {

    Mono<CapabilityPage> findAll(int page, int size, String sort, String dir);
}
