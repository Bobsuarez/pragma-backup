package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.api.util.ResponseBuilder;
import co.com.pragma.usecase.GetBootcampDetailUseCase;
import co.com.pragma.validator.dto.response.BootcampDetailResponseDto;
import co.com.pragma.validator.mappers.BootcampDetailMapper;
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
public class BootcampDetailHandler {
    
    private final GetBootcampDetailUseCase getBootcampDetailUseCase;
    private final BootcampDetailMapper bootcampDetailMapper;
    
    public Mono<ServerResponse> getBootcampDetail(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        String token = extractAuthorizationToken(request);
        
        Long bootcampId = Long.parseLong(request.pathVariable("bootcampId"));
        
        log.info("Getting bootcamp detail, bootcampId={}, traceId={}", bootcampId, traceId);
        
        return getBootcampDetailUseCase.execute(bootcampId, traceId, token)
                .map(bootcampDetailMapper::toResponseDto)
                .flatMap(responseDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-B3-TraceId", traceId)
                        .bodyValue(ResponseBuilder.buildSuccessResponse(responseDto, traceId)))
                .doOnSuccess(response -> log.info("Bootcamp detail retrieved successfully, bootcampId={}, traceId={}", 
                        bootcampId, traceId))
                .doOnError(error -> log.error("Error getting bootcamp detail, bootcampId={}, traceId={}", 
                        bootcampId, traceId, error))
                .onErrorResume(this::handleError);
    }
    
    private String extractAuthorizationToken(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader;
        }
        return authHeader != null ? authHeader : "";
    }
    
    private Mono<ServerResponse> handleError(Throwable error) {
        log.error("Error handling bootcamp detail request", error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ResponseBuilder.buildErrorResponse(error.getMessage(), "N/A"));
    }
}
