package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import co.com.pragma.usecase.BootcampListUseCase;
import co.com.pragma.usecase.DeleteBootcampUseCase;
import co.com.pragma.validator.dto.request.BootcampListRequestDto;
import co.com.pragma.validator.mappers.BootcampListMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.pragma.api.util.ResponseBuilder.buildErrorResponse;
import static co.com.pragma.api.util.ResponseBuilder.buildSuccessResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampDelete {

    private static final String X_B3_TRACE_ID = "X-B3-TraceId";

    private final DeleteBootcampUseCase deleteBootcampUseCase;

    public Mono<ServerResponse> deleteBootcamp(ServerRequest request) {

        String traceId = EntryPointsUtil.extractTraceId(request);

        return Mono.fromCallable(() -> Long.parseLong(request.pathVariable("id")))
                .flatMap(bootcampId -> {
                    log.info("Deleting bootcamp, bootcampId: {}, traceId: {}", bootcampId, traceId);
                    return deleteBootcampUseCase.execute(bootcampId, traceId)
                            .then(ServerResponse.noContent()
                                          .header(X_B3_TRACE_ID, traceId)
                                          .build());
                })
                .onErrorResume(NumberFormatException.class , error -> {
                    log.error("Invalid bootcamp ID format, traceId: {}", traceId, error);
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(X_B3_TRACE_ID, traceId)
                            .bodyValue(buildErrorResponse("Invalid bootcamp ID format", traceId));
                })
                .onErrorResume(error -> {
                    log.error("Error deleting bootcamp, traceId: {}", traceId, error);
                    return ServerResponse.status(500)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(X_B3_TRACE_ID, traceId)
                            .bodyValue(buildErrorResponse(error.getMessage(), traceId));
                });
    }

}

