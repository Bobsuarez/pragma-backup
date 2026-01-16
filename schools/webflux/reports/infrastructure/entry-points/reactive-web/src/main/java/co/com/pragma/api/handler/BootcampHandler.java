package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.api.util.ResponseBuilder;
import co.com.pragma.usecase.CreateBootcampUseCase;
import co.com.pragma.validator.dto.request.BootcampRequestDto;
import co.com.pragma.validator.engine.ValidatorEngine;
import co.com.pragma.validator.mappers.BootcampMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampHandler {
    
    private final CreateBootcampUseCase createBootcampUseCase;
    private final BootcampMapper mapper;
    
    public Mono<ServerResponse> createBootcamp(ServerRequest request) {

        String traceId = EntryPointsUtil.extractTraceId(request);
        String authToken = request.headers().header("Authorization").stream().findFirst().orElse("");
        log.info("Extracted auth token for traceId={}: {}", traceId, authToken);
        
        return request.bodyToMono(BootcampRequestDto.class)
                .doOnNext(ValidatorEngine::validate)
                .map(mapper::toDomain)
                .flatMap(bootcamp -> createBootcampUseCase.execute(bootcamp, traceId, authToken))
                .map(mapper::toResponseDto)
                .flatMap(responseDto -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-B3-TraceId", traceId)
                        .bodyValue(ResponseBuilder.buildSuccessResponse(responseDto, traceId)))
                .onErrorResume(this::handleError);
    }

    
    private Mono<ServerResponse> handleError(Throwable error) {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ResponseBuilder.buildErrorResponse(error.getMessage(), "N/A"));
    }
}

