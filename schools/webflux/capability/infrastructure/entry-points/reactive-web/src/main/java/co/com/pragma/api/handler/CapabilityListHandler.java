package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.api.util.ResponseBuilder;
import co.com.pragma.usecase.CapabilityPageableUseCase;
import co.com.pragma.usecase.SearchCapabilityUseCase;
import co.com.pragma.usecase.SearchCapabilityWithTechnologiesUseCase;
import co.com.pragma.validator.dto.request.CapabilitiesIdsRequestDto;
import co.com.pragma.validator.dto.request.CapabilityIdsRequestDto;
import co.com.pragma.validator.engine.ValidatorEngine;
import co.com.pragma.validator.mappers.CapabilityListMapper;
import co.com.pragma.validator.mappers.CapabilityStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.pragma.api.util.ResponseBuilder.buildSuccessResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapabilityListHandler {

    private final CapabilityListMapper capabilityListMapper;
    private final CapabilityPageableUseCase capabilityPageableUseCase;
    private final CapabilityStatusMapper capabilityStatusMapper;
    private final SearchCapabilityUseCase capabilityUseCase;
    private final SearchCapabilityWithTechnologiesUseCase searchCapabilityWithTechnologiesUseCase;

    public Mono<ServerResponse> getAllByPageable(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        log.info("Processing get all capabilities by pageable request, traceId={}", traceId);

        String token = request.headers().header("Authorization").getFirst();

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        String sort = request.queryParam("sort").orElse("nombre");
        String dir = request.queryParam("dir").orElse("asc");

        log.debug("Query parameters - traceId={}, page={}, size={}, sort={}, dir={}", traceId, page, size, sort, dir);

        return capabilityPageableUseCase.execute(page, size, sort, dir)
                .contextWrite(ctx -> ctx.put("token", token))
                .doOnNext(pageData -> log.debug("Retrieved capabilities page, traceId={}, totalItems={}, totalPages={}", 
                        traceId, pageData.getItems().size(), pageData.getTotalElements()))
                .flatMap(this::buildCreatedResponse)
                .doOnError(error -> log.error("Error processing get all capabilities request, traceId={}", traceId, error))
                .onErrorResume(this::handleGenericError);
    }

    public Mono<ServerResponse> getListCapabilitiesIds(ServerRequest request){
        String traceId = EntryPointsUtil.extractTraceId(request);
        log.info("Processing get capabilities IDs request, traceId={}", traceId);

        return request.bodyToMono(CapabilityIdsRequestDto.class)
                .doOnNext(dto -> log.debug("Received capability IDs request, traceId={}, ids={}", traceId, dto.getCapabilityIds()))
                .doOnNext(ValidatorEngine::validate)
                .map(capabilityListMapper::toDomain)
                .flatMap(capabilityUseCase::execute)
                .map(capabilityStatusMapper::toResponse)
                .flatMap(this::buildCreatedResponseList);
    }

    public Mono<ServerResponse> getCapabilitiesByIds(ServerRequest request){

        String traceId = EntryPointsUtil.extractTraceId(request);
        log.info("Processing get capabilities IDs request, traceId={}", traceId);

        return request.bodyToMono(CapabilityIdsRequestDto.class)
                .doOnNext(dto -> log.debug("Received capability IDs request, traceId={}, ids={}", traceId, dto.getCapabilityIds()))
                .doOnNext(ValidatorEngine::validate)
                .map(capabilityListMapper::toDomain)
                .flatMap(searchCapabilityWithTechnologiesUseCase::execute)
                .contextWrite( ctx -> {
                    String token = request.headers().header("Authorization").getFirst();
                    return ctx.put("token", token);
                })
                .map(capabilityStatusMapper::toDtoList)
                .flatMap(this::buildCreatedResponseList);

    }

    private Mono<ServerResponse> buildCreatedResponseList(Object responseDto) {
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(responseDto);
    }

    private Mono<ServerResponse> buildCreatedResponse(Object responseDto) {
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildSuccessResponse(responseDto));
    }

    private Mono<ServerResponse> handleGenericError(Throwable error) {
        log.error("Handling generic error in CapabilityListHandler", error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-B3-TraceId", "traceId")
                .bodyValue(ResponseBuilder.buildErrorResponse(error.getMessage()));
    }
}
