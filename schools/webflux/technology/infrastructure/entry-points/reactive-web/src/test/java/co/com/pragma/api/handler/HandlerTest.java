package co.com.pragma.api.handler;

import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.usecase.RegisterTechnologyUseCase;
import co.com.pragma.validator.dto.request.TechnologyRequestDto;
import co.com.pragma.validator.dto.respose.TechnologyResponseDto;
import co.com.pragma.validator.mappers.TechnologyApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Añade esta línea
class HandlerTest {

    @Mock
    private RegisterTechnologyUseCase registerTechnologyUseCase;

    @Mock
    private TechnologyApiMapper technologyApiMapper;

    @InjectMocks
    private Handler handler;

    private TechnologyRequestDto requestDto;
    private Technology domain;
    private TechnologyResponseDto responseDto;
    private ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        requestDto = TechnologyRequestDto.builder()
                .name("Java")
                .description("Lenguaje de programación orientado a objetos")
                .build();

        domain = Technology.builder()
                .id("tech-123")
                .name("Java")
                .description("Lenguaje de programación orientado a objetos")
                .build();

        responseDto = TechnologyResponseDto.builder()
                .id("tech-123")
                .name("Java")
                .description("Lenguaje de programación orientado a objetos")
                .build();

        serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(TechnologyRequestDto.class)).thenReturn(Mono.just(requestDto));
    }

    @Test
    void shouldRegisterTechnologySuccessfully() {
        // Given
        given(technologyApiMapper.toDomain(any(TechnologyRequestDto.class))).willReturn(domain);
        given(registerTechnologyUseCase.execute(any(Technology.class))).willReturn(Mono.just(domain));
        given(technologyApiMapper.toResponse(any(Technology.class))).willReturn(responseDto);

        // When
        Mono<ServerResponse> result = handler.registerTechnology(serverRequest);

        // Then
        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                })
                .verifyComplete();

        verify(technologyApiMapper).toDomain(any(TechnologyRequestDto.class));
        verify(registerTechnologyUseCase).execute(any(Technology.class));
        verify(technologyApiMapper).toResponse(any(Technology.class));
    }

    @Test
    void shouldHandleBusinessException() {
        // Given
        BusinessException businessException = new BusinessException("El nombre de la tecnología ya existe");
        
        given(technologyApiMapper.toDomain(any(TechnologyRequestDto.class))).willReturn(domain);
        given(registerTechnologyUseCase.execute(any(Technology.class)))
                .willReturn(Mono.error(businessException));

        // When
        Mono<ServerResponse> result = handler.registerTechnology(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(BusinessException.class)
                .verify();

        verify(technologyApiMapper).toDomain(any(TechnologyRequestDto.class));
        verify(registerTechnologyUseCase).execute(any(Technology.class));
    }

    @Test
    void shouldHandleValidationException() {
        // Given
        TechnologyRequestDto invalidRequest = TechnologyRequestDto.builder()
                .name("") // Invalid: empty name
                .description("Description")
                .build();
        
        ServerRequest invalidServerRequest = mock(ServerRequest.class);
        when(invalidServerRequest.bodyToMono(TechnologyRequestDto.class)).thenReturn(Mono.just(invalidRequest));

        // When
        Mono<ServerResponse> result = handler.registerTechnology(invalidServerRequest);

        // Then
        StepVerifier.create(result)
                .expectError()
                .verify();
    }
}

