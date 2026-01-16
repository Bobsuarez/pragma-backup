package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.BootcampList;
import co.com.pragma.model.bootcamp.BootcampPage;
import co.com.pragma.model.bootcamp.Capability;
import co.com.pragma.model.bootcamp.gateway.BootcampListRepository;
import co.com.pragma.model.capability.CapabilityIds;
import co.com.pragma.model.capability.CapabilityListResult;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Log
public class BootcampListUseCase {

    private final BootcampListRepository repository;
    private final CapabilityRepository capabilityRepository;

    public Mono<BootcampPage> execute(
            int page,
            int size,
            BootcampSortField sortField,
            SortDirection sortDirection,
            String traceId
    ) {
        log.info(() -> String.format(
                "Listing bootcamps - page=%d, size=%d, sortField=%s, sortDirection=%s, traceId=%s",
                page, size, sortField, sortDirection, traceId
        ));

        return repository.findAll(page, size, sortField, sortDirection)
                .flatMap(data -> enrichWithCapabilities(data, traceId))
                .doOnSuccess(result ->
                                     log.info(() -> String.format(
                                             "Bootcamps listed successfully, total=%d, traceId=%s",
                                             result.getTotalElements(), traceId
                                     ))
                )
                .doOnError(error ->
                                   log.severe(() -> String.format(
                                           "Error listing bootcamps, traceId=%s, error=%s",
                                           traceId, error.getMessage()
                                   ))
                );
    }

    private Mono<BootcampPage> enrichWithCapabilities(
            BootcampPage data,
            String traceId
    ) {
        List<String> capabilityIds = data.getContent().stream()
                .map(BootcampList::getIdCapabilities)
                .flatMap(List::stream)
                .distinct()
                .map(String::valueOf)
                .toList();

        if (capabilityIds.isEmpty()) {
            return Mono.just(data);
        }

        return capabilityRepository.findAllTechnologiesByIds(
                        CapabilityIds.builder()
                                .capabilityIds(capabilityIds)
                                .build()
                )
                .collectList()
                .map(results -> attachCapabilities(data, results))
                .onErrorResume(error -> {
                    log.warning(() -> String.format(
                            "Capabilities enrichment failed, traceId=%s, error=%s",
                            traceId, error.getMessage()
                    ));
                    return Mono.just(data); // degradación elegante
                });
    }

    private BootcampPage attachCapabilities(
            BootcampPage pageData,
            List<CapabilityListResult> allCapabilityListResults
    ) {

        return pageData.withItems(
                pageData.getContent()
                        .stream()
                        .map(cap -> cap.toBuilder()
                                .capabilities(
                                        allCapabilityListResults.stream()
                                                .filter(t -> cap.getIdCapabilities()
                                                        .contains(t.getId()))
                                                .map(this::mapCapabilitiesToBootcamps)
                                                .toList()
                                )
                                .build()
                        )
                        .toList()
        );
    }

    private Capability mapCapabilitiesToBootcamps(CapabilityListResult capabilities) {
        return Capability.builder()
                .id(capabilities.getId())
                .name(capabilities.getName())
                .description(capabilities.getDescription())
                .technologies(capabilities.getTechnologies())
                .build();
    }
}

