package co.com.pragma.consumer.adapter;

import co.com.pragma.consumer.dto.ApiErrorResponse;
import co.com.pragma.consumer.dto.BootcampApiResponse;
import co.com.pragma.consumer.dto.CapabilitiesTechnologiesRequest;
import co.com.pragma.consumer.dto.CapabilityWithTechnologiesResponse;
import co.com.pragma.consumer.dto.PersonBootcampListResponse;
import co.com.pragma.consumer.mappers.BootcampDetailConsumerMapper;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;

@Component
@Slf4j
public class BootcampMetricsResponseDataAdapter implements  BootcampDetailRepository {

    private final WebClient bootcampWebClient;
    private final WebClient capabilitiesWebClient;
    private final WebClient personBootcampWebClient;
    private final BootcampDetailConsumerMapper bootcampDetailConsumerMapper;

    public BootcampMetricsResponseDataAdapter(
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
    public Mono<BootcampDetail> getBootcampDetail(Long bootcampId) {

        return Mono.deferContextual(ctx -> {

            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            log.info("token: {}, traceId: {}", token, traceId);

            return getBootcamp(token, traceId, bootcampId)
                    .flatMap(bootcamp ->
                                     Mono.zip(
                                             Mono.just(bootcamp),
                                             getCapabilities(token, traceId, bootcamp),
                                             getPeople(token, traceId, bootcampId)
                                     )
                    )
                    .map(this::buildBootcampDetail);
        });
    }

    private Mono<BootcampApiResponse> getBootcamp(String token, String traceId, Long id) {
        return bootcampWebClient.get()
                .uri("/api/v1/bootcamps/{id}", id)
                .headers(h -> applyHeaders(h, token, traceId))
                .exchangeToMono(this::handleBootcampResponse)
                .cache();
    }

    private Mono<List<CapabilityDetail>> getCapabilities(
            String token,
            String traceId,
            BootcampApiResponse bootcamp
    ) {

        List<String> capabilityIds = bootcamp.getData()
                .getCapabilities()
                .stream()
                .map(c -> String.valueOf(c.getId()))
                .toList();

        if (capabilityIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return capabilitiesWebClient.post()
                .uri("/api/v1/capabilities/technologies/items")
                .headers(h -> applyHeaders(h, token, traceId))
                .bodyValue(new CapabilitiesTechnologiesRequest(capabilityIds))
                .exchangeToFlux(this::handleCapabilitiesResponse)
                .collectList()
                .map(bootcampDetailConsumerMapper::toCapabilityDetailList)
                .onErrorReturn(List.of());
    }

    private Mono<List<EnrolledPerson>> getPeople(
            String token,
            String traceId,
            Long bootcampId
    ) {

        return personBootcampWebClient.get()
                .uri("/api/v1/person-bootcamp/{id}/people", bootcampId)
                .headers(h -> applyHeaders(h, token, traceId))
                .exchangeToMono(this::handlePersonBootcampListResponse)
                .map(r -> bootcampDetailConsumerMapper.toEnrolledPersonList(r.getData()))
                .onErrorReturn(List.of());
    }

    private BootcampDetail buildBootcampDetail(
            Tuple3<BootcampApiResponse, List<CapabilityDetail>, List<EnrolledPerson>> tuple
    ) {
        var base = bootcampDetailConsumerMapper.toBootcampDetail(tuple.getT1().getData());

        return new BootcampDetail(
                base.id(),
                base.name(),
                base.description(),
                base.launchDate(),
                base.durationMonths(),
                tuple.getT2(),
                tuple.getT3()
        );
    }

    private void applyHeaders(HttpHeaders headers, String token, String traceId) {
        headers.set("Authorization", token);
        headers.set("X-B3-TraceId", traceId);
    }

    private Mono<PersonBootcampListResponse> handlePersonBootcampListResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode()
                .isError()) {
            return clientResponse.bodyToMono(ApiErrorResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "PersonBootcampList WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers()
                                        .asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException("Error retrieving person-bootcamp list: " + body.getMessage()));
                    });
        }
        return clientResponse.bodyToMono(PersonBootcampListResponse.class);
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

}
