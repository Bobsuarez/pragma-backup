package co.com.pragma.usecase;

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
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTechnologiesByCapabilityUseCase Tests")
class GetTechnologiesByCapabilityUseCaseTest {

    @Mock
    private TechnologyRepository technologyR2dbcRepository;

    @Mock
    private TechnologyRepository technologyApiRepository;


    private Long capabilityId = 1L;
    private List<Long> technologyIds;
    private Technology technology1;
    private Technology technology2;

    @BeforeEach
    void setUp() {
        technologyIds = Arrays.asList(1L, 2L);
        
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
    @DisplayName("Should return technologies when capability has associated technologies")
    void shouldReturnTechnologiesWhenCapabilityHasTechnologies() {

        when(technologyR2dbcRepository.findTechnologyIdsByCapabilityId(capabilityId))
                .thenReturn(Flux.fromIterable(technologyIds));
        
        when(technologyApiRepository.findAllByIds(any(TechnologyIds.class)))
                .thenReturn(Flux.just(technology1, technology2));

        GetTechnologiesByCapabilityUseCase getTechnologiesByCapabilityUseCase =
                new GetTechnologiesByCapabilityUseCase(technologyR2dbcRepository, technologyApiRepository);

        StepVerifier.create(getTechnologiesByCapabilityUseCase.execute(capabilityId))
                .expectNextMatches(tech -> tech.getId().equals(1L) && tech.getName().equals("Java"))
                .expectNextMatches(tech -> tech.getId().equals(2L) && tech.getName().equals("Spring"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when capability has no technologies")
    void shouldReturnEmptyWhenCapabilityHasNoTechnologies() {
        when(technologyR2dbcRepository.findTechnologyIdsByCapabilityId(capabilityId))
                .thenReturn(Flux.empty());

        GetTechnologiesByCapabilityUseCase getTechnologiesByCapabilityUseCase =
                new GetTechnologiesByCapabilityUseCase(technologyR2dbcRepository, technologyApiRepository);

        StepVerifier.create(getTechnologiesByCapabilityUseCase.execute(capabilityId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate error when repository fails")
    void shouldPropagateErrorWhenRepositoryFails() {
        when(technologyR2dbcRepository.findTechnologyIdsByCapabilityId(capabilityId))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        GetTechnologiesByCapabilityUseCase getTechnologiesByCapabilityUseCase =
                new GetTechnologiesByCapabilityUseCase(technologyR2dbcRepository, technologyApiRepository);

        StepVerifier.create(getTechnologiesByCapabilityUseCase.execute(capabilityId))
                .expectError(RuntimeException.class)
                .verify();
    }
}
