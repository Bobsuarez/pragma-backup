package co.com.pragma.consumer.adapter;

import co.com.pragma.consumer.dto.CapabilityApiResponse;
import co.com.pragma.consumer.dto.TechnologyApiResponse;
import co.com.pragma.consumer.dto.TechnologyUsageCountResponse;
import co.com.pragma.consumer.dto.response.ApiResponse;
import co.com.pragma.consumer.util.ObjectMapperSingletonUtil;
import co.com.pragma.model.capability.CapabilityIds;
import co.com.pragma.model.capability.CapabilityListResult;
import co.com.pragma.model.capability.CapabilityValidationResult;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.technology.Technology;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CapabilityAdapter implements CapabilityRepository {

    private final WebClient webClient;

    public CapabilityAdapter(@Qualifier("capabilityWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<CapabilityValidationResult> findAllByIds(CapabilityIds ids) {
        log.info("Requesting capability from external service, capabilityId={}", ObjectMapperSingletonUtil.objectToJson(ids));

        return Mono.deferContextual(ctx -> {
            String token = ctx.get("token"); // Lo recuperas del contexto
            log.debug("Calling external technology service, endpoint=/api/v1/technologies/items");

            return webClient.post()
                    .uri("/api/v1/capabilities/items")
                    .header("Authorization", token)
                    .bodyValue(ids)
                    .exchangeToMono(this::handlerResponse)
                    .map(this::builderToDomain)
                    .doOnNext(tech ->
                                      log.debug(
                                              "Received technology from external service, isValidate={}, message={}",
                                              tech.getIsValidate(), tech.getMessage()
                                      ))
                    .doOnError(error -> log.error(
                            "Error retrieving technologies from external service, technologyIdsCount={}",
                            ids.getCapabilityIds() != null ? ids.getCapabilityIds()
                                    .size() : 0, error
                    ));
        });
    }

    private CapabilityValidationResult builderToDomain(CapabilityApiResponse response) {
        return CapabilityValidationResult.builder()
                .isValidate(response.getIsExisting())
                .message(response.getMessage())
                .build();
    }

    @Override
    public Flux<CapabilityListResult> findAllTechnologiesByIds(CapabilityIds ids) {

        log.info("Requesting from external service, capabilityId={}", ObjectMapperSingletonUtil.objectToJson(ids));

        return Flux.deferContextual(ctx -> {
            String token = ctx.get("token"); // Lo recuperas del contexto
            log.info("Calling external technology service, endpoint=/api/v1/technologies/items");

            return webClient.post()
                    .uri("/api/v1/capabilities/technologies/items")
                    .header("Authorization", token)
                    .bodyValue(ids)
                    .exchangeToFlux(this::handlerResponseFlux)
                    .doOnNext(tech ->
                                      log.debug(
                                              "Received technology from external service, isValidate={}, message={}",
                                              tech.getId(), tech.getName()
                                      ))
                    .doOnError(error -> log.error(
                            "Error retrieving technologies from external service, technologyIdsCount={}",
                            ids.getCapabilityIds() != null ? ids.getCapabilityIds()
                                    .size() : 0, error
                    ));
        });
    }

    private Mono<CapabilityApiResponse> handlerResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ApiResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers()
                                        .asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException(body.getMessage(), body)
                        );
                    });
        }

        return clientResponse.bodyToMono(CapabilityApiResponse.class);
    }

    private Flux<CapabilityListResult> handlerResponseFlux(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {

            return clientResponse.bodyToFlux(CapabilityListResult.class)
                    .flatMap(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException(body.getName(), body)
                        );
                    });
        }

        return clientResponse.bodyToFlux(CapabilityListResult.class);
    }

    @Override
    public Flux<Technology> getTechnologiesByCapabilityId(Long capabilityId) {
        log.info("Requesting technologies by capability from external service, capabilityId={}", capabilityId);

        return Flux.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");
            
            log.debug("Calling external service, endpoint=/api/v1/technologies/by-capability/{}, traceId={}", 
                capabilityId, traceId);

            return webClient.get()
                    .uri("/api/v1/technologies/by-capability/{id}", capabilityId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToFlux(this::handleTechnologyResponseFlux)
                    .doOnNext(tech -> 
                        log.debug("Received technology from external service, id={}, name={}, traceId={}", 
                            tech.getId(), tech.getName(), traceId))
                    .doOnError(error -> 
                        log.error("Error retrieving technologies by capability, capabilityId={}, traceId={}", 
                            capabilityId, traceId, error));
        });
    }

    @Override
    public Mono<Long> getTechnologyUsageCount(Long technologyId) {
        log.info("Requesting technology usage count from external service, technologyId={}", technologyId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");
            
            log.debug("Calling external service, endpoint=/api/v1/technologies/{}/usage-count, traceId={}", 
                technologyId, traceId);

            return webClient.get()
                    .uri("/api/v1/technologies/{id}/usage-count", technologyId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleUsageCountResponse)
                    .doOnNext(count -> 
                        log.debug("Received technology usage count, technologyId={}, count={}, traceId={}", 
                            technologyId, count, traceId))
                    .doOnError(error -> 
                        log.error("Error retrieving technology usage count, technologyId={}, traceId={}", 
                            technologyId, traceId, error));
        });
    }

    @Override
    public Mono<Void> deleteCapability(Long capabilityId) {
        log.info("Deleting capability from external service, capabilityId={}", capabilityId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");
            
            log.debug("Calling external service DELETE, endpoint=/api/v1/capabilities/{}, traceId={}", 
                capabilityId, traceId);

            return webClient.delete()
                    .uri("/api/v1/capabilities/{id}", capabilityId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleDeleteResponse)
                    .doOnSuccess(v -> 
                        log.info("Capability deleted successfully, capabilityId={}, traceId={}", 
                            capabilityId, traceId))
                    .doOnError(error -> 
                        log.error("Error deleting capability, capabilityId={}, traceId={}", 
                            capabilityId, traceId, error));
        });
    }

    @Override
    public Mono<Void> deleteTechnology(Long technologyId) {
        log.info("Deleting technology from external service, technologyId={}", technologyId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");
            
            log.debug("Calling external service DELETE, endpoint=/api/v1/technologies/{}, traceId={}", 
                technologyId, traceId);

            return webClient.delete()
                    .uri("/api/v1/technologies/{id}", technologyId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleDeleteResponse)
                    .doOnSuccess(v -> 
                        log.info("Technology deleted successfully, technologyId={}, traceId={}", 
                            technologyId, traceId))
                    .doOnError(error -> 
                        log.error("Error deleting technology, technologyId={}, traceId={}", 
                            technologyId, traceId, error));
        });
    }

    private Flux<Technology> handleTechnologyResponseFlux(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ApiResponse.class)
                    .flatMapMany(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException(body.getMessage(), body)
                        );
                    });
        }

        return clientResponse.bodyToFlux(TechnologyApiResponse.class)
                .map(techResponse -> Technology.builder()
                        .id(techResponse.getId())
                        .name(techResponse.getName())
                        .build());
    }

    private Mono<Long> handleUsageCountResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ApiResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException(body.getMessage(), body)
                        );
                    });
        }

        return clientResponse.bodyToMono(TechnologyUsageCountResponse.class)
                .map(response -> response.getCount() != null ? response.getCount() : 0L);
    }

    private Mono<Void> handleDeleteResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ApiResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException(body.getMessage(), body)
                        );
                    });
        }

        return clientResponse.bodyToMono(Void.class)
                .switchIfEmpty(Mono.empty());
    }

}
