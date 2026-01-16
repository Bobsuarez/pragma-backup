package co.com.pragma.api.config.authority;

import co.com.pragma.config.security.ISecurityCustomizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class TechnologySecurityCustomizer implements ISecurityCustomizer {

    @Override
    public void customize(ServerHttpSecurity.AuthorizeExchangeSpec spec) {
        spec.pathMatchers(HttpMethod.POST, "/api/v1/capabilities").hasRole("ADMIN");
    }
}
