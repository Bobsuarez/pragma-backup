package co.com.pragma.consumer.adapter;

import co.com.pragma.consumer.dto.ReportsApiResponse;
import co.com.pragma.consumer.dto.response.ApiResponse;
import co.com.pragma.consumer.util.ObjectMapperSingletonUtil;
import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.reports.gateway.ReportsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReportsAdapter implements ReportsRepository {

    private final WebClient reportsWebClient;

    public ReportsAdapter(@Qualifier("reportsWebClient") WebClient reportsWebClient) {
        this.reportsWebClient = reportsWebClient;
    }

    @Override
    public Mono<Void> sendToReports(Bootcamp bootcamp, String token, String traceId) {
        log.info("Requesting reports from external service, bootcamp={}, traceId={}",
                 ObjectMapperSingletonUtil.objectToJson(bootcamp), traceId);

        log.info("Calling external reports service, endpoint=/api/v1/bootcamp-reports, traceId={}", traceId);

        return reportsWebClient.post()
                .uri("/api/v1/bootcamp-reports")
                .header("Authorization", token != null ? token : "")
                .header("X-B3-TraceId", traceId)
                .bodyValue(bootcamp)
                .exchangeToMono(this::handlerResponse)
                .doOnNext(logs -> log.info("Received response from external reports service for bootcamp={}, traceId={}",
                                        ObjectMapperSingletonUtil.objectToJson(logs), traceId)
                )
                .then()
                .doOnSuccess(response ->
                                  log.info(
                                          "Successfully sent bootcamp to reports service, bootcamp={}, traceId={}",
                                          ObjectMapperSingletonUtil.objectToJson(bootcamp), traceId
                                  ))
                .doOnError(error -> log.error(
                        "Error sending bootcamp to reports service, traceId={}, error={}", 
                        traceId, error.getMessage()
                ));
    }


    private Mono<ReportsApiResponse> handlerResponse(ClientResponse clientResponse) {

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

        return clientResponse.bodyToMono(ReportsApiResponse.class);
    }
}
