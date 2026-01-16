package co.com.pragma.usecase;

import co.com.pragma.model.Bootcamp;
import co.com.pragma.model.PersonBootcamp;
import co.com.pragma.model.exceptions.BusinessException;
import co.com.pragma.model.gateway.BootcampRepository;
import co.com.pragma.model.gateway.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Log
public class EnrollPersonInBootcampUseCase {

    private static final int MAX_BOOTCAMPS = 5;

    private final PersonRepository personRepository;
    private final BootcampRepository bootcampRepository;

    public Mono<PersonBootcamp> execute(Long personId, Long bootcampId, String traceId) {

        log.info(String.format("Enrolling person %d in bootcamp %d, traceId: %s", personId, bootcampId, traceId));

        return personRepository.findById(personId)
                .switchIfEmpty(Mono.error(new BusinessException("La persona no existe")))
                .flatMap(person -> bootcampRepository.findById(bootcampId)
                        .contextWrite(Context.of("traceId", traceId))
                        .switchIfEmpty(Mono.error(new BusinessException("El bootcamp no existe")))
                        .flatMap(newBootcamp -> validateEnrollment(personId, newBootcamp, traceId)
                                .then(personRepository.enrollPersonInBootcamp(personId, bootcampId)
                                              .flatMap(personBootcamp -> Mono.just(personBootcamp.toBuilder()
                                                                                           .bootcamp(newBootcamp)
                                                                                           .build()))))
                        .doOnSuccess(result -> log.info(String.format(
                                "Successfully enrolled person %d in bootcamp %d, traceId: %s",
                                personId, bootcampId, traceId
                        )))
                        .doOnError(error -> log.severe(String.format(
                                "Error enrolling person %d in bootcamp %d, traceId: %s, error: %s",
                                personId, bootcampId, traceId, error.getMessage()
                        ))));
    }

    private Mono<Void> validateEnrollment(Long personId, Bootcamp newBootcamp, String traceId) {

        log.info(
                String.format(
                        "validateEnrollment person %d in bootcamp %d, traceId: %s", personId, newBootcamp.getId(),
                        traceId
                ));

        LocalDate newStart = newBootcamp.getLaunchDate();
        LocalDate newEnd = newStart.plusMonths(newBootcamp.getDurationMonths());

        // Obtener IDs de bootcamps desde BD y luego obtener cada bootcamp de la API externa
        return personRepository.findBootcampIdsByPersonId(personId)
                .flatMap(bootcampId -> bootcampRepository.findById(bootcampId)
                        .contextWrite(Context.of("traceId", traceId)))
                .collectList()
                .flatMap(existing -> {

                    validateNotAlreadyEnrolled(existing, newBootcamp);
                    validateMaxBootcamps(existing);
                    validateNoDateOverlap(existing, newBootcamp, newStart, newEnd);

                    return Mono.empty();
                });
    }

    private void validateNotAlreadyEnrolled(List<Bootcamp> existing, Bootcamp newBootcamp) {
        if (existing.stream().anyMatch(b -> b.getId().equals(newBootcamp.getId()))) {
            throw new BusinessException("La persona ya está inscrita en el bootcamp '" + newBootcamp.getName() + "'");
        }
    }

    private void validateMaxBootcamps(List<Bootcamp> existing) {
        if (existing.size() >= MAX_BOOTCAMPS) {
            throw new BusinessException(
                    "No se puede inscribir en más de " + MAX_BOOTCAMPS + " bootcamps simultáneamente"
            );
        }
    }

    private void validateNoDateOverlap(
            List<Bootcamp> existing,
            Bootcamp newBootcamp,
            LocalDate newStart,
            LocalDate newEnd
    ) {

        existing.stream()
                .filter(b -> datesOverlap(
                        newStart,
                        newEnd,
                        b.getLaunchDate(),
                        b.getLaunchDate()
                                .plusMonths(b.getDurationMonths())
                ))
                .findFirst()
                .ifPresent(b -> {
                    throw new BusinessException(
                            "El bootcamp '" + newBootcamp.getName() + "' se solapa con el bootcamp '" + b.getName() +
                                    "'"
                    );
                });
    }


    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        // Dos rangos se solapan si:
        // - El inicio del primero está dentro del segundo, O
        // - El fin del primero está dentro del segundo, O
        // - El primero contiene completamente al segundo
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
}

