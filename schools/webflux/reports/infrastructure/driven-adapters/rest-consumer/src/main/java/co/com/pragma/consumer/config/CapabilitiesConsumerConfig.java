package co.com.pragma.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class CapabilitiesConsumerConfig {

    @Bean(name = "capabilitiesWebClient")
    public WebClient capabilitiesWebClient(
            @Value("${adapter.restconsumer.capabilities.host:http://localhost:8081}") String host,
            @Value("${adapter.restconsumer.capabilities.timeout:10000}") int timeout) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        return WebClient.builder()
                .baseUrl(host)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
                .clientConnector(getClientHttpConnector(timeout))
                .build();
    }

    private ClientHttpConnector getClientHttpConnector(int timeout) {
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .compress(true)
                        .keepAlive(true)
                        .option(CONNECT_TIMEOUT_MILLIS, timeout)
                        .doOnConnected(connection -> {
                            connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                            connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
                        }));
    }
}
