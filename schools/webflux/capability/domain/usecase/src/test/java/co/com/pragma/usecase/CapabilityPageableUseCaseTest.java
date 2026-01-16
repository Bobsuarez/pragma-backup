package co.com.pragma.usecase;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityPage;
import co.com.pragma.model.capablity.gateway.capabilitylist.CapabilityListRepository;
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
@DisplayName("CapabilityPageableUseCase Tests")
class CapabilityPageableUseCaseTest {

    @Mock
    private CapabilityListRepository capabilitiesRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private CapabilityPageableUseCase capabilityPageableUseCase;

    private CapabilityPage capabilityPage;
    private Capability capability1;
    private Capability capability2;
    private Technology technology1;
    private Technology technology2;

    @BeforeEach
    void setUp() {
        capability1 = Capability.builder()
                .id(1L)
                .name("Backend Development")
                .description("Backend skills")
                .technologyIds(Arrays.asList(1L, 2L))
                .build();

        capability2 = Capability.builder()
                .id(2L)
                .name("Frontend Development")
                .description("Frontend skills")
                .technologyIds(Arrays.asList(1L))
                .build();

        capabilityPage = new CapabilityPage(
                Arrays.asList(capability1, capability2),
                2L
        );

        technology1 = Technology.builder()
                .id(1L)
                .name("Java")
                .build();

        technology2 = Technology.builder()
                .id(2L)
                .name("Spring")
                .build();
    }

    @Test
    @DisplayName("Should return capabilities page with technologies enriched")
    void shouldReturnCapabilitiesPageWithTechnologies() {
        when(capabilitiesRepository.findAll(0, 10, "nombre", "asc"))
                .thenReturn(Mono.just(capabilityPage));

        when(technologyRepository.findAllByIds(any(TechnologyIds.class)))
                .thenReturn(Flux.just(technology1, technology2));

        StepVerifier.create(capabilityPageableUseCase.execute(0, 10, "nombre", "asc"))
                .expectNextMatches(page -> {
                    List<Capability> items = page.getItems();
                    if (items.size() != 2) return false;
                    
                    Capability cap1 = items.get(0);
                    Capability cap2 = items.get(1);
                    
                    return cap1.getTechnologies().size() == 2 && 
                           cap2.getTechnologies().size() == 1 &&
                           page.getTotalElements() == 2L;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return capabilities page without technologies when no technology IDs exist")
    void shouldReturnCapabilitiesPageWithoutTechnologies() {
        CapabilityPage pageWithoutTechIds = new CapabilityPage(
                Arrays.asList(
                        Capability.builder()
                                .id(1L)
                                .name("Test")
                                .description("Test")
                                .technologyIds(List.of())
                                .build()
                ),
                1L
        );

        when(capabilitiesRepository.findAll(0, 10, "nombre", "asc"))
                .thenReturn(Mono.just(pageWithoutTechIds));

        StepVerifier.create(capabilityPageableUseCase.execute(0, 10, "nombre", "asc"))
                .expectNextMatches(page -> 
                    page.getItems().get(0).getTechnologyIds().isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(capabilitiesRepository.findAll(0, 10, "nombre", "asc"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(capabilityPageableUseCase.execute(0, 10, "nombre", "asc"))
                .expectError(RuntimeException.class)
                .verify();
    }
}
