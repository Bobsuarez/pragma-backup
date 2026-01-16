package co.com.pragma.usecase;

import co.com.pragma.model.Bootcamp;
import co.com.pragma.model.Person;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampRepository;
import co.com.pragma.model.gateway.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPeopleByBootcampUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private BootcampRepository bootcampRepository;

    @InjectMocks
    private GetPeopleByBootcampUseCase useCase;

    private Bootcamp bootcamp;
    private Person person1;
    private Person person2;
    private String traceId;
    private Long bootcampId;

    @BeforeEach
    void setUp() {
        bootcampId = 1L;
        traceId = "test-trace-id";

        bootcamp = Bootcamp.builder()
                .id(bootcampId)
                .name("Test Bootcamp")
                .description("Test Description")
                .launchDate(LocalDate.now())
                .durationMonths(6)
                .build();

        person1 = Person.builder()
                .id(1L)
                .name("Person 1")
                .email("person1@example.com")
                .build();

        person2 = Person.builder()
                .id(2L)
                .name("Person 2")
                .email("person2@example.com")
                .build();
    }

    @Test
    void execute_WhenBootcampExists_ShouldReturnPeople() {
        // Arrange
        List<Person> expectedPeople = List.of(person1, person2);
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(bootcamp).contextWrite(Context.of("traceId", traceId)));
        when(personRepository.findPeopleByBootcampId(bootcampId)).thenReturn(Flux.fromIterable(expectedPeople));

        // Act & Assert
        StepVerifier.create(useCase.execute(bootcampId, traceId))
                .expectNext(person1)
                .expectNext(person2)
                .verifyComplete();
    }

    @Test
    void execute_WhenBootcampDoesNotExist_ShouldReturnError() {
        // Arrange
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.<Bootcamp>empty().contextWrite(Context.of("traceId", traceId)));

        // Act & Assert
        StepVerifier.create(useCase.execute(bootcampId, traceId))
                .expectErrorMatches(error -> error instanceof BusinessException
                        && error.getMessage().equals("El bootcamp no existe"))
                .verify();
    }

    @Test
    void execute_WhenBootcampExistsButNoPeople_ShouldReturnEmpty() {
        // Arrange
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(bootcamp).contextWrite(Context.of("traceId", traceId)));
        when(personRepository.findPeopleByBootcampId(bootcampId)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(bootcampId, traceId))
                .verifyComplete();
    }

    @Test
    void execute_WhenRepositoryThrowsError_ShouldPropagateError() {
        // Arrange
        RuntimeException repositoryError = new RuntimeException("Database error");
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.<Bootcamp>error(repositoryError).contextWrite(Context.of("traceId", traceId)));

        // Act & Assert
        StepVerifier.create(useCase.execute(bootcampId, traceId))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void execute_WhenPersonRepositoryThrowsError_ShouldPropagateError() {
        // Arrange
        RuntimeException repositoryError = new RuntimeException("Person repository error");
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(bootcamp).contextWrite(Context.of("traceId", traceId)));
        when(personRepository.findPeopleByBootcampId(bootcampId)).thenReturn(Flux.error(repositoryError));

        // Act & Assert
        StepVerifier.create(useCase.execute(bootcampId, traceId))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().equals("Person repository error"))
                .verify();
    }
}
