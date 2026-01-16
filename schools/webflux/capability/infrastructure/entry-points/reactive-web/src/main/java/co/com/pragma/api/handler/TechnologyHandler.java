package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.usecase.GetTechnologiesByCapabilityUseCase;
import co.com.pragma.usecase.GetTechnologyUsageCountUseCase;
import co.com.pragma.validator.dto.respose.TechnologyUsageCountResponseDto;
import co.com.pragma.validator.mappers.TechnologyApiMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.pragma.api.util.ResponseBuilder.buildSuccessResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyHandler {

    private final GetTechnologiesByCapabilityUseCase getTechnologiesByCapabilityUseCase;
    private final GetTechnologyUsageCountUseCase getTechnologyUsageCountUseCase;
    private final TechnologyApiMapper technologyApiMapper;

    public Mono<ServerResponse> getTechnologiesByCapability(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        Long capabilityId = Long.parseLong(request.pathVariable("capabilityId"));
        
        log.info("Processing get technologies by capability request, traceId={}, capabilityId={}", traceId, capabilityId);

        return getTechnologiesByCapabilityUseCase.execute(capabilityId)
                .contextWrite( ctx -> {
                        String authToken = request.headers().header("Authorization").stream().findFirst().orElse("");
                        return ctx.put("token", authToken);
                })
                .map(technologyApiMapper::toResponseDto)
                .collectList()
                .flatMap(technologies -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildSuccessResponse(technologies)))
                .doOnError(error -> log.error("Error processing get technologies by capability request, traceId={}, capabilityId={}", 
                        traceId, capabilityId, error));
    }

    public Mono<ServerResponse> getTechnologyUsageCount(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        Long technologyId = Long.parseLong(request.pathVariable("id"));
        
        log.info("Processing get technology usage count request, traceId={}, technologyId={}", traceId, technologyId);

        return getTechnologyUsageCountUseCase.execute(technologyId)
                .map(count -> TechnologyUsageCountResponseDto.builder()
                        .technologyId(technologyId)
                        .usageCount(count)
                        .build())
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildSuccessResponse(response)))
                .doOnError(error -> log.error("Error processing get technology usage count request, traceId={}, technologyId={}", 
                        traceId, technologyId, error));
    }
}
