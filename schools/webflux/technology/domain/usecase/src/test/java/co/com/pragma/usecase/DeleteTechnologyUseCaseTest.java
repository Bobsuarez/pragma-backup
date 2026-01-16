package co.com.pragma.usecase;

import co.com.pragma.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTechnologyUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private DeleteTechnologyUseCase deleteTechnologyUseCase;

    @Test
    @DisplayName("Should complete successfully when technology is deleted")
    void executeSuccess() {
        // Arrange
        Long id = 1L;
        // Simulamos que el repositorio devuelve Mono.empty() que es el estándar para Void
        when(technologyRepository.deleteById(id)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteTechnologyUseCase.execute(id);

        // Assert
        StepVerifier.create(result)
                .verifyComplete(); // Verifica que el flujo termine exitosamente sin emitir datos

        verify(technologyRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should propagate error when repository fails to delete")
    void executeError() {
        // Arrange
        Long id = 1L;
        when(technologyRepository.deleteById(anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Void> result = deleteTechnologyUseCase.execute(id);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(technologyRepository, times(1)).deleteById(id);
    }
}