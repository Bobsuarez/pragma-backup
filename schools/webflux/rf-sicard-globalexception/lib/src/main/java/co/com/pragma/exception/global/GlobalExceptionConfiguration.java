package co.com.pragma.exception.global;

import co.com.pragma.exception.commands.factories.ExceptionHandlerFactory;
import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import co.com.pragma.exception.commands.handlers.impl.DefaultExceptionHandler;
import co.com.pragma.exception.dto.ResponseDTO;
import co.com.pragma.exception.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Class for handle global exceptions in webflux
 */
@Slf4j
@Configuration
@Order(-1)
@RequiredArgsConstructor
public class GlobalExceptionConfiguration implements ErrorWebExceptionHandler {

    private final ExceptionHandlerFactory handlerFactory;

    /**
     * This method handle all exceptions and use the ExceptionHandlerAdvice class
     * for parsing and map a general message output
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        log.error("Exception type: {}", ex.getClass().getSimpleName());
        log.error("Exception message: {}", ex.getMessage());
        log.error("Exception full class name: {}", ex.getClass().getName());

        if (serverHttpResponse.isCommitted()) {
            return Mono.error(ex);
        }

        // Look for specific handler first, then look for superclass for domain exceptions
        ResponseDTO<Object> responseDTO;
        Optional<ExceptionHandlerRegistry> handler = handlerFactory.getExceptionHandler(ex.getClass().getSimpleName());

        log.info("Looking for handler for: {}", ex.getClass().getSimpleName());
        log.info("Handler found: {}", handler.isPresent());

        if (handler.isPresent()) {
            log.info("Using specific handler for: {}", ex.getClass().getSimpleName());
            responseDTO = handler.get().handleException(ex);
        } else {
            log.warn("No specific handler found, using default handler");
            responseDTO = new DefaultExceptionHandler().handleException(ex);
        }

        serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        serverHttpResponse.setStatusCode(HttpStatus.valueOf(responseDTO.getCode()));

        return serverHttpResponse.writeWith(
                Mono.fromSupplier(
                        () -> ResponseUtils.buildDataBufferResponse(serverHttpResponse, responseDTO)
                )
        );
    }
}
