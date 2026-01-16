package co.com.pragma.usecase;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.model.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterCapabilityUseCase Tests")
class RegisterCapabilityUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private RegisterCapabilityUseCase registerCapabilityUseCase;

    private Capability validCapability;
    private Capability capabilityWithDuplicates;
    private Capability savedCapability;

    @BeforeEach
    void setUp() {
        List<Long> validTechnologyIds = Arrays.asList(1L, 2L, 3L);
        List<Long> duplicateTechnologyIds = Arrays.asList(1L, 2L, 1L);

        validCapability = Capability.builder()
                .name("Test Capability")
                .description("Test Description")
                .technologyIds(validTechnologyIds)
                .build();

        capabilityWithDuplicates = Capability.builder()
                .name("Test Capability")
                .description("Test Description")
                .technologyIds(duplicateTechnologyIds)
                .build();

        savedCapability = Capability.builder()
                .id(1L)
                .name("Test Capability")
                .description("Test Description")
                .technologyIds(validTechnologyIds)
                .build();
    }

    @Test
    @DisplayName("Should register capability successfully when technology IDs are valid")
    void shouldRegisterCapabilitySuccessfully() {
        when(capabilityRepository.save(any(Capability.class))).thenReturn(Mono.just(savedCapability));

        StepVerifier.create(registerCapabilityUseCase.execute(validCapability))
                .expectNextMatches(capability -> 
                    capability.getId().equals(1L) && 
                    capability.getName().equals("Test Capability"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BusinessException when technology IDs have duplicates")
    void shouldThrowExceptionWhenTechnologyIdsHaveDuplicates() {
        StepVerifier.create(registerCapabilityUseCase.execute(capabilityWithDuplicates))
                .expectErrorMatches(error -> 
                    error instanceof BusinessException && 
                    error.getMessage().equals("Las tecnologias no pueden estar repetidas"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(capabilityRepository.save(any(Capability.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(registerCapabilityUseCase.execute(validCapability))
                .expectError(RuntimeException.class)
                .verify();
    }
}
