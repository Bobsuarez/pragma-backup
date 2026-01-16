package co.com.pragma.usecase;

import co.com.pragma.model.BootcampReport;
import co.com.pragma.model.gateway.BootcampMetricsRepository;
import co.com.pragma.model.gateway.BootcampReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveBootcampReportUseCaseTest {

    @Mock
    private BootcampMetricsRepository bootcampMetricsRepository;

    @Mock
    private BootcampReportRepository bootcampReportRepository;

    @InjectMocks
    private SaveBootcampReportUseCase useCase;

    @Test
    void execute_ShouldSaveReportSuccessfully() {

        Long bootcampId = 1L;
        String traceId = "trace-123";
        String token = "token-abc";

        var bootcampInfo = new BootcampMetricsRepository.BootcampInfo(
                bootcampId, "Java Camp", "Desc", LocalDate.now()
                .toString(), 3
        );

        when(bootcampMetricsRepository.getBootcampInfo(bootcampId))
                .thenReturn(Mono.just(bootcampInfo));

        when(bootcampMetricsRepository.countCapabilitiesByBootcampId(bootcampId))
                .thenReturn(Mono.just(5));
        when(bootcampMetricsRepository.countTechnologiesByBootcampId(bootcampId))
                .thenReturn(Mono.just(10));
        when(bootcampMetricsRepository.countEnrolledPeopleByBootcampId(bootcampId))
                .thenReturn(Mono.just(50));
        when(bootcampReportRepository.save(any())).thenReturn(Mono.just(new BootcampReport()));

        StepVerifier.create(useCase.execute(bootcampId, traceId, token))
                .verifyComplete();

        verify(bootcampReportRepository, times(1))
                .save(argThat(report ->
                                      report.getBootcampId()
                                              .equals(bootcampId) &&
                                              report.getCapabilitiesCount() == 5 &&
                                              report.getTechnologiesCount() == 10 &&
                                              report.getEnrolledPeopleCount() == 50
                ));
    }

    @Test
    void execute_ShouldHandleErrorsInMetricsAndContinueWithZero() {

        Long bootcampId = 1L;
        var bootcampInfo = new BootcampMetricsRepository.BootcampInfo(1L, "Test", "Test", LocalDate.now()
                .toString(), 1
        );

        when(bootcampMetricsRepository.getBootcampInfo(bootcampId)).thenReturn(Mono.just(bootcampInfo));
        when(bootcampMetricsRepository.countCapabilitiesByBootcampId(bootcampId))
                .thenReturn(Mono.error(new RuntimeException("DB Error")));
        when(bootcampMetricsRepository.countTechnologiesByBootcampId(bootcampId)).thenReturn(Mono.just(2));
        when(bootcampMetricsRepository.countEnrolledPeopleByBootcampId(bootcampId)).thenReturn(Mono.just(5));

        when(bootcampReportRepository.save(any())).thenReturn(Mono.just(new BootcampReport()));

        StepVerifier.create(useCase.execute(bootcampId, "trace", "token"))
                .verifyComplete();

        // Verificamos que gracias al .onErrorReturn(0), el reporte se guardó con 0 en capabilities
        verify(bootcampReportRepository).save(argThat(report -> report.getCapabilitiesCount() == 0));
    }
}