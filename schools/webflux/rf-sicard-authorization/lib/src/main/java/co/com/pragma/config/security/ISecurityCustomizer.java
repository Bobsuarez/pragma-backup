package co.com.pragma.config.security;

import org.springframework.security.config.web.server.ServerHttpSecurity;

public interface ISecurityCustomizer {
    void customize(ServerHttpSecurity.AuthorizeExchangeSpec spec);
}
