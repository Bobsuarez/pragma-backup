package co.com.pragma.usecase;

import co.com.pragma.model.gateway.BootcampDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBootcampDetailUseCaseTest {

    @Mock
    private BootcampDetailRepository bootcampDetailRepository;

    @InjectMocks
    private GetBootcampDetailUseCase getBootcampDetailUseCase;

    @Test
    void execute_ShouldReturnDetail_WhenIdExists() {

        Long bootcampId = 100L;
        String traceId = "trace-123";
        String token = "Bearer token";

        var expectedDetail = new BootcampDetailRepository.BootcampDetail(
                bootcampId,
                "Fullstack Java",
                "Description test",
                "2024-01-01",
                6,
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(bootcampDetailRepository.getBootcampDetail(bootcampId))
                .thenReturn(Mono.just(expectedDetail));


        StepVerifier.create(getBootcampDetailUseCase.execute(bootcampId, traceId, token))
                .expectNext(expectedDetail)
                .verifyComplete();

        verify(bootcampDetailRepository).getBootcampDetail(bootcampId);
    }

    @Test
    void execute_ShouldReturnError_WhenRepositoryFails() {

        Long bootcampId = 100L;
        String errorMessage = "Database connection failed";

        when(bootcampDetailRepository.getBootcampDetail(bootcampId))
                .thenReturn(Mono.error(new RuntimeException(errorMessage)));

        StepVerifier.create(getBootcampDetailUseCase.execute(bootcampId, "trace", "token"))
                .expectErrorMatches(throwable ->
                                            throwable instanceof RuntimeException &&
                                                    throwable.getMessage().equals(errorMessage)
                )
                .verify();
    }

    @Test
    void execute_ShouldCompleteWithoutEmitting_WhenDetailNotFound() {

        Long bootcampId = 999L;
        when(bootcampDetailRepository.getBootcampDetail(bootcampId))
                .thenReturn(Mono.empty());

        StepVerifier.create(getBootcampDetailUseCase.execute(bootcampId, "trace", "token"))
                .verifyComplete();
    }
}