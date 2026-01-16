package co.com.pragma.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class BootcampConsumerConfig {

    @Bean(name = "bootcampWebClient")
    public WebClient bootcampWebClient(
            @Value("${adapter.restconsumer.bootcamp.host}") String host,
            @Value("${adapter.restconsumer.bootcamp.connectionTimeout:10000}") int connectionTimeout,
            @Value("${adapter.restconsumer.bootcamp.readTimeout:10000}") int readTimeout,
            @Value("${adapter.restconsumer.bootcamp.writeTimeout:5000}") int writeTimeout
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return WebClient.builder()
                .baseUrl(host)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
                .clientConnector(getClientHttpConnector(connectionTimeout, readTimeout, writeTimeout))
                .build();
    }

    private ClientHttpConnector getClientHttpConnector(
            int connectionTimeout, int readTimeout, int writeTimeout) {
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .compress(true)
                        .keepAlive(true)
                        .option(CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                        .doOnConnected(connection -> {
                            connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, MILLISECONDS));
                            connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, MILLISECONDS));
                        }));
    }
}
