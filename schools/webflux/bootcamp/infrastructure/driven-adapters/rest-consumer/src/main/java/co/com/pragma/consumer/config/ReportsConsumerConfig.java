package co.com.pragma.consumer.config;

import co.com.pragma.consumer.util.RestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ReportsConsumerConfig {

    @Bean(name = "reportsWebClient")
    public WebClient reportsWebClient(
            @Value("${adapter.reports.host}") String host
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return RestUtil.buildWebClient(
                host,
                headers,
                10000,
                10000,
                5000
        );
    }

}
