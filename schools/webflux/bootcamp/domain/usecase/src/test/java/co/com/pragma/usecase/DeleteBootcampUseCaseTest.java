package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.gateway.BootcampDeleteRepository;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import co.com.pragma.model.technology.Technology;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBootcampUseCaseTest {

    @Mock
    private BootcampDeleteRepository bootcampDeleteRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private DeleteBootcampUseCase deleteBootcampUseCase;

    private final String traceId = "trace-delete-123";
    private final Long bootcampId = 1L;

    @Test
    @DisplayName("Should delete bootcamp and everything else when no one else uses the capabilities/techs")
    void deleteEverythingSuccess() {
        // Arrange
        Long capabilityId = 10L;
        Long technologyId = 100L;
        Technology tech = Technology.builder().id(technologyId).build();

        // 1. Obtiene IDs de capacidades del bootcamp
        when(bootcampDeleteRepository.getCapabilityIdsByBootcampId(bootcampId))
                .thenReturn(Flux.just(capabilityId));

        // 2. ¿La capacidad es usada por otros? No (false)
        when(bootcampDeleteRepository.isCapabilityUsedByOtherBootcamps(capabilityId, bootcampId))
                .thenReturn(Mono.just(false));

        // 3. Obtiene tecnologías de esa capacidad
        when(capabilityRepository.getTechnologiesByCapabilityId(capabilityId))
                .thenReturn(Flux.just(tech));

        // 4. ¿La tecnología es usada por otros? Solo por esta (count == 1)
        when(capabilityRepository.getTechnologyUsageCount(technologyId))
                .thenReturn(Mono.just(1L));

        // 5. Mocks de las eliminaciones físicas
        when(capabilityRepository.deleteTechnology(technologyId)).thenReturn(Mono.empty());
        when(capabilityRepository.deleteCapability(capabilityId)).thenReturn(Mono.empty());
        when(bootcampDeleteRepository.deleteById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(deleteBootcampUseCase.execute(bootcampId, traceId))
                .verifyComplete();

        // Verificamos que se llamó a borrar TODO
        verify(capabilityRepository).deleteTechnology(technologyId);
        verify(capabilityRepository).deleteCapability(capabilityId);
        verify(bootcampDeleteRepository).deleteById(bootcampId);
    }

    @Test
    @DisplayName("Should delete bootcamp but SKIP capability deletion if it is used by another bootcamp")
    void skipCapabilityDeletionWhenUsed() {
        // Arrange
        Long capabilityId = 20L;

        when(bootcampDeleteRepository.getCapabilityIdsByBootcampId(bootcampId))
                .thenReturn(Flux.just(capabilityId));

        // La capacidad SÍ está siendo usada por otros
        when(bootcampDeleteRepository.isCapabilityUsedByOtherBootcamps(capabilityId, bootcampId))
                .thenReturn(Mono.just(true));

        when(bootcampDeleteRepository.deleteById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(deleteBootcampUseCase.execute(bootcampId, traceId))
                .verifyComplete();

        // Verificamos: Se borra el bootcamp, pero NUNCA se intentó borrar la capacidad ni sus techs
        verify(bootcampDeleteRepository).deleteById(bootcampId);
        verify(capabilityRepository, never()).deleteCapability(anyLong());
        verify(capabilityRepository, never()).getTechnologiesByCapabilityId(anyLong());
    }

    @Test
    @DisplayName("Should delete capability but SKIP technology deletion if tech is used elsewhere")
    void skipTechnologyDeletionWhenUsed() {
        // Arrange
        Long capabilityId = 30L;
        Long techId = 300L;
        Technology tech = Technology.builder().id(techId).build();

        when(bootcampDeleteRepository.getCapabilityIdsByBootcampId(bootcampId)).thenReturn(Flux.just(capabilityId));
        when(bootcampDeleteRepository.isCapabilityUsedByOtherBootcamps(capabilityId, bootcampId)).thenReturn(Mono.just(false));
        when(capabilityRepository.getTechnologiesByCapabilityId(capabilityId)).thenReturn(Flux.just(tech));

        // La tecnología es usada por 2 capacidades (contando esta), así que no debe borrarse
        when(capabilityRepository.getTechnologyUsageCount(techId)).thenReturn(Mono.just(2L));

        when(capabilityRepository.deleteCapability(capabilityId)).thenReturn(Mono.empty());
        when(bootcampDeleteRepository.deleteById(bootcampId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(deleteBootcampUseCase.execute(bootcampId, traceId))
                .verifyComplete();

        // Verificamos: Se borra bootcamp y capacidad, pero NO la tecnología
        verify(capabilityRepository, never()).deleteTechnology(techId);
        verify(capabilityRepository).deleteCapability(capabilityId);
        verify(bootcampDeleteRepository).deleteById(bootcampId);
    }
}