package co.com.pragma.usecase;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityAndTechnology;
import co.com.pragma.model.capablity.CapabilityIds;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.model.capablity.gateway.CapabilityTechnologyRepository;
import co.com.pragma.model.technology.Technology;
import co.com.pragma.model.technology.TechnologyIds;
import co.com.pragma.model.technology.gateway.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchCapabilityWithTechnologiesUseCase Tests")
class SearchCapabilityWithTechnologiesUseCaseTest {

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @Mock
    private CapabilityTechnologyRepository capabilityTechnologyRepository;

    @InjectMocks
    private SearchCapabilityWithTechnologiesUseCase searchCapabilityWithTechnologiesUseCase;

    private CapabilityIds capabilityIds;
    private Capability capability1;
    private Capability capability2;
    private Technology technology1;
    private Technology technology2;
    private List<CapabilityAndTechnology> capabilityTechnologyRelations1;
    private List<CapabilityAndTechnology> capabilityTechnologyRelations2;

    @BeforeEach
    void setUp() {
        capabilityIds = CapabilityIds.builder()
                .capabilityIds(Arrays.asList(1L, 2L))
                .build();

        capability1 = Capability.builder()
                .id(1L)
                .name("Backend Development")
                .description("Backend skills")
                .build();

        capability2 = Capability.builder()
                .id(2L)
                .name("Frontend Development")
                .description("Frontend skills")
                .build();

        technology1 = Technology.builder()
                .id(1L)
                .name("Java")
                .build();

        technology2 = Technology.builder()
                .id(2L)
                .name("Spring")
                .build();

        capabilityTechnologyRelations1 = Arrays.asList(
                CapabilityAndTechnology.builder()
                        .capabilityId(1L)
                        .technologyId(1L)
                        .build(),
                CapabilityAndTechnology.builder()
                        .capabilityId(1L)
                        .technologyId(2L)
                        .build()
        );

        capabilityTechnologyRelations2 = Arrays.asList(
                CapabilityAndTechnology.builder()
                        .capabilityId(2L)
                        .technologyId(1L)
                        .build()
        );
    }

    @Test
    @DisplayName("Should return capabilities with technologies when all data exists")
    void shouldReturnCapabilitiesWithTechnologies() {

        when(capabilityRepository.findCapabilityById(1L)).thenReturn(Mono.just(capability1));
        when(capabilityRepository.findCapabilityById(2L)).thenReturn(Mono.just(capability2));
        
        when(capabilityTechnologyRepository.findTechnologyAndCapabilityById(1L))
                .thenReturn(Flux.fromIterable(capabilityTechnologyRelations1));
        when(capabilityTechnologyRepository.findTechnologyAndCapabilityById(2L))
                .thenReturn(Flux.fromIterable(capabilityTechnologyRelations2));

        when(technologyRepository.findAllByIds(any(TechnologyIds.class)))
                .thenReturn(Flux.just(technology1, technology2));

        StepVerifier.create(searchCapabilityWithTechnologiesUseCase.execute(capabilityIds))
                .expectNextMatches(capabilities -> {
                    if (capabilities.size() != 2) return false;
                    Capability cap1 = capabilities.get(0);
                    Capability cap2 = capabilities.get(1);
                    return cap1.getTechnologies().size() == 2 && 
                           cap2.getTechnologies().size() == 1;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return capabilities without technologies when no technology relations exist")
    void shouldReturnCapabilitiesWithoutTechnologies() {
        when(capabilityRepository.findCapabilityById(1L)).thenReturn(Mono.just(capability1));
        
        when(capabilityTechnologyRepository.findTechnologyAndCapabilityById(1L))
                .thenReturn(Flux.empty());

        StepVerifier.create(searchCapabilityWithTechnologiesUseCase.execute(
                CapabilityIds.builder().capabilityIds(Arrays.asList(1L)).build()))
                .expectNextMatches(capabilities -> {
                    Capability cap = capabilities.get(0);
                    return cap.getTechnologies() == null;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate error when capability repository fails")
    void shouldPropagateErrorWhenCapabilityRepositoryFails() {
        when(capabilityRepository.findCapabilityById(1L))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(searchCapabilityWithTechnologiesUseCase.execute(
                CapabilityIds.builder().capabilityIds(Arrays.asList(1L)).build()))
                .expectError(RuntimeException.class)
                .verify();
    }
}
