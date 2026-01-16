package co.com.pragma.config;

import co.com.pragma.model.technology.gateway.TechnologyRepository;
import co.com.pragma.usecase.GetTechnologiesByCapabilityUseCase;
import co.com.pragma.usecase.GetTechnologyUsageCountUseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TechnologyUseCasesConfig {

    @Bean
    public GetTechnologiesByCapabilityUseCase getTechnologiesByCapabilityUseCase(
            @Qualifier("technologyR2dbcRepository") TechnologyRepository technologyR2dbcRepository,
            @Qualifier("technologyAdapter") TechnologyRepository technologyApiRepository) {
        return new GetTechnologiesByCapabilityUseCase(technologyR2dbcRepository, technologyApiRepository);
    }

    @Bean
    public GetTechnologyUsageCountUseCase getTechnologyUsageCountUseCase(
            @Qualifier("technologyR2dbcRepository") TechnologyRepository technologyR2dbcRepository) {
        return new GetTechnologyUsageCountUseCase(technologyR2dbcRepository);
    }
}
