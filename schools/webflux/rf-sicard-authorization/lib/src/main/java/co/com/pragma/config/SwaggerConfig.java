package co.com.pragma.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.title}")
    private String titleDescription;

    @Value("${swagger.urlServer}")
    private String urlServer;

    @Value("${swagger.version}")
    private String version;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                              .title(titleDescription)
                              .description(
                                      "This microservice provides user registration functionality following Clean Architecture principles.")
                              .version(version)
                              .contact(new Contact()
                                               .name("Pragma Team")
                                               .email("dev@pragma.com")
                                               .url("https://www.pragma.com"))
                              .license(new License()
                                               .name("MIT License")
                                               .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(urlServer)
                                .description("Local Development Server")
                ));
    }
}
