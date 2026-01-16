package co.com.pragma.usecase;

import co.com.pragma.model.capablity.CapabilityIds;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchCapabilityUseCase Tests")
class SearchCapabilityUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private SearchCapabilityUseCase searchCapabilityUseCase;

    private CapabilityIds existingCapabilityIds;
    private CapabilityIds nonExistingCapabilityIds;

    @BeforeEach
    void setUp() {
        existingCapabilityIds = CapabilityIds.builder()
                .capabilityIds(Arrays.asList(1L, 2L))
                .build();

        nonExistingCapabilityIds = CapabilityIds.builder()
                .capabilityIds(Arrays.asList(1L, 999L))
                .build();
    }

    @Test
    @DisplayName("Should return CapabilityStatus with isExisting true when all capabilities exist")
    void shouldReturnTrueWhenAllCapabilitiesExist() {
        when(capabilityRepository.findById(1L)).thenReturn(Mono.just(true));
        when(capabilityRepository.findById(2L)).thenReturn(Mono.just(true));

        StepVerifier.create(searchCapabilityUseCase.execute(existingCapabilityIds))
                .expectNextMatches(status -> 
                    status.getIsExisting().equals(Boolean.TRUE))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BusinessException when one or more capabilities do not exist")
    void shouldThrowExceptionWhenCapabilitiesDoNotExist() {
        when(capabilityRepository.findById(1L)).thenReturn(Mono.just(true));
        when(capabilityRepository.findById(999L)).thenReturn(Mono.just(false));

        StepVerifier.create(searchCapabilityUseCase.execute(nonExistingCapabilityIds))
                .expectErrorMatches(error -> 
                    error instanceof BusinessException && 
                    error.getMessage().equals("One or more capabilities are not registered"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(capabilityRepository.findById(1L))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(searchCapabilityUseCase.execute(existingCapabilityIds))
                .expectError(RuntimeException.class)
                .verify();
    }
}
