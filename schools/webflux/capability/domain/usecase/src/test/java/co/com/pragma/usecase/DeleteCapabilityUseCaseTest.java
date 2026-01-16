package co.com.pragma.usecase;

import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCapabilityUseCase Tests")
class DeleteCapabilityUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private DeleteCapabilityUseCase deleteCapabilityUseCase;

    @Test
    @DisplayName("Should delete capability successfully when it exists")
    void shouldDeleteCapabilitySuccessfully() {
        when(capabilityRepository.findById(anyLong())).thenReturn(Mono.just(true));
        when(capabilityRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        StepVerifier.create(deleteCapabilityUseCase.execute(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw error when capability does not exist")
    void shouldThrowErrorWhenCapabilityDoesNotExist() {
        when(capabilityRepository.findById(anyLong())).thenReturn(Mono.just(false));
        StepVerifier.create(deleteCapabilityUseCase.execute(999L))
                .expectErrorMatches(error -> 
                    error instanceof RuntimeException && 
                    error.getMessage().contains("Capability not found"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(capabilityRepository.findById(anyLong())).thenReturn(Mono.just(true));
        when(capabilityRepository.findById(1L))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(deleteCapabilityUseCase.execute(1L))
                .expectError(RuntimeException.class)
                .verify();
    }
}
