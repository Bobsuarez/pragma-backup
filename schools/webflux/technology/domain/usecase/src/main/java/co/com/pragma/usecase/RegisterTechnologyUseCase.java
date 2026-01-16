package co.com.pragma.usecase;

import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Use case to register new Technologies.
 */
@RequiredArgsConstructor
public class RegisterTechnologyUseCase {

    private final TechnologyRepository technologyRepository;

    /**
     * Registers a new Technology applying domain rules.
     *
     * @param technology technology to be registered
     * @return the persisted Technology with its generated id (if any)
     */
    public Mono<Technology> execute(Technology technology) {
        return technologyRepository.existsByName(technology.getName())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new BusinessException("El nombre de la tecnología ya existe"));
                    }
                    return technologyRepository.save(technology);
                });
    }
}

