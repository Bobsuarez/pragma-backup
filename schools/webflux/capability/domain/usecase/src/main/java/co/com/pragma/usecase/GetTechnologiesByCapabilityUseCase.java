package co.com.pragma.usecase;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import co.com.pragma.model.technology.gateway.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class GetTechnologiesByCapabilityUseCase {

    private final TechnologyRepository technologyR2dbcRepository; // Para consultar BD local
    private final TechnologyRepository technologyApiRepository; // Para consultar API externa

    public Flux<Technology> execute(Long capabilityId) {
        log.log(Level.INFO, "Executing get technologies by capability use case, capabilityId={0}", capabilityId);

        // Paso 1: Obtener los IDs de tecnologías desde la BD local
        return technologyR2dbcRepository.findTechnologyIdsByCapabilityId(capabilityId)
                .collectList()
                .doOnNext(technologyIds -> log.log(Level.INFO, 
                        "Found {0} technology IDs for capability, capabilityId={1}", 
                        new Object[]{technologyIds.size(), capabilityId}))
                .flatMapMany(technologyIds -> {
                    if (technologyIds.isEmpty()) {
                        log.warning("No technologies found for capability, capabilityId=" + capabilityId);
                        return Flux.empty();
                    }
                    
                    // Paso 2: Consultar la API externa con los IDs encontrados
                    return technologyApiRepository.findAllByIds(
                            TechnologyIds.builder()
                                    .technologyIds(technologyIds)
                                    .build()
                    );
                })
                .doOnNext(technology -> log.log(Level.FINE, 
                        "Retrieved technology from API, capabilityId={0}, technologyId={1}, name={2}", 
                        new Object[]{capabilityId, technology.getId(), technology.getName()}))
                .doOnComplete(() -> log.log(Level.INFO, 
                        "Completed get technologies by capability, capabilityId={0}", capabilityId))
                .doOnError(error -> log.severe("Error getting technologies by capability, capabilityId=" + capabilityId + ", error=" + error.getMessage()));
    }
}
