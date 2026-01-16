package co.com.pragma.usecase;

import co.com.pragma.model.Person;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampRepository;
import co.com.pragma.model.gateway.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RequiredArgsConstructor
@Log
public class GetPeopleByBootcampUseCase {

    private final PersonRepository personRepository;
    private final BootcampRepository bootcampRepository;

    public Flux<Person> execute(Long bootcampId, String traceId) {
        log.info(String.format("Getting people enrolled in bootcamp %d, traceId: %s", bootcampId, traceId));

        return bootcampRepository.findById(bootcampId)
                .contextWrite(Context.of("traceId", traceId))
                .switchIfEmpty(Mono.error(new BusinessException("El bootcamp no existe")))
                .thenMany(personRepository.findPeopleByBootcampId(bootcampId))
                .doOnComplete(() -> log.info(String.format(
                        "Successfully retrieved people for bootcamp %d, traceId: %s",
                        bootcampId, traceId
                )))
                .doOnError(error -> log.severe(String.format(
                        "Error retrieving people for bootcamp %d, traceId: %s, error: %s",
                        bootcampId, traceId, error.getMessage()
                )));
    }
}
