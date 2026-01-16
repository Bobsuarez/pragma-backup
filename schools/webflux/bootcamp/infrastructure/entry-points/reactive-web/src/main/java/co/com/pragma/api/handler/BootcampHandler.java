package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.usecase.CreateBootcampUseCase;
import co.com.pragma.usecase.DeleteBootcampUseCase;
import co.com.pragma.usecase.GetBootcampUseCase;
import co.com.pragma.validator.dto.request.BootcampRequestDto;
import co.com.pragma.validator.engine.ValidatorEngine;
import co.com.pragma.validator.mappers.BootcampMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import static co.com.pragma.api.util.ResponseBuilder.buildErrorResponse;
import static co.com.pragma.api.util.ResponseBuilder.buildSuccessResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampHandler {

    private final CreateBootcampUseCase createBootcampUseCase;
    private final DeleteBootcampUseCase deleteBootcampUseCase;
    private final GetBootcampUseCase getBootcampUseCase;
    private final BootcampMapper bootcampMapper;

    public Mono<ServerResponse> createBootcamp(ServerRequest request) {

        String traceId = EntryPointsUtil.extractTraceId(request);

        log.info("Received request to create bootcamp, traceId: {}", traceId);

        String authorizationHeader = request.headers()
                .header("Authorization")
                .getFirst();

        return request.bodyToMono(BootcampRequestDto.class)
                .doOnNext(ValidatorEngine::validate)
                .map(bootcampMapper::toDomain)
                .flatMap(bootcamp -> createBootcampUseCase.execute(
                        bootcamp, 
                        traceId, 
                        authorizationHeader != null ? authorizationHeader : ""))
                .contextWrite(ctx -> ctx.put("token", authorizationHeader != null ? authorizationHeader : ""))
                .map(bootcampMapper::toResponseDto)
                .flatMap(responseDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-B3-TraceId", traceId)
                        .bodyValue(buildSuccessResponse(responseDto, traceId)))
                .doOnError(
                        error -> log.error("Error processing bootcamp creation request, traceId: {}", traceId, error))
                .onErrorResume(error -> {
                    log.error("Error in bootcamp creation, traceId: {}", traceId, error);
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-B3-TraceId", traceId)
                            .bodyValue(buildErrorResponse(error.getMessage(), traceId));
                });
    }

    public Mono<ServerResponse> deleteBootcamp(ServerRequest request) {

        String traceId = EntryPointsUtil.extractTraceId(request);
        String authorizationHeader = request.headers()
                .header("Authorization")
                .getFirst();

        log.info("Received request to delete bootcamp, traceId: {}", traceId);

        return Mono.fromCallable(() -> Long.parseLong(request.pathVariable("id")))
                .flatMap(bootcampId -> {
                    log.info("Deleting bootcamp, bootcampId: {}, traceId: {}", bootcampId, traceId);
                    return deleteBootcampUseCase.execute(bootcampId, traceId)
                            .contextWrite(Context.of("token", authorizationHeader != null ? authorizationHeader : "", 
                                    "traceId", traceId))
                            .then(ServerResponse.noContent()
                                    .header("X-B3-TraceId", traceId)
                                    .build());
                })
                .onErrorResume(NumberFormatException.class, error -> {
                    log.error("Invalid bootcamp ID format, traceId: {}", traceId, error);
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-B3-TraceId", traceId)
                            .bodyValue(buildErrorResponse("Invalid bootcamp ID format", traceId));
                })
                .onErrorResume(error -> {
                    log.error("Error deleting bootcamp, traceId: {}", traceId, error);
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-B3-TraceId", traceId)
                            .bodyValue(buildErrorResponse(error.getMessage(), traceId));
                });
    }

    public Mono<ServerResponse> getBootcamp(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);

        log.info("Received request to get bootcamp, traceId: {}", traceId);

        return Mono.fromCallable(() -> Long.parseLong(request.pathVariable("id")))
                .flatMap(bootcampId -> {
                    log.info("Getting bootcamp, bootcampId: {}, traceId: {}", bootcampId, traceId);
                    return getBootcampUseCase.execute(bootcampId, traceId)
                            .map(bootcampMapper::toResponseDto)
                            .flatMap(responseDto -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("X-B3-TraceId", traceId)
                                    .bodyValue(buildSuccessResponse(responseDto, traceId)));
                })
                .onErrorResume(NumberFormatException.class, error -> {
                    log.error("Invalid bootcamp ID format, traceId: {}", traceId, error);
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-B3-TraceId", traceId)
                            .bodyValue(buildErrorResponse("Invalid bootcamp ID format", traceId));
                })
                .onErrorResume(error -> {
                    log.error("Error getting bootcamp, traceId: {}", traceId, error);
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-B3-TraceId", traceId)
                            .bodyValue(buildErrorResponse(error.getMessage(), traceId));
                });
    }

}


