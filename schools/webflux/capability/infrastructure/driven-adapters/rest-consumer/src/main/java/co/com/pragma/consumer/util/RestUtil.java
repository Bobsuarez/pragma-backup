package co.com.pragma.consumer.util;

import co.com.pragma.model.exceptions.BusinessException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@UtilityClass
@Slf4j
public class RestUtil {

    public final String LOG_WEBCLIENT_ERROR = "WebClient error: {} - {}";

    public WebClient buildWebClient(
            String host, HttpHeaders headers, int connectionTimeout, int readTimeout, int writeTimeout) {

        return WebClient.builder()
                .baseUrl(host)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
                .clientConnector(getClientHttpConnector(connectionTimeout, readTimeout, writeTimeout))
                .build();
    }

    public ClientHttpConnector getClientHttpConnector(
            int connectionTimeout, int readTimeout, int writeTimeout) {

        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .compress(true)
                        .keepAlive(true)
                        .option(CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                        .doOnConnected(
                                connection -> {
                                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, MILLISECONDS));
                                    connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, MILLISECONDS));
                                }));
    }

    public BusinessException mapWebClientException(WebClientResponseException ex, String traceId) {
        log.error(LOG_WEBCLIENT_ERROR, ex.getStatusCode(), ex.getResponseBodyAsString());

        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new BusinessException("ERROR_NOT_FOUND");
        } else if (ex.getStatusCode()
                .is4xxClientError()) {
            return new BusinessException("VALIDATION_ERROR");
        } else if (ex.getStatusCode()
                .is5xxServerError()) {
            return new BusinessException("ERROR_INTERNAL");
        }

        return new BusinessException("ERROR_INTERNAL");
    }
}
