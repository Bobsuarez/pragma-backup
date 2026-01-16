package co.com.pragma.usecase;

import co.com.pragma.model.capablity.gateway.CapabilityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Log
@AllArgsConstructor
public class DeleteCapabilityUseCase {

    private final CapabilityRepository capabilityRepository;

    public Mono<Void> execute(Long capabilityId) {
        log.log(Level.INFO, "Executing delete capability use case, capabilityId={0}", capabilityId);

        // Verificar que la capacidad existe antes de eliminar
        return capabilityRepository.findById(capabilityId)
                .flatMap(exists -> {
                    if (!exists) {
                        log.warning("Capability not found for deletion, capabilityId=" + capabilityId);
                        return Mono.error(new RuntimeException("Capability not found with id: " + capabilityId));
                    }
                    return capabilityRepository.deleteById(capabilityId)
                            .doOnSuccess(v -> log.log(Level.INFO, 
                                    "Capability deleted successfully, capabilityId={0}", capabilityId))
                            .doOnError(error -> log.severe("Error deleting capability, capabilityId=" + capabilityId + ", error=" + error.getMessage()));
                })
                .then();
    }
}
