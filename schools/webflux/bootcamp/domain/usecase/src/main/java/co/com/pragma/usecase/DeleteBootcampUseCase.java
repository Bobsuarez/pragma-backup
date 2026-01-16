package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.gateway.BootcampDeleteRepository;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RequiredArgsConstructor
@Log
public class DeleteBootcampUseCase {

    private final BootcampDeleteRepository bootcampDeleteRepository;
    private final CapabilityRepository capabilityRepository;

    public Mono<Void> execute(Long bootcampId, String traceId) {

        logShowMessage("Starting bootcamp deletion, bootcampId={0}, traceId={1}", bootcampId, traceId);

        return bootcampDeleteRepository.getCapabilityIdsByBootcampId(bootcampId)
                .flatMap(capabilityId -> processCapability(capabilityId, bootcampId, traceId))
                .then(deleteBootcampRelationships(bootcampId, traceId))
                .then(deleteBootcamp(bootcampId, traceId))
                .doOnSuccess(v -> logShowMessage(
                        "Bootcamp deleted successfully, bootcampId={0}, traceId={1}",
                        bootcampId, traceId
                ))
                .doOnError(error -> logShowMessage(
                        "Error deleting bootcamp, bootcampId={0}, traceId={1}, error={2}",
                        bootcampId, traceId, error.getMessage()
                ));
    }

    private Mono<Void> processCapability(Long capabilityId, Long bootcampId, String traceId) {

        logShowMessage(
                "Processing capability, capabilityId={0}, bootcampId={1}, traceId={2}",
                capabilityId, bootcampId, traceId
        );

        return bootcampDeleteRepository.isCapabilityUsedByOtherBootcamps(capabilityId, bootcampId)
                .flatMap(isUsed -> {
                    if (isUsed) {
                        logShowMessage(
                                "Capability is used by other bootcamps, skipping deletion, capabilityId={0}, traceId={1}",
                                capabilityId, traceId
                        );
                        return Mono.empty();
                    }
                    return processTechnologiesAndDeleteCapability(capabilityId, traceId);
                });
    }

    private Mono<Void> processTechnologiesAndDeleteCapability(Long capabilityId, String traceId) {

        logShowMessage(
                "Processing technologies for capability, capabilityId={0}, traceId={1}",
                capabilityId, traceId
        );

        return capabilityRepository.getTechnologiesByCapabilityId(capabilityId)
                .flatMap(technology -> processTechnology(technology.getId(), traceId))
                .then(capabilityRepository.deleteCapability(capabilityId))
                .doOnSuccess(v -> logShowMessage(
                        "Capability and its technologies processed, capabilityId={0}, traceId={1}",
                        capabilityId, traceId
                ));
    }

    private Mono<Void> processTechnology(Long technologyId, String traceId) {

        logShowMessage("Processing technology, technologyId={0}, traceId={1}", technologyId, traceId);

        return capabilityRepository.getTechnologyUsageCount(technologyId)
                .filter(count -> count == 1)
                .flatMap(count -> {
                    logShowMessage(
                            "Technology is only used by one capability, deleting, technologyId={0}, traceId={1}",
                            technologyId, traceId
                    );
                    return capabilityRepository.deleteTechnology(technologyId);
                })
                .doOnSuccess(v -> logShowMessage(
                        "Technology processed, technologyId={0}, traceId={1}",
                        technologyId,
                        traceId
                ))
                .switchIfEmpty(Mono.defer(() -> {
                    logShowMessage(
                            "Technology is used by other capabilities, skipping deletion, technologyId={0}, traceId={1}",
                            technologyId, traceId
                    );
                    return Mono.empty();
                }));
    }

    private Mono<Void> deleteBootcampRelationships(Long bootcampId, String traceId) {
        logShowMessage(
                "Bootcamp relationships will be deleted with bootcamp, bootcampId={0}, traceId={1}",
                bootcampId, traceId
        );
        return Mono.empty();
    }

    private Mono<Void> deleteBootcamp(Long bootcampId, String traceId) {
        logShowMessage(
                "Deleting bootcamp, bootcampId={0}, traceId={1}",
                bootcampId, traceId
        );
        return bootcampDeleteRepository.deleteById(bootcampId);
    }

    private void logShowMessage(String message, Object... objectList) {
        log.log(Level.INFO, message, new Object[]{objectList});
    }
}

