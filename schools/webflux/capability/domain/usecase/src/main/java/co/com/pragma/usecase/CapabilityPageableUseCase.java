package co.com.pragma.usecase;

import co.com.pragma.model.capablity.CapabilityPage;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import co.com.pragma.model.capablity.gateway.capabilitylist.CapabilityListRepository;
import co.com.pragma.model.technology.gateway.TechnologyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@Log
@AllArgsConstructor
public class CapabilityPageableUseCase {

    private final CapabilityListRepository capabilitiesRepository;
    private final TechnologyRepository technologyRepository;

    public Mono<CapabilityPage> execute(int page, int size, String sort, String dir) {

        log.log(
                Level.INFO,
                "Executing capability pageable use case, page={0}, size={1}, sort={2}, dir={3}",
                new Object[]{page, size, sort, dir}
        );

        return capabilitiesRepository.findAll(page, size, sort, dir)
                .flatMap(this::enrichWithTechnologies);
    }

    private Mono<CapabilityPage> enrichWithTechnologies(CapabilityPage pageData) {

        List<Long> uniqueTechnologyIds = pageData.getItems()
                .stream()
                .flatMap(cap -> cap.getTechnologyIds()
                        .stream())
                .distinct()
                .toList();

        log.info("Extracted unique technology IDs, count=" + uniqueTechnologyIds.size());

        if (uniqueTechnologyIds.isEmpty()) {
            log.warning("No technology IDs found, returning page data without technologies");
            return Mono.just(pageData);
        }

        return technologyRepository.findAllByIds(
                        TechnologyIds.builder()
                                .technologyIds(uniqueTechnologyIds)
                                .build()
                )
                .collectList()
                .doOnNext(technologies ->
                                  log.info("Retrieved technologies, count=" + technologies.size())
                )
                .map(technologies -> attachTechnologies(pageData, technologies));
    }

    private CapabilityPage attachTechnologies(
            CapabilityPage pageData,
            List<Technology> allTechnologies
    ) {

        return pageData.withItems(
                pageData.getItems()
                        .stream()
                        .map(cap -> cap.toBuilder()
                                .technologies(
                                     allTechnologies.stream()
                                     .filter(t -> cap.getTechnologyIds().contains(t.getId()))
                                     .toList()
                                )
                                .technologyIds(Collections.emptyList())
                                .build()
                        )
                        .toList()
        );
    }
}