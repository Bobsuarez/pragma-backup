package co.com.pragma.usecase;

import co.com.pragma.model.Bootcamp;
import co.com.pragma.model.Person;
import co.com.pragma.model.PersonBootcamp;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampRepository;
import co.com.pragma.model.gateway.PersonRepository;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollPersonInBootcampUseCaseTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private BootcampRepository bootcampRepository;

    @InjectMocks
    private EnrollPersonInBootcampUseCase useCase;

    private final String traceId = "trace-enroll-123";
    private final Long personId = 1L;
    private final Long bootcampId = 2L;

    @Test
    @DisplayName("Should enroll person successfully when all validations pass")
    void executeSuccess() {
        // Arrange
        Bootcamp newBootcamp = Bootcamp.builder()
                .id(bootcampId).name("Java").launchDate(LocalDate.now().plusDays(10)).durationMonths(3).build();

        when(personRepository.findById(personId)).thenReturn(Mono.just(new Person()));
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(newBootcamp));
        when(personRepository.findBootcampIdsByPersonId(personId)).thenReturn(Flux.empty()); // No inscripciones previas
        when(personRepository.enrollPersonInBootcamp(personId, bootcampId))
                .thenReturn(Mono.just(PersonBootcamp.builder().build()));

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, bootcampId, traceId))
                .assertNext(pb -> {
                    assert pb.getBootcamp().getName().equals("Java");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BusinessException when max bootcamps reached")
    void executeMaxBootcampsError() {
        // Arrange
        LocalDate today = LocalDate.now();
        Bootcamp newBootcamp = Bootcamp.builder()
                .id(bootcampId)
                .name("New Bootcamp")
                .launchDate(today.plusMonths(10))
                .durationMonths(4)
                .build();

        // Bootcamps existentes (5 bootcamps)
        Bootcamp existingBootcamp1 = Bootcamp.builder().id(10L).name("Bootcamp 1").launchDate(today).durationMonths(1).build();
        Bootcamp existingBootcamp2 = Bootcamp.builder().id(11L).name("Bootcamp 2").launchDate(today.plusMonths(2)).durationMonths(1).build();
        Bootcamp existingBootcamp3 = Bootcamp.builder().id(12L).name("Bootcamp 3").launchDate(today.plusMonths(4)).durationMonths(1).build();
        Bootcamp existingBootcamp4 = Bootcamp.builder().id(13L).name("Bootcamp 4").launchDate(today.plusMonths(6)).durationMonths(1).build();
        Bootcamp existingBootcamp5 = Bootcamp.builder().id(14L).name("Bootcamp 5").launchDate(today.plusMonths(8)).durationMonths(1).build();

        when(personRepository.findById(personId)).thenReturn(Mono.just(new Person()));
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(newBootcamp).contextWrite(Context.of("traceId", traceId)));

        // Simular que ya tiene 5 bootcamps (IDs)
        when(personRepository.findBootcampIdsByPersonId(personId)).thenReturn(Flux.just(10L, 11L, 12L, 13L, 14L));

        // Mock de findById para cada uno de los 5 bootcamps existentes
        when(bootcampRepository.findById(10L)).thenReturn(Mono.just(existingBootcamp1).contextWrite(Context.of("traceId", traceId)));
        when(bootcampRepository.findById(11L)).thenReturn(Mono.just(existingBootcamp2).contextWrite(Context.of("traceId", traceId)));
        when(bootcampRepository.findById(12L)).thenReturn(Mono.just(existingBootcamp3).contextWrite(Context.of("traceId", traceId)));
        when(bootcampRepository.findById(13L)).thenReturn(Mono.just(existingBootcamp4).contextWrite(Context.of("traceId", traceId)));
        when(bootcampRepository.findById(14L)).thenReturn(Mono.just(existingBootcamp5).contextWrite(Context.of("traceId", traceId)));

        // Mock enrollPersonInBootcamp para evitar NPE (aunque la validación debería fallar antes)
        when(personRepository.enrollPersonInBootcamp(anyLong(), anyLong()))
                .thenReturn(Mono.just(PersonBootcamp.builder().build()));

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, bootcampId, traceId))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().contains("más de 5 bootcamps"))
                .verify();
    }

    @Test
    @DisplayName("Should throw BusinessException when dates overlap")
    void executeDateOverlapError() {
        // Arrange
        LocalDate today = LocalDate.now();
        // Bootcamp nuevo: hoy a hoy+3 meses (ej: Enero a Abril)
        Bootcamp newBootcamp = Bootcamp.builder()
                .id(bootcampId)
                .name("New")
                .launchDate(today)
                .durationMonths(3)
                .build();

        // Bootcamp existente: hoy+1 mes a hoy+4 meses (ej: Febrero a Mayo) - Se solapan
        Bootcamp existingBootcamp = Bootcamp.builder()
                .id(99L)
                .name("Existing")
                .launchDate(today.plusMonths(1))
                .durationMonths(3)
                .build();

        when(personRepository.findById(personId)).thenReturn(Mono.just(new Person()));
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(newBootcamp).contextWrite(Context.of("traceId", traceId)));
        when(personRepository.findBootcampIdsByPersonId(personId)).thenReturn(Flux.just(99L));
        when(bootcampRepository.findById(99L)).thenReturn(Mono.just(existingBootcamp).contextWrite(Context.of("traceId", traceId)));

        // Mock enrollPersonInBootcamp para evitar NPE (aunque la validación debería fallar antes)
        when(personRepository.enrollPersonInBootcamp(anyLong(), anyLong()))
                .thenReturn(Mono.just(PersonBootcamp.builder().build()));

        // Act & Assert
        StepVerifier.create(useCase.execute(personId, bootcampId, traceId))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().contains("se solapa"))
                .verify();
    }
}
