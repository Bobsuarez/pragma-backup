package co.com.pragma.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret()
                                          .getBytes());
    }

    public Mono<Claims> extractAllClaims(String token) {

        return Mono.just(token)
                .map(data ->
                             Jwts.parser()
                                     .verifyWith(getSigningKey())
                                     .build()
                                     .parseSignedClaims(token)
                                     .getPayload())
                .doOnError(ex -> log.error("Error extracting claims from JWT", ex));
    }


    @SuppressWarnings("unchecked")
    public Set<SimpleGrantedAuthority> getGrantedValues(Claims claims) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        try {
            List<String> scopes = (List<String>) claims.get("roles");
            if (scopes != null) {
                scopes.forEach(scope -> authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase(Locale.getDefault())))
                );
            } else {
                throw new RuntimeException("Scopes variable is null");
            }
        } catch (Exception e) {
            log.error("Unexpected exception", e);
        }

        return authorities;
    }

    public Mono<Claims> validateAudience(Mono<Claims> claims) {

        return claims
                .filter(c -> {
                    System.out.println(c);
                    System.out.println(c.getAudience());
                    return jwtProperties.getAudience().equals(c.getAudience().iterator().next());
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Invalid audience. Expected: " + jwtProperties.getAudience())));
    }

    public Mono<Claims> validateExpiration(Mono<Claims> claims) {
        return claims.flatMap(c -> {
            if (c.getExpiration().after(new Date())) {
                return Mono.just(c);
            } else {
                return Mono.error(new ExpiredJwtException(null, c, "Token has expired"));
            }
        });
    }

    public Mono<Boolean> validateToken(Mono<String> token) {
        System.out.println("Validating token: " + token);
        return token
                .map(this::extractAllClaims)
                .map(this::validateAudience)
//                .map(this::validateExpiration)
                .then(Mono.just(Boolean.TRUE));
    }
}
