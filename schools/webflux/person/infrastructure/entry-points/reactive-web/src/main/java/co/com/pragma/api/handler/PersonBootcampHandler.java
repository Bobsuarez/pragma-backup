package co.com.pragma.api.handler;

import co.com.pragma.api.util.EntryPointsUtil;
import co.com.pragma.api.util.ResponseBuilder;
import co.com.pragma.model.Person;
import co.com.pragma.usecase.CountPersonBootcampsUseCase;
import co.com.pragma.usecase.EnrollPersonInBootcampUseCase;
import co.com.pragma.usecase.GetPeopleByBootcampUseCase;
import co.com.pragma.validator.dto.request.PersonBootcampRequestDto;
import co.com.pragma.validator.dto.response.PersonBootcampCountResponseDto;
import co.com.pragma.validator.dto.response.PersonResponseDto;
import co.com.pragma.validator.engine.ValidatorEngine;
import co.com.pragma.validator.mappers.PersonBootcampMapper;
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
public class PersonBootcampHandler {
    
    private final EnrollPersonInBootcampUseCase enrollUseCase;
    private final CountPersonBootcampsUseCase countUseCase;
    private final GetPeopleByBootcampUseCase getPeopleUseCase;
    private final PersonBootcampMapper mapper;
    
    public Mono<ServerResponse> enrollPersonInBootcamp(ServerRequest request) {

        String traceId = EntryPointsUtil.extractTraceId(request);
        
        return request.bodyToMono(PersonBootcampRequestDto.class)
                .doOnNext(ValidatorEngine::validate)
                .flatMap(dto -> enrollUseCase.execute(dto.getPersonId(), dto.getBootcampId(), traceId))
                .contextWrite(ctx -> {
                    String authorizationHeader = request.headers()
                            .header("Authorization")
                            .getFirst();
                    return ctx.put("token", authorizationHeader);
                })
                .map(mapper::toResponseDto)
                .flatMap(responseDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-B3-TraceId", traceId)
                        .bodyValue(buildSuccessResponse(responseDto, traceId)))
                .onErrorResume(error -> handleGenericError(error, traceId))
                .doOnError(error -> log.error("Error processing enrollment request, traceId: {}", traceId, error));
    }

    public Mono<ServerResponse> countPersonBootcamps(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        Long personId = Long.parseLong(request.pathVariable("id-person"));

        return countUseCase.execute(personId, traceId)
                .map(count -> PersonBootcampCountResponseDto.builder()
                        .bootcampRegister(count)
                        .build())
                .flatMap(responseDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-B3-TraceId", traceId)
                        .bodyValue(buildSuccessResponse(responseDto, traceId)))
                .onErrorResume(error -> handleGenericError(error, traceId))
                .doOnError(error -> log.error("Error counting bootcamps for person {}, traceId: {}", personId, traceId, error));
    }

    public Mono<ServerResponse> getPeopleByBootcamp(ServerRequest request) {
        String traceId = EntryPointsUtil.extractTraceId(request);
        Long bootcampId = Long.parseLong(request.pathVariable("id"));

        return getPeopleUseCase.execute(bootcampId, traceId)
                .contextWrite(ctx -> {
                    String authorizationHeader = request.headers()
                            .header("Authorization")
                            .getFirst();
                    return ctx.put("token", authorizationHeader != null ? authorizationHeader : "");
                })
                .map(this::toPersonResponseDto)
                .collectList()
                .flatMap(people -> {
                    // Si solo hay una persona, retornar el objeto directamente; si hay más, retornar la lista
                    if (people.size() == 1) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-B3-TraceId", traceId)
                                .bodyValue(buildSuccessResponse(people.get(0), traceId));
                    } else {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-B3-TraceId", traceId)
                                .bodyValue(buildSuccessResponse(people, traceId));
                    }
                })
                .onErrorResume(error -> handleGenericError(error, traceId))
                .doOnError(error -> log.error("Error getting people for bootcamp {}, traceId: {}", bootcampId, traceId, error));
    }

    private PersonResponseDto toPersonResponseDto(Person person) {
        if (person == null) {
            return null;
        }
        return PersonResponseDto.builder()
                .id(person.getId())
                .name(person.getName())
                .email(person.getEmail())
                .build();
    }

    private Mono<ServerResponse> handleGenericError(Throwable error, String traceId) {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-B3-TraceId", traceId)
                .bodyValue(ResponseBuilder.buildErrorResponse(error.getMessage(), traceId));
    }
}

