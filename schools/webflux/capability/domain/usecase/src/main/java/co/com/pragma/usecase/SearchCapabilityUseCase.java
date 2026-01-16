package co.com.pragma.usecase;

import co.com.pragma.model.capablity.CapabilityIds;
import co.com.pragma.model.capablity.CapabilityStatus;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.model.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Log
@AllArgsConstructor
public class SearchCapabilityUseCase {

    private final CapabilityRepository capabilityRepository;

    public Mono<CapabilityStatus> execute(CapabilityIds capabilityIds) {

        log.log(
                Level.INFO,
                "Executing search capability use case, capabilityName={0}",
                new Object[]{capabilityIds.getCapabilityIds()}
        );

        return Flux.fromIterable(capabilityIds.getCapabilityIds())
                .flatMap(capabilityRepository::findById)
                .collectList()
                .map(foundCapabilities -> {
                    log.info("------" + foundCapabilities + "-------");
                    return foundCapabilities.contains(Boolean.FALSE) ? Boolean.FALSE : Boolean.TRUE;
                })
                .flatMap(allExist -> {
                    if (!allExist) {
                        return Mono.error(new BusinessException("One or more capabilities are not registered"));
                    }
                    return Mono.just(CapabilityStatus.builder()
                                             .isExisting(Boolean.TRUE)
                                             .build());
                });
    }
}
