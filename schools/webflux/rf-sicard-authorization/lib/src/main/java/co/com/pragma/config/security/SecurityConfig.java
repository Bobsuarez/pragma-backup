package co.com.pragma.config.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final CommonAuthenticationManager commonAuthenticationManager;
    private final CommonSecurityContextRepository commonSecurityContextRepository;

    private static final String[] AUTH_WHITE_LIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/api-docs/**",
            "/h2-console/**",
            "/h2/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/actuator/**",
            "/api/v1/jwt-test/**"
    };

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, List<ISecurityCustomizer> customizers) {
        return http
                .exceptionHandling((ServerHttpSecurity.ExceptionHandlingSpec exceptionHandlingSpec) ->
                                           exceptionHandlingSpec
                                                   .authenticationEntryPoint(
                                                           (ServerWebExchange swe, AuthenticationException e) -> Mono.error(
                                                                   e))
                                                   .accessDeniedHandler(
                                                           (ServerWebExchange swe, AccessDeniedException e) -> Mono.error(
                                                                   e))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(commonAuthenticationManager)
                .securityContextRepository(commonSecurityContextRepository)
                .authorizeExchange(ex -> {
                                       customizers.forEach(C -> C.customize(ex));
                                       ex.pathMatchers(AUTH_WHITE_LIST)
                                               .permitAll()
                                               .anyExchange()
                                               .authenticated();
                                   }
                )
                .build();
    }
}

