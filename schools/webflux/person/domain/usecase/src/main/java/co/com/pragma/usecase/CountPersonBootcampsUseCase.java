package co.com.pragma.usecase;

import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Log
public class CountPersonBootcampsUseCase {

    private final PersonRepository personRepository;

    public Mono<Long> execute(Long personId, String traceId) {
        log.info(String.format("Counting bootcamps for person %d, traceId: %s", personId, traceId));

        return personRepository.findById(personId)
                .switchIfEmpty(Mono.error(new BusinessException("La persona no existe")))
                .flatMap(person -> personRepository.countEnrollmentsByPersonId(personId))
                .doOnSuccess(count -> log.info(String.format(
                        "Successfully counted bootcamps for person %d, count: %d, traceId: %s",
                        personId, count, traceId
                )))
                .doOnError(error -> log.severe(String.format(
                        "Error counting bootcamps for person %d, traceId: %s, error: %s",
                        personId, traceId, error.getMessage()
                )));
    }
}
