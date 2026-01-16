package co.com.pragma.consumer.adapter;

import co.com.pragma.consumer.dto.BootcampApiResponse;
import co.com.pragma.consumer.dto.BootcampApiWrapperResponse;
import co.com.pragma.model.Bootcamp;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@Slf4j
public class BootcampAdapter implements BootcampRepository {

    private final WebClient webClient;

    public BootcampAdapter(@Qualifier("bootcampWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Bootcamp> findById(Long id) {
        log.info("Requesting bootcamp from external service, id={}", id);

        // ✅ Usar deferContextual para acceder al contexto reactivo (tokens, traceId, etc.)
        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            log.debug("Calling external service, endpoint=/api/v1/bootcamps/{}, traceId={}", id, traceId);

            return webClient.get()
                    .uri("/api/v1/bootcamps/{id}", id)
                    .header("X-B3-TraceId", traceId)
                    .header("Authorization", token)
                    .exchangeToMono(this::handleResponse)
                    .map(BootcampApiWrapperResponse::getData)
                    .map(this::toDomain)
                    .doOnNext(response ->
                            log.debug("Received bootcamp from external service, id={}, traceId={}",
                                    response.getId(), traceId))
                    .doOnError(error ->
                            log.error("Error retrieving bootcamp from external service, id={}, traceId={}",
                                    id, traceId, error));
        });
    }

    /**
     * Maneja la respuesta HTTP
     */
    private Mono<BootcampApiWrapperResponse> handleResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        // Mapear error HTTP a excepción de dominio
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new BusinessException("El bootcamp no existe"));
                        } else if (clientResponse.statusCode().is4xxClientError()) {
                            return Mono.error(new BusinessException("Error de validación al consultar el bootcamp"));
                        } else {
                            return Mono.error(new BusinessException("Error al consultar el servicio de bootcamp"));
                        }
                    });
        }

        return clientResponse.bodyToMono(BootcampApiWrapperResponse.class);
    }

    /**
     * Convierte BootcampApiResponse a Bootcamp del dominio
     */
    private Bootcamp toDomain(BootcampApiResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }
        return Bootcamp.builder()
                .id(apiResponse.getId())
                .name(apiResponse.getName())
                .description(apiResponse.getDescription())
                .launchDate(apiResponse.getLaunchDate())
                .durationMonths(apiResponse.getDurationMonths())
                .build();
    }
}
