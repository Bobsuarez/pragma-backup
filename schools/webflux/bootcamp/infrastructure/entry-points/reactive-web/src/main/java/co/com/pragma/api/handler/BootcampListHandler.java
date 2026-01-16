package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import co.com.pragma.usecase.BootcampListUseCase;
import co.com.pragma.validator.dto.request.BootcampListRequestDto;
import co.com.pragma.validator.dto.response.BootcampPageResponseDto;
import co.com.pragma.validator.mappers.BootcampListMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static co.com.pragma.api.util.ResponseBuilder.buildErrorResponse;
import static co.com.pragma.api.util.ResponseBuilder.buildSuccessResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampListHandler {

    private static final String TRACE_ID_HEADER = "X-B3-TraceId";

    private final BootcampListUseCase useCase;
    private final BootcampListMapper mapper;

    public Mono<ServerResponse> invoke(ServerRequest request) {

        String traceId = EntryPointsUtil.extractTraceId(request);

        BootcampListRequestDto requestDto = BootcampListRequestDto.builder()
                .page(getQueryParamAsInt(request, "page", 0))
                .size(getQueryParamAsInt(request, "size", 10))
                .sortField(request.queryParam("sortField").orElse("capacidades"))
                .sortDirection(request.queryParam("sortDirection").orElse("ASC"))
                .build();

        return Mono.fromCallable(() -> {
                    co.com.pragma.validator.engine.ValidatorEngine.validate(requestDto);
                    return requestDto;
                })
                .flatMap(dto -> {
                    BootcampSortField sortFieldEnum = mapper.toSortField(dto.getSortField());
                    SortDirection sortDirectionEnum = mapper.toSortDirection(dto.getSortDirection());

                    return useCase.execute(
                            dto.getPage(),
                            dto.getSize(),
                            sortFieldEnum,
                            sortDirectionEnum,
                            traceId
                    );
                })
                .contextWrite( ctx -> {
                    String token = request.headers().firstHeader("Authorization");
                    assert token != null;
                    return ctx.put("token", token);
                })
                .map(mapper::toPageResponseDto)
                .flatMap(response -> okResponse(response, traceId))
                .onErrorResume(error -> handleError(error, traceId));
    }

    private int getQueryParamAsInt(ServerRequest request, String paramName, int defaultValue) {
        return request.queryParam(paramName)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    private Mono<ServerResponse> okResponse(Object body, String traceId) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(TRACE_ID_HEADER, traceId)
                .bodyValue(buildSuccessResponse(body, traceId));
    }

    private Mono<ServerResponse> handleError(Throwable error, String traceId) {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TRACE_ID_HEADER, traceId)
                .bodyValue(buildErrorResponse("Error interno del servidor", traceId));
    }

}

