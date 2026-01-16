package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.capability.CapabilityIds;
import co.com.pragma.model.capability.CapabilityValidationResult;
import co.com.pragma.model.capability.gateway.CapabilityRepository;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.bootcamp.gateway.BootcampRepository;
import co.com.pragma.model.reports.gateway.ReportsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.logging.Level;

@RequiredArgsConstructor
@Log
public class CreateBootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final CapabilityRepository capabilityRepository;
    private final ReportsRepository reportsRepository;

    public Mono<Bootcamp> execute(Bootcamp bootcamp, String traceId, String token) {
        return withLogging(
                createBootcampFlow(bootcamp, traceId, token),
                bootcamp,
                traceId
        );
    }

    private Mono<Bootcamp> createBootcampFlow(
            Bootcamp bootcamp,
            String traceId,
            String token
    ) {

        return Mono.fromRunnable(() -> validateCapabilitiesCount(bootcamp))
                .then(Mono.defer(() -> {
                    List<String> capabilityIds = bootcamp.getCapabilities()
                            .stream()
                            .map(cap -> cap.getId().toString())
                            .toList();

                    return validateCapabilitiesExist(capabilityIds, traceId);
                }))
                .then(bootcampRepository.save(bootcamp))
                .flatMap(saved ->
                                 reportsRepository.sendToReports(saved, token, traceId)
                                         .subscribeOn(Schedulers.boundedElastic())
                                         .onErrorResume(e -> Mono.empty())
                                         .thenReturn(saved)
                );
    }

    private Mono<Bootcamp> withLogging(
            Mono<Bootcamp> source,
            Bootcamp bootcamp,
            String traceId
    ) {
        return source
                .doOnSubscribe(s -> log.log(
                        Level.INFO,
                        "Creating bootcamp, name={0}, traceId={1}",
                        new Object[]{bootcamp.getName(), traceId}
                ))
                .doOnSuccess(saved -> log.log(
                        Level.INFO,
                        "Bootcamp created successfully, id={0}, traceId={1}",
                        new Object[]{saved.getId(), traceId}
                ))
                .doOnError(error -> log.log(
                        Level.SEVERE,
                        "Error creating bootcamp, traceId={0}, error={1}",
                        new Object[]{traceId, error.getMessage()}
                ));
    }

    private void validateCapabilitiesCount(Bootcamp bootcamp) {

        int size = bootcamp.getCapabilities()
                .size();

        if (size < 1 || size > 4) {
            throw new BusinessException(
                    "Un bootcamp debe tener entre 1 y 4 capacidades asociadas"
            );
        }
    }

    private Mono<Void> validateCapabilitiesExist(List<String> capabilityIds, String traceId) {

        return capabilityRepository.findAllByIds(
                        CapabilityIds.builder()
                                .capabilityIds(capabilityIds)
                                .build()
                )
                .flatMap(this::handleCapabilityValidationResult);
    }

    private Mono<Void> handleCapabilityValidationResult(CapabilityValidationResult result) {

        if (!result.getIsValidate()) {
            return Mono.error(new BusinessException(
                    "Una o más capacidades no existen en el sistema: " + result.getMessage()));
        }
        return Mono.empty();
    }
}

