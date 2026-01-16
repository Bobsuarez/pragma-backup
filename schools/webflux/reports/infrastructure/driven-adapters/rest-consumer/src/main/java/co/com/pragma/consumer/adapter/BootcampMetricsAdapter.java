package co.com.pragma.consumer.adapter;

import co.com.pragma.consumer.dto.ApiErrorResponse;
import co.com.pragma.consumer.dto.BootcampApiResponse;
import co.com.pragma.consumer.dto.CapabilitiesTechnologiesRequest;
import co.com.pragma.consumer.dto.CapabilityWithTechnologiesResponse;
import co.com.pragma.consumer.dto.PersonBootcampApiResponse;
import co.com.pragma.consumer.mappers.BootcampDetailConsumerMapper;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampMetricsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BootcampMetricsAdapter implements BootcampMetricsRepository {

    private final WebClient bootcampWebClient;
    private final WebClient capabilitiesWebClient;
    private final WebClient personBootcampWebClient;
    private final BootcampDetailConsumerMapper bootcampDetailConsumerMapper;

    public BootcampMetricsAdapter(
            @Qualifier("bootcampWebClient") WebClient bootcampWebClient,
            @Qualifier("capabilitiesWebClient") WebClient capabilitiesWebClient,
            @Qualifier("personBootcampWebClient") WebClient personBootcampWebClient,
            BootcampDetailConsumerMapper bootcampDetailConsumerMapper
    ) {
        this.bootcampWebClient = bootcampWebClient;
        this.capabilitiesWebClient = capabilitiesWebClient;
        this.personBootcampWebClient = personBootcampWebClient;
        this.bootcampDetailConsumerMapper = bootcampDetailConsumerMapper;
    }

    @Override
    public Mono<BootcampInfo> getBootcampInfo(Long bootcampId) {
        log.info("Requesting bootcamp info from external service, bootcampId={}", bootcampId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            log.info("token={}, traceId={}", token, traceId);

            log.debug("Calling bootcamp service, endpoint=/api/v1/bootcamps/{}, traceId={}", bootcampId, traceId);

            return bootcampWebClient.get()
                    .uri("/api/v1/bootcamps/{id}", bootcampId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleBootcampResponse)
                    .map(response -> {
                        BootcampApiResponse.BootcampData data = response.getData();
                        return new BootcampInfo(
                                data.getId(),
                                data.getName(),
                                data.getDescription(),
                                data.getLaunchDate(),
                                data.getDurationMonths()
                        );
                    })
                    .doOnNext(info -> log.debug(
                            "Received bootcamp info, id={}, name={}, traceId={}",
                            info.id(), info.name(), traceId
                    ))
                    .doOnError(error -> log.error(
                            "Error retrieving bootcamp info, bootcampId={}, traceId={}",
                            bootcampId, traceId, error
                    ));
        });
    }

    @Override
    public Mono<Integer> countCapabilitiesByBootcampId(Long bootcampId) {
        log.info("Counting capabilities for bootcamp, bootcampId={}", bootcampId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            log.info("token={}, traceId={}", token, traceId);

            return bootcampWebClient.get()
                    .uri("/api/v1/bootcamps/{id}", bootcampId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleBootcampResponse)
                    .map(bootcampResponse -> {
                        List<BootcampApiResponse.CapabilityRef> capabilities =
                                bootcampResponse.getData()
                                        .getCapabilities();
                        return capabilities != null ? capabilities.size() : 0;
                    })
                    .doOnNext(count -> log.debug("Capabilities count: {}, traceId={}", count, traceId))
                    .doOnError(error -> log.error(
                            "Error counting capabilities, bootcampId={}, traceId={}",
                            bootcampId, traceId, error
                    ))
                    .defaultIfEmpty(0);
        });
    }

    @Override
    public Mono<Integer> countTechnologiesByBootcampId(Long bootcampId) {
        log.info("Counting technologies for bootcamp, bootcampId={}", bootcampId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            return bootcampWebClient.get()
                    .uri("/api/v1/bootcamps/{id}", bootcampId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleBootcampResponse)
                    .flatMap(bootcampResponse -> {
                        List<BootcampApiResponse.CapabilityRef> capabilities =
                                bootcampResponse.getData()
                                        .getCapabilities();
                        if (capabilities == null || capabilities.isEmpty()) {
                            return Mono.just(0);
                        }

                        List<String> capabilityIds = capabilities.stream()
                                .map(cap -> String.valueOf(cap.getId()))
                                .collect(Collectors.toList());

                        CapabilitiesTechnologiesRequest request = CapabilitiesTechnologiesRequest.builder()
                                .capabilityIds(capabilityIds)
                                .build();

                        return capabilitiesWebClient.post()
                                .uri("/api/v1/capabilities/technologies/items")
                                .header("Authorization", token)
                                .header("X-B3-TraceId", traceId)
                                .bodyValue(request)
                                .exchangeToFlux(this::handleCapabilitiesResponse)
                                .flatMap(capability -> Flux.fromIterable(
                                        capability.getTechnologies() !=
                                                null ? capability.getTechnologies() : List.of()))
                                .collectList()
                                .map(List::size)
                                .doOnNext(
                                        count -> log.debug("Total technologies count: {}, traceId={}", count, traceId))
                                .doOnError(error -> log.error(
                                        "Error counting technologies, bootcampId={}, traceId={}",
                                        bootcampId, traceId, error
                                ));
                    })
                    .defaultIfEmpty(0);
        });
    }

    @Override
    public Mono<Integer> countEnrolledPeopleByBootcampId(Long bootcampId) {
        log.info("Counting enrolled people for bootcamp, bootcampId={}", bootcampId);

        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            log.debug(
                    "Calling person-bootcamp service, endpoint=/api/v1/person-bootcamp/{}, traceId={}", bootcampId,
                    traceId
            );

            return personBootcampWebClient.get()
                    .uri("/api/v1/person-bootcamp/{id}", bootcampId)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handlePersonBootcampResponse)
                    .map(response -> {
                        PersonBootcampApiResponse.PersonBootcampData data = response.getData();
                        return data.getBootcampRegister() != null ? data.getBootcampRegister() : 0;
                    })
                    .doOnNext(count -> log.debug("Enrolled people count: {}, traceId={}", count, traceId))
                    .doOnError(error -> log.error(
                            "Error counting enrolled people, bootcampId={}, traceId={}",
                            bootcampId, traceId, error
                    ));
        });
    }

    private Mono<BootcampApiResponse> handleBootcampResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode()
                .isError()) {
            return clientResponse.bodyToMono(ApiErrorResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "Bootcamp WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers()
                                        .asHttpHeaders(),
                                body
                        );
                        return Mono.error(new BusinessException("Error retrieving bootcamp: " + body.getMessage()));
                    });
        }
        return clientResponse.bodyToMono(BootcampApiResponse.class);
    }

    private Flux<CapabilityWithTechnologiesResponse> handleCapabilitiesResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode()
                .isError()) {
            return clientResponse.bodyToMono(ApiErrorResponse.class)
                    .flatMapMany(body -> {
                        log.error(
                                "Capabilities WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers()
                                        .asHttpHeaders(),
                                body
                        );
                        return Mono.error(new BusinessException("Error retrieving capabilities: " + body.getMessage()));
                    });
        }
        return clientResponse.bodyToFlux(CapabilityWithTechnologiesResponse.class);
    }

    private Mono<PersonBootcampApiResponse> handlePersonBootcampResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode()
                .isError()) {
            return clientResponse.bodyToMono(ApiErrorResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "PersonBootcamp WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers()
                                        .asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException("Error retrieving person-bootcamp: " + body.getMessage()));
                    });
        }
        return clientResponse.bodyToMono(PersonBootcampApiResponse.class);
    }
}
