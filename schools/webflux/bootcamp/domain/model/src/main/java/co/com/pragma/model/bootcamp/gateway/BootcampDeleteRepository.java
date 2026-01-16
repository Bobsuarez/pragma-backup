package co.com.pragma.model.bootcamp.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampDeleteRepository {
    /**
     * Elimina un bootcamp y sus capacidades y tecnologías asociadas
     * Solo elimina capacidades y tecnologías si no están referenciadas por otros bootcamps
     * @param bootcampId ID del bootcamp a eliminar
     * @return Mono vacío que se completa cuando la eliminación es exitosa
     */
    Mono<Void> deleteById(Long bootcampId);

    /**
     * Obtiene los IDs de las capacidades asociadas a un bootcamp
     * @param bootcampId ID del bootcamp
     * @return Flux de IDs de capacidades
     */
    Flux<Long> getCapabilityIdsByBootcampId(Long bootcampId);

    /**
     * Verifica si una capacidad está asociada a otros bootcamps además del especificado
     * @param capabilityId ID de la capacidad
     * @param bootcampId ID del bootcamp a excluir del conteo
     * @return Mono con true si la capacidad está asociada a otros bootcamps, false en caso contrario
     */
    Mono<Boolean> isCapabilityUsedByOtherBootcamps(Long capabilityId, Long bootcampId);
}

