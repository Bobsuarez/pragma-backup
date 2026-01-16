package co.com.pragma.consumer.adapter;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import co.com.pragma.model.technology.gateway.TechnologyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component("technologyAdapter")
@Primary
@Slf4j
public class TechnologyAdapter implements TechnologyRepository {

    private final WebClient webClient;

    public TechnologyAdapter(@Qualifier("technologyWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Technology> findAllByIds(TechnologyIds ids) {
        log.debug("Requesting technologies from external service, technologyIdsCount={}", 
                ids.getTechnologyIds() != null ? ids.getTechnologyIds().size() : 0);
        
        return Flux.deferContextual(ctx -> {
            String token = ctx.get("token"); // Lo recuperas del contexto
            log.debug("Calling external technology service, endpoint=/api/v1/technologies/items");
            
            return webClient.post()
                    .uri("/api/v1/technologies/items")
                    .header("Authorization", token)
                    .bodyValue(ids)
                    .retrieve()
                    .bodyToFlux(Technology.class)
                    .doOnNext(tech -> log.debug("Received technology from external service, technologyId={}, technologyName={}", 
                            tech.getId(), tech.getName()))
                    .doOnComplete(() -> log.info("Successfully retrieved all technologies from external service, requestedCount={}", 
                            ids.getTechnologyIds() != null ? ids.getTechnologyIds().size() : 0))
                    .doOnError(error -> log.error("Error retrieving technologies from external service, technologyIdsCount={}", 
                            ids.getTechnologyIds() != null ? ids.getTechnologyIds().size() : 0, error));
        });
    }

    @Override
    public Flux<Long> findTechnologyIdsByCapabilityId(Long capabilityId) {
        // Este método se implementa en TechnologyR2dbcAdapter
        return Flux.error(new UnsupportedOperationException("This method should be implemented by TechnologyR2dbcAdapter"));
    }

    @Override
    public Mono<Long> getUsageCount(Long technologyId) {
        // Este método se implementa en TechnologyR2dbcAdapter
        return Mono.error(new UnsupportedOperationException("This method should be implemented by TechnologyR2dbcAdapter"));
    }
}
