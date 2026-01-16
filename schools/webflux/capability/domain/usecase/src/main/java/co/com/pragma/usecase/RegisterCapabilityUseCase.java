package co.com.pragma.usecase;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import co.com.pragma.model.exceptions.BusinessException;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Log
public class RegisterCapabilityUseCase {

    private final CapabilityRepository capabilityRepository;

    public RegisterCapabilityUseCase(CapabilityRepository capabilityRepository) {
        this.capabilityRepository = capabilityRepository;
    }

    public Mono<Capability> execute(Capability capability) {

        log.info("Executing register capability use case, capabilityName=" + capability.getName());

        if (hasDuplicateTechnologies(capability)) {
            return Mono.error(new BusinessException("Las tecnologias no pueden estar repetidas"));
        }

        log.log(
                Level.INFO,
                "Technology IDs validation passed, capabilityName={0}, technologyCount={1}",
                new Object[]{capability.getName(), capability.getTechnologyIds().size()}
        );

        return capabilityRepository.save(capability)
                .doOnSuccess(cap -> log.log(
                        Level.INFO,
                        "Capability registered successfully, capabilityId={0}, capabilityName={1}",
                        new Object[]{cap.getId(), cap.getName()}
                ))
                .doOnError(error ->
                                   log.severe("Error registering capability, error=" + error.getMessage())
                );
    }

    private boolean hasDuplicateTechnologies(Capability capability) {
        return capability.getTechnologyIds().stream().distinct().count()
                != capability.getTechnologyIds().size();
    }

}
