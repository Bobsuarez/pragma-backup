package co.com.pragma.usecase;

import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTechnologiesByIdsUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private GetTechnologiesByIdsUseCase getTechnologiesByIdsUseCase;

    @Test
    @DisplayName("Should return a flux of technologies when IDs are provided")
    void executeSuccess() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);

        Technology tech1 = Technology.builder()
                .id("tech-123")
                .name("Java")
                .description("Lenguaje de programación orientado a objetos")
                .build();

        Technology tech2 = Technology.builder()
                .id("tech-123")
                .name("Spring Boot")
                .description("Framework para aplicaciones Java")
                .build();

        when(technologyRepository.findAllByIds(ids)).thenReturn(Flux.just(tech1, tech2));

        // Act
        Flux<Technology> result = getTechnologiesByIdsUseCase.execute(ids);

        // Assert
        StepVerifier.create(result)
                .expectNext(tech1)
                .expectNext(tech2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should log and propagate error when repository fails")
    void executeError() {
        // Arrange
        List<Long> ids = List.of(1L);
        when(technologyRepository.findAllByIds(anyList()))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        // Act
        Flux<Technology> result = getTechnologiesByIdsUseCase.execute(ids);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();
    }
}