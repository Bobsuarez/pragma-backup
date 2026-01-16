package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.usecase.DeleteCapabilityUseCase;
import co.com.pragma.usecase.RegisterCapabilityUseCase;
import co.com.pragma.validator.dto.request.CapabilityRequestDto;
import co.com.pragma.validator.dto.respose.CapabilityResponseDto;
import co.com.pragma.validator.engine.ValidatorEngine;
import co.com.pragma.validator.mappers.CapabilityApiMapper;
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
public class CapabilityHandler {

    private final CapabilityApiMapper capabilityApiMapper;
    private final RegisterCapabilityUseCase registerCapabilityUseCase;
    private final DeleteCapabilityUseCase deleteCapabilityUseCase;


    public Mono<ServerResponse> capabilityRegister(ServerRequest serverRequest) {

        String traceId = EntryPointsUtil.extractTraceId(serverRequest);
        log.info("Processing capability registration request, traceId={}", traceId);

        return serverRequest.bodyToMono(CapabilityRequestDto.class)
                .doOnNext(dto -> log.info("Received capability request, traceId={}, name={}", traceId, dto.getName()))
                .doOnNext(ValidatorEngine::validate)
                .map(capabilityApiMapper::toDomain)
                .flatMap(domain -> {
                    log.info("Executing register capability use case, traceId={}, capabilityName={}", traceId, domain.getName());
                    return registerCapabilityUseCase.execute(domain);
                })
                .map(capabilityApiMapper::toResponse)
                .doOnNext(response -> log.info("Capability registered successfully, traceId={}, capabilityId={}", traceId, response.getId()))
                .flatMap(this::buildCreatedResponse)
                .doOnError(error -> log.error("Error processing capability registration request, traceId={}", traceId, error));
    }

    public Mono<ServerResponse> deleteCapability(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        Long capabilityId = Long.parseLong(request.pathVariable("id"));
        
        log.info("Processing delete capability request, traceId={}, capabilityId={}", traceId, capabilityId);

        return deleteCapabilityUseCase.execute(capabilityId)
                .then(ServerResponse.noContent().build())
                .doOnError(error -> log.error("Error processing delete capability request, traceId={}, capabilityId={}", 
                        traceId, capabilityId, error));
    }

    private Mono<ServerResponse> buildCreatedResponse(CapabilityResponseDto responseDto) {
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildSuccessResponse(responseDto));
    }
}
