package co.com.pragma.usecase;

import co.com.pragma.model.Person;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountPersonBootcampsUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private CountPersonBootcampsUseCase useCase;

    private Person person;
    private String traceId;
    private Long personId;

    @BeforeEach
    void setUp() {
        personId = 1L;
        traceId = "test-trace-id";
        person = Person.builder()
                .id(personId)
                .name("Test Person")
                .email("test@example.com")
                .build();
    }

    @Test
    void execute_WhenPersonExists_ShouldReturnCount() {
        // Arrange
        Long expectedCount = 3L;
        when(personRepository.findById(personId)).thenReturn(Mono.just(person));
        when(personRepository.countEnrollmentsByPersonId(personId)).thenReturn(Mono.just(expectedCount));

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, traceId))
                .expectNext(expectedCount)
                .verifyComplete();
    }

    @Test
    void execute_WhenPersonDoesNotExist_ShouldReturnError() {
        // Arrange
        when(personRepository.findById(personId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, traceId))
                .expectErrorMatches(error -> error instanceof BusinessException
                        && error.getMessage().equals("La persona no existe"))
                .verify();
    }

    @Test
    void execute_WhenPersonExistsButHasNoBootcamps_ShouldReturnZero() {
        // Arrange
        Long expectedCount = 0L;
        when(personRepository.findById(personId)).thenReturn(Mono.just(person));
        when(personRepository.countEnrollmentsByPersonId(personId)).thenReturn(Mono.just(expectedCount));

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, traceId))
                .expectNext(expectedCount)
                .verifyComplete();
    }

    @Test
    void execute_WhenRepositoryThrowsError_ShouldPropagateError() {
        // Arrange
        RuntimeException repositoryError = new RuntimeException("Database error");
        when(personRepository.findById(personId)).thenReturn(Mono.error(repositoryError));

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, traceId))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database error"))
                .verify();
    }
}
