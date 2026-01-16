package co.com.pragma.model.capability.gateway;

import co.com.pragma.model.capability.CapabilityIds;
import co.com.pragma.model.capability.CapabilityListResult;
import co.com.pragma.model.capability.CapabilityValidationResult;
import co.com.pragma.model.technology.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapabilityRepository {

    Mono<CapabilityValidationResult> findAllByIds(CapabilityIds ids);

    Flux<CapabilityListResult> findAllTechnologiesByIds(CapabilityIds ids);

    /**
     * Obtiene las tecnologías asociadas a una capacidad desde la API externa
     * @param capabilityId ID de la capacidad
     * @return Flux de tecnologías
     */
    Flux<Technology> getTechnologiesByCapabilityId(Long capabilityId);

    /**
     * Obtiene el conteo de uso de una tecnología (cuántas capacidades la usan)
     * @param technologyId ID de la tecnología
     * @return Mono con el conteo de uso
     */
    Mono<Long> getTechnologyUsageCount(Long technologyId);

    /**
     * Elimina una capacidad en la API externa
     * @param capabilityId ID de la capacidad a eliminar
     * @return Mono vacío que se completa cuando la eliminación es exitosa
     */
    Mono<Void> deleteCapability(Long capabilityId);

    /**
     * Elimina una tecnología en la API externa
     * @param technologyId ID de la tecnología a eliminar
     * @return Mono vacío que se completa cuando la eliminación es exitosa
     */
    Mono<Void> deleteTechnology(Long technologyId);
}

