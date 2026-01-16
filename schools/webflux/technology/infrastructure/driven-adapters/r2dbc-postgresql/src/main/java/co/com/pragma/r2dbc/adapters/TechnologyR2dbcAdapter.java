package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import co.com.pragma.r2dbc.entity.TechnologyEntity;
import co.com.pragma.r2dbc.mappers.TechnologyR2dbcMapper;
import co.com.pragma.r2dbc.repositories.TechnologyReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TechnologyR2dbcAdapter implements TechnologyRepository {

    private final TechnologyReactiveRepository technologyReactiveRepository;
    private final TechnologyR2dbcMapper technologyR2dbcMapper;

    @Override
    public Mono<Technology> save(Technology technology) {
        TechnologyEntity entity = technologyR2dbcMapper.toEntity(technology);
        return technologyReactiveRepository.save(entity)
                .map(technologyR2dbcMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return technologyReactiveRepository.existsByName(name);
    }

    @Override
    public Flux<Technology> findAllByIds(List<Long> ids) {
        return technologyReactiveRepository.findAllById(ids)
                .map(technologyR2dbcMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return technologyReactiveRepository.deleteById(id);
    }
}


