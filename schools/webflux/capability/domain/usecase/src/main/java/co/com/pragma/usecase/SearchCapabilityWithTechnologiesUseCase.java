package co.com.pragma.usecase;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityAndTechnology;
import co.com.pragma.model.capablity.CapabilityIds;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.model.capablity.gateway.CapabilityTechnologyRepository;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import co.com.pragma.model.technology.gateway.TechnologyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@Log
@AllArgsConstructor
public class SearchCapabilityWithTechnologiesUseCase {

    private final CapabilityRepository capabilityRepository;
    private final TechnologyRepository technologyRepository;
    private final CapabilityTechnologyRepository capabilityTechnologyRepository;

    public Mono<List<Capability>> execute(CapabilityIds capabilityIds) {

        log.log(
                Level.INFO,
                "Executing search capability use case, capabilityName={0}",
                new Object[]{capabilityIds.getCapabilityIds()}
        );

        return Flux.fromIterable(capabilityIds.getCapabilityIds())
                .flatMap(capabilityRepository::findCapabilityById)
                .collectList()
                .doOnNext(capabilities ->
                        log.log(Level.INFO, "Found {0} capabilities", new Object[]{capabilities.size()}))
                .flatMap(this::enrichWithTechnologyIds)
                .flatMap(this::enrichWithTechnologies)
                .doOnSuccess(capabilities ->
                                     log.log(Level.INFO,
                                             "Successfully enriched {0} capabilities with technologies",
                                             new Object[]{capabilities.size()}))
                .doOnError(error ->
                                   log.severe("Error in search capability use case: " + error.getMessage()));
    }

    private Mono<List<Capability>> enrichWithTechnologyIds(List<Capability> capabilities){

        return Flux.fromIterable(capabilities)
                .flatMap(capability -> capabilityTechnologyRepository.findTechnologyAndCapabilityById(capability.getId())
                        .collectList()
                        .map(capabilityAndTechnologies -> {

                            List<Long> technologyIds = capabilityAndTechnologies.stream()
                                    .map(CapabilityAndTechnology::getTechnologyId)
                                    .toList();

                            return capability.toBuilder()
                                    .technologyIds(technologyIds)
                                    .build();
                        }))
                .collectList()
                .doOnNext( capabilities1 ->  log.log(Level.INFO, "Enriched capabilities with technologies, total capabilities={0}", new Object[]{capabilities1.size()}));
    }

    private Mono<List<Capability>> enrichWithTechnologies(List<Capability> capabilities){

        List<Long> uniqueTechnologyIds = capabilities.stream()
                .flatMap(capability -> capability.getTechnologyIds().stream())
                .distinct()
                .toList();

        log.log(
                Level.INFO,
                "Extracted {0} unique technology IDs from capabilities",
                new Object[]{uniqueTechnologyIds.size()}
        );

        // Si no hay tecnologías, retornar las capacidades sin enriquecer
        if (uniqueTechnologyIds.isEmpty()) {
            log.warning("No technology IDs found in capabilities, returning capabilities without technologies");
            return Mono.just(capabilities);
        }

        // Consultar las tecnologías desde la API externa
        return technologyRepository.findAllByIds(
                        TechnologyIds.builder()
                                .technologyIds(uniqueTechnologyIds)
                                .build()
                )
                .collectList()
                .doOnNext(technologies ->
                                  log.log(Level.INFO,
                                          "Retrieved {0} technologies from external API",
                                          new Object[]{technologies.size()}))
                .map(technologies -> attachTechnologies(capabilities, technologies));

    }

    /**
     * Paso 4: Enriquecer cada capacidad con sus tecnologías completas
     */
    private List<Capability> attachTechnologies(
            List<Capability> capabilities,
            List<Technology> allTechnologies
    ) {
        return capabilities.stream()
                .map(cap -> cap.toBuilder()
                        .technologies(
                                allTechnologies.stream()
                                        .filter(tech -> cap.getTechnologyIds().contains(tech.getId()))
                                        .toList()
                        )
                        // Opcional: limpiar los IDs ya que ahora tenemos los objetos completos
                        .technologyIds(Collections.emptyList())
                        .build()
                )
                .toList();
    }
}
