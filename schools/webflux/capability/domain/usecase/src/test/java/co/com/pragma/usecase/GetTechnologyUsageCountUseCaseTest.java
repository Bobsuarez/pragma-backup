package co.com.pragma.usecase;

import co.com.pragma.model.technology.gateway.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTechnologyUsageCountUseCase Tests")
class GetTechnologyUsageCountUseCaseTest {

    @Mock
    private TechnologyRepository technologyR2dbcRepository;

    @InjectMocks
    private GetTechnologyUsageCountUseCase getTechnologyUsageCountUseCase;

    private Long technologyId = 1L;
    private Long usageCount = 5L;

    @BeforeEach
    void setUp() {
        when(technologyR2dbcRepository.getUsageCount(technologyId))
                .thenReturn(Mono.just(usageCount));
    }

    @Test
    @DisplayName("Should return usage count successfully")
    void shouldReturnUsageCountSuccessfully() {
        StepVerifier.create(getTechnologyUsageCountUseCase.execute(technologyId))
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return zero when technology is not used")
    void shouldReturnZeroWhenTechnologyNotUsed() {
        when(technologyR2dbcRepository.getUsageCount(technologyId))
                .thenReturn(Mono.just(0L));

        StepVerifier.create(getTechnologyUsageCountUseCase.execute(technologyId))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(technologyR2dbcRepository.getUsageCount(technologyId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(getTechnologyUsageCountUseCase.execute(technologyId))
                .expectError(RuntimeException.class)
                .verify();
    }
}
