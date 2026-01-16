package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.gateway.BootcampRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @InjectMocks
    private GetBootcampUseCase getBootcampUseCase;

    private final String traceId = "trace-get-123";
    private final Long bootcampId = 1L;

    @Test
    @DisplayName("Should return bootcamp when it exists")
    void executeSuccess() {
        // Arrange
        Bootcamp mockBootcamp = Bootcamp.builder()
                .id(bootcampId)
                .name("Java Cloud")
                .build();

        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(mockBootcamp));

        // Act & Assert
        StepVerifier.create(getBootcampUseCase.execute(bootcampId, traceId))
                .expectNextMatches(bootcamp ->
                                           bootcamp.getId().equals(bootcampId) &&
                                                   bootcamp.getName().equals("Java Cloud")
                )
                .verifyComplete();

        verify(bootcampRepository, times(1)).findById(bootcampId);
    }

    @Test
    @DisplayName("Should return empty Mono when bootcamp does not exist")
    void executeNotFound() {
        // Arrange
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(getBootcampUseCase.execute(bootcampId, traceId))
                .expectNextCount(0) // No esperamos ningún elemento
                .verifyComplete();

        verify(bootcampRepository, times(1)).findById(bootcampId);
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void executeError() {
        // Arrange
        when(bootcampRepository.findById(bootcampId))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // Act & Assert
        StepVerifier.create(getBootcampUseCase.execute(bootcampId, traceId))
                .expectError(RuntimeException.class)
                .verify();
    }
}