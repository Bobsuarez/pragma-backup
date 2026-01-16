package co.com.pragma.api.handler;

import co.com.pragma.usecase.DeleteTechnologyUseCase;
import co.com.pragma.usecase.GetTechnologiesByIdsUseCase;
import co.com.pragma.validator.dto.request.TechnologyIdsRequestDto;
import co.com.pragma.validator.dto.request.TechnologyRequestDto;
import co.com.pragma.validator.dto.respose.TechnologyResponseDto;
import co.com.pragma.validator.mappers.TechnologyApiMapper;
import co.com.pragma.usecase.RegisterTechnologyUseCase;
import co.com.pragma.validator.engine.ValidatorEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterTechnologyUseCase registerTechnologyUseCase;
    private final GetTechnologiesByIdsUseCase getTechnologiesByIdsUseCase;
    private final DeleteTechnologyUseCase deleteTechnologyUseCase;
    private final TechnologyApiMapper technologyApiMapper;

    public Mono<ServerResponse> registerTechnology(ServerRequest request) {
        log.info("Handler: registerTechnology called");

        return request.bodyToMono(TechnologyRequestDto.class)
                .doOnNext(ValidatorEngine::validate)
                .map(technologyApiMapper::toDomain)
                .flatMap(registerTechnologyUseCase::execute)
                .map(technologyApiMapper::toResponse)
                .flatMap(this::buildCreatedResponse);
    }

    public Mono<ServerResponse> getTechnologiesByIds(ServerRequest request){
        log.info("Handler: getTechnologiesByIds called");

        return request.bodyToMono(TechnologyIdsRequestDto.class)
                .doOnNext(ValidatorEngine::validate)
                .flatMapMany(dto ->  getTechnologiesByIdsUseCase.execute(dto.getTechnologyIds()))
                .map(technologyApiMapper::toSimpleResponse)
                .collectList()
                .flatMap(this::buildCreatedResponse);

    }

    public Mono<ServerResponse> deleteTechnology(ServerRequest request) {
        log.info("Handler: deleteTechnology called");
        
        String idStr = request.pathVariable("id");
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("Invalid ID format: " + idStr);
        }

        return deleteTechnologyUseCase.execute(id)
                .then(ServerResponse.noContent().build())
                .doOnSuccess(response -> log.info("Technology deleted successfully, id: {}", id))
                .doOnError(error -> log.error("Error deleting technology, id: {}", id, error));
    }

    private Mono<ServerResponse> buildCreatedResponse(Object responseDto) {
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(responseDto);
    }
}

