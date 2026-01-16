package co.com.pragma.usecase;

import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterTechnologyUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private RegisterTechnologyUseCase registerTechnologyUseCase;

    private Technology technology;

    @BeforeEach
    void setUp() {
        technology = Technology.builder()
                .name("Java")
                .description("Lenguaje de programación orientado a objetos")
                .build();
    }

    @Test
    void shouldRegisterTechnologySuccessfullyWhenNameDoesNotExist() {
        // Given
        Technology savedTechnology = Technology.builder()
                .id("tech-123")
                .name("Java")
                .description("Lenguaje de programación orientado a objetos")
                .build();

        given(technologyRepository.existsByName("Java")).willReturn(Mono.just(false));
        given(technologyRepository.save(any(Technology.class))).willReturn(Mono.just(savedTechnology));

        // When
        Mono<Technology> result = registerTechnologyUseCase.execute(technology);

        // Then
        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo("tech-123");
                    assertThat(saved.getName()).isEqualTo("Java");
                    assertThat(saved.getDescription()).isEqualTo("Lenguaje de programación orientado a objetos");
                })
                .verifyComplete();

        verify(technologyRepository).existsByName("Java");
        verify(technologyRepository).save(any(Technology.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenTechnologyNameAlreadyExists() {
        // Given
        given(technologyRepository.existsByName("Java")).willReturn(Mono.just(true));

        // When
        Mono<Technology> result = registerTechnologyUseCase.execute(technology);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> {
                    assertThat(throwable).isInstanceOf(BusinessException.class);
                    assertThat(throwable.getMessage()).isEqualTo("El nombre de la tecnología ya existe");
                    return true;
                })
                .verify();

        verify(technologyRepository).existsByName("Java");
        verify(technologyRepository, never()).save(any(Technology.class));
    }

    @Test
    void shouldHandleRepositoryErrorWhenCheckingExistence() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Database connection error");
        given(technologyRepository.existsByName("Java")).willReturn(Mono.error(repositoryError));

        // When
        Mono<Technology> result = registerTechnologyUseCase.execute(technology);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(technologyRepository).existsByName("Java");
        verify(technologyRepository, never()).save(any(Technology.class));
    }

    @Test
    void shouldHandleRepositoryErrorWhenSaving() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Database save error");
        given(technologyRepository.existsByName("Java")).willReturn(Mono.just(false));
        given(technologyRepository.save(any(Technology.class))).willReturn(Mono.error(repositoryError));

        // When
        Mono<Technology> result = registerTechnologyUseCase.execute(technology);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(technologyRepository).existsByName("Java");
        verify(technologyRepository).save(any(Technology.class));
    }
}

