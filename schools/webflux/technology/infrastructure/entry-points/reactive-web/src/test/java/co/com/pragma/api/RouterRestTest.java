package co.com.pragma.api;

import co.com.pragma.api.handler.Handler;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.usecase.DeleteTechnologyUseCase;
import co.com.pragma.usecase.GetTechnologiesByIdsUseCase;
import co.com.pragma.usecase.RegisterTechnologyUseCase;
import co.com.pragma.validator.dto.request.TechnologyIdsRequestDto;
import co.com.pragma.validator.dto.request.TechnologyRequestDto;
import co.com.pragma.validator.dto.respose.TechnologyResponseDto;
import co.com.pragma.validator.dto.respose.TechnologySimpleResponseDto;
import co.com.pragma.validator.mappers.TechnologyApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private RegisterTechnologyUseCase registerTechnologyUseCase;
    @Mock
    private GetTechnologiesByIdsUseCase getTechnologiesByIdsUseCase;
    @Mock
    private DeleteTechnologyUseCase deleteTechnologyUseCase;
    @Mock
    private TechnologyApiMapper technologyApiMapper;
    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private Handler handler;

    @Test
    @DisplayName("Should register technology successfully")
    void registerTechnologySuccess() {
        // Arrange

        // Given
        TechnologyRequestDto requestDto = TechnologyRequestDto.builder()
                .name("WebFlux")
                .description("Stack reactivo para capacidades")
                .build();

        Technology domain = Technology.builder()
                .id("tech-1")
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .build();

        TechnologyResponseDto responseDto = TechnologyResponseDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();

        when(serverRequest.bodyToMono(TechnologyRequestDto.class)).thenReturn(Mono.just(requestDto));
        when(technologyApiMapper.toDomain(any())).thenReturn(domain);
        when(registerTechnologyUseCase.execute(any())).thenReturn(Mono.just(domain));
        when(technologyApiMapper.toResponse(any())).thenReturn(responseDto);

        // Act
        Mono<ServerResponse> result = handler.registerTechnology(serverRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get technologies by ids successfully")
    void getTechnologiesByIdsSuccess() {
        // Arrange
        TechnologyIdsRequestDto idsRequest = new TechnologyIdsRequestDto();
        idsRequest.setTechnologyIds(List.of(1L, 2L));

        when(serverRequest.bodyToMono(TechnologyIdsRequestDto.class)).thenReturn(Mono.just(idsRequest));
        when(getTechnologiesByIdsUseCase.execute(anyList())).thenReturn(Flux.just(new Technology()));
        when(technologyApiMapper.toSimpleResponse(any())).thenReturn(TechnologySimpleResponseDto.builder().build());

        // Act
        Mono<ServerResponse> result = handler.getTechnologiesByIds(serverRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete technology successfully")
    void deleteTechnologySuccess() {
        // Arrange
        String id = "1";
        when(serverRequest.pathVariable("id")).thenReturn(id);
        when(deleteTechnologyUseCase.execute(1L)).thenReturn(Mono.empty());

        // Act
        Mono<ServerResponse> result = handler.deleteTechnology(serverRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return BAD REQUEST when id format is invalid")
    void deleteTechnologyInvalidId() {
        // Arrange
        when(serverRequest.pathVariable("id")).thenReturn("abc");

        // Act
        Mono<ServerResponse> result = handler.deleteTechnology(serverRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                })
                .verifyComplete();

        verifyNoInteractions(deleteTechnologyUseCase);
    }
}