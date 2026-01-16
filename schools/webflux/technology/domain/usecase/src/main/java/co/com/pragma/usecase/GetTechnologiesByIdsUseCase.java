package co.com.pragma.usecase;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class GetTechnologiesByIdsUseCase {

    private final TechnologyRepository technologyRepository;

    public Flux<Technology> execute(List<Long> ids){
        return technologyRepository.findAllByIds(ids)
                .doOnNext( tech -> log.info("Found technology: " + tech.getName()))
                .doOnError( error -> log.log(Level.SEVERE , "Error fetching technologies: " + error.getMessage()));
    }

}
