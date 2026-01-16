package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.BootcampList;
import co.com.pragma.model.bootcamp.BootcampPage;
import co.com.pragma.model.bootcamp.gateway.BootcampListRepository;
import co.com.pragma.model.capability.CapabilityListResult;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampListUseCaseTest {

    @Mock
    private BootcampListRepository repository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private BootcampListUseCase bootcampListUseCase;

    @Test
    @DisplayName("Should list bootcamps and enrich them with capabilities")
    void executeSuccess() {
        // Arrange
        String traceId = "test-trace";
        // 1. Crear Bootcamp inicial (con IDs de capacidades)
        BootcampList bootcamp = BootcampList.builder()
                .id(1L)
                .name("Java Cloud")
                .idCapabilities(List.of(10L)) // ID para cruzar
                .build();

        BootcampPage page = new BootcampPage(List.of(bootcamp), 1L, 1, 1);

        // 2. Crear Resultado de Capacidad (para el enriquecimiento)
        CapabilityListResult capResult = CapabilityListResult.builder()
                .id(10L)
                .name("Backend Capability")
                .build();

        when(repository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Mono.just(page));
        when(capabilityRepository.findAllTechnologiesByIds(any())).thenReturn(Flux.just(capResult));

        // Act & Assert
        StepVerifier.create(bootcampListUseCase.execute(0, 10, BootcampSortField.NAME, SortDirection.ASC, traceId))
                .assertNext(result -> {
                    // Verificar que el bootcamp ahora tiene la capacidad mapeada
                    assert !result.getContent().get(0).getCapabilities().isEmpty();
                    assert result.getContent().get(0).getCapabilities().get(0).getName().equals("Backend Capability");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return bootcamps even if enrichment fails (Graceful Degradation)")
    void executeWithEnrichmentError() {
        // Arrange
        BootcampList bootcamp = BootcampList.builder()
                .id(1L)
                .idCapabilities(List.of(10L))
                .build();
        BootcampPage page = new BootcampPage(List.of(bootcamp), 1L, 1, 1);

        when(repository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Mono.just(page));
        // Simulamos error en el repositorio de capacidades
        when(capabilityRepository.findAllTechnologiesByIds(any())).thenReturn(Flux.error(new RuntimeException("Service Down")));

        // Act & Assert
        StepVerifier.create(bootcampListUseCase.execute(0, 10, BootcampSortField.NAME, SortDirection.ASC, "trace"))
                .assertNext(result -> {
                    // El flujo no falla, pero la lista de capacidades viene vacía o nula
                    assert result.getContent().get(0).getCapabilities() == null || result.getContent().get(0).getCapabilities().isEmpty();
                    assert result.getContent().get(0).getId().equals(1L);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty list when no bootcamps found")
    void executeEmpty() {
        // Arrange
        BootcampPage emptyPage = new BootcampPage(List.of(), 0L, 10, 0);
        when(repository.findAll(anyInt(), anyInt(), any(), any())).thenReturn(Mono.just(emptyPage));

        // Act & Assert
        StepVerifier.create(bootcampListUseCase.execute(0, 10, BootcampSortField.NAME, SortDirection.ASC, "trace"))
                .assertNext(result -> {
                    assert result.getContent().isEmpty();
                })
                .verifyComplete();

        // Verificar que no se llamó a capacidades si la lista de bootcamps estaba vacía
        verify(capabilityRepository, never()).findAllTechnologiesByIds(any());
    }
}