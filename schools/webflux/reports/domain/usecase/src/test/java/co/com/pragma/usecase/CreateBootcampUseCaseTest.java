package co.com.pragma.usecase;

import co.com.pragma.model.Bootcamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBootcampUseCaseTest {

    @Mock
    private SaveBootcampReportUseCase saveBootcampReportUseCase;

    @InjectMocks
    private CreateBootcampUseCase createBootcampUseCase;

    @Test
    void execute_ShouldReturnBootcampAndTriggerReport() {

        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(123L);
        String traceId = "trace-456";
        String token = "Bearer token-789";

        when(saveBootcampReportUseCase.execute(anyLong(), anyString(), anyString()))
                .thenReturn(Mono.empty());


        StepVerifier.create(createBootcampUseCase.execute(bootcamp, traceId, token))
                .expectNextMatches(saved -> saved.getId().equals(123L))
                .verifyComplete();

        verify(saveBootcampReportUseCase, times(1))
                .execute(123L, traceId, token);
    }

    @Test
    void execute_ShouldLogError_WhenReportFails() throws InterruptedException {

        Bootcamp bootcamp = new Bootcamp();
        bootcamp.setId(123L);
        String traceId = "trace-456";
        String token = "token-789";

        when(saveBootcampReportUseCase.execute(anyLong(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Simulated error")));

        // 2. Act & Assert
        StepVerifier.create(createBootcampUseCase.execute(bootcamp, traceId, token))
                .expectNextCount(1)//Se valida que el flujo emita un elemento
                .verifyComplete();

        // 3. Pequeña espera para el hilo asíncrono
        // Como el log ocurre en Schedulers.boundedElastic(), el test podría terminar
        // antes de que el código llegue a la línea del log.severe
        Thread.sleep(100);

        verify(saveBootcampReportUseCase).execute(123L, traceId, token);
    }
}