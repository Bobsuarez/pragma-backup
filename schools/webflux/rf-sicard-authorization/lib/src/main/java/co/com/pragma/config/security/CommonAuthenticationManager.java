package co.com.pragma.config.security;

import co.com.pragma.config.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommonAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String authToken = authentication.getCredentials()
                .toString();

        return jwtUtil.validateToken(Mono.just(authToken))
                .filter((Boolean valid) -> valid)
                .switchIfEmpty(Mono.error(new AuthenticationCredentialsNotFoundException("UNAUTHORIZED")))
                .map((Boolean valid) -> jwtUtil.extractAllClaims(authToken))
                .onErrorResume(e -> Mono.error(new AuthenticationCredentialsNotFoundException(e.getMessage())))
                .flatMap(dataMono ->
                                 dataMono.flatMap(claims -> {
                                     Authentication auth = new UsernamePasswordAuthenticationToken(
                                             "Jwt-user", null, jwtUtil.getGrantedValues(claims)
                                     );
                                     SecurityContextHolder.getContext()
                                             .setAuthentication(auth);
                                     return Mono.just(auth);
                                 }))
                .onErrorResume(Mono::error);
    }
}
