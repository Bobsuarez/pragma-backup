package co.com.pragma.usecase;


import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.capability.Capability;
import co.com.pragma.model.bootcamp.gateway.BootcampRepository;
import co.com.pragma.model.capability.CapabilityValidationResult;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import co.com.pragma.model.exceptions.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private CreateBootcampUseCase createBootcampUseCase;

    @Test
    @DisplayName("Should create bootcamp successfully when all validations pass")
    void executeSuccess() {
        // Arrange
        String traceId = "trace-123";
        Capability cap = Capability.builder().id(1L).build();
        Bootcamp bootcamp = Bootcamp.builder()
                .name("Java Advanced")
                .capabilities(List.of(cap))
                .build();

        CapabilityValidationResult validationResult = CapabilityValidationResult.builder()
                .isValidate(true)
                .build();

        when(capabilityRepository.findAllByIds(any())).thenReturn(Mono.just(validationResult));
        when(bootcampRepository.save(any())).thenReturn(Mono.just(bootcamp.toBuilder().id(1L).build()));

        // Act & Assert
        StepVerifier.create(createBootcampUseCase.execute(bootcamp, traceId))
                .assertNext(saved -> {
                    assert saved.getId() != null;
                    assert saved.getName().equals("Java Advanced");
                })
                .verifyComplete();

        verify(bootcampRepository).save(bootcamp);
    }

    @Test
    @DisplayName("Should throw BusinessException when capabilities count is invalid (e.g., 0)")
    void executeInvalidCount() {
        // Arrange
        Bootcamp bootcamp = Bootcamp.builder()
                .name("Empty Bootcamp")
                .capabilities(List.of()) // 0 capacidades
                .build();

        // Act & Assert
        StepVerifier.create(createBootcampUseCase.execute(bootcamp, "trace"))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().contains("entre 1 y 4 capacidades"))
                .verify();

        verifyNoInteractions(capabilityRepository);
        verifyNoInteractions(bootcampRepository);
    }

//    @Test
//    @DisplayName("Should throw BusinessException when capabilities do not exist in system")
//    void executeCapabilitiesNotFound() {
//        // Arrange
//        Capability cap = Capability.builder().id(99L).build();
//        Bootcamp bootcamp = Bootcamp.builder()
//                .name("Ghost Bootcamp")
//                .capabilities(List.of(cap))
//                .build();
//
//        CapabilityValidationResult invalidResult = CapabilityValidationResult.builder()
//                .isValidate(false)
//                .message("ID 99 not found")
//                .build();
//
//        when(capabilityRepository.findAllByIds(any())).thenReturn(Mono.just(invalidResult));
//
//        CreateBootcampUseCase createBootcampUseCase = new CreateBootcampUseCase(bootcampRepository, capabilityRepository);
//
//        // Act & Assert
//        StepVerifier.create(createBootcampUseCase.execute(bootcamp, "trace"))
//                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
//                        throwable.getMessage().contains("Una o más capacidades no existen"))
//                .verify();
//
//        verify(bootcampRepository, never()).save(any());
//    }

    @Test
    @DisplayName("Should throw BusinessException when more than 4 capabilities are provided")
    void executeTooManyCapabilities() {
        // Arrange
        List<Capability> caps = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            caps.add(Capability.builder().id(i).build());
        }
        Bootcamp bootcamp = Bootcamp.builder().capabilities(caps).build();

        // Act & Assert
        StepVerifier.create(createBootcampUseCase.execute(bootcamp, "trace"))
                .expectError(BusinessException.class)
                .verify();
    }
}