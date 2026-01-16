package co.com.pragma.model.gateway;

import co.com.pragma.model.Person;
import co.com.pragma.model.PersonBootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {

    Mono<Person> findById(Long id);

    Mono<PersonBootcamp> enrollPersonInBootcamp(Long personId, Long bootcampId);

    Flux<PersonBootcamp> findEnrollmentsByPersonId(Long personId);

    /**
     * Obtiene los IDs de los bootcamps en los que está inscrita una persona
     * @param personId ID de la persona
     * @return Flux con los IDs de los bootcamps
     */
    Flux<Long> findBootcampIdsByPersonId(Long personId);

    /**
     * Cuenta el número de bootcamps en los que está inscrita una persona
     * @param personId ID de la persona
     * @return Mono con el conteo de bootcamps
     */
    Mono<Long> countEnrollmentsByPersonId(Long personId);

    /**
     * Obtiene las personas inscritas en un bootcamp
     * @param bootcampId ID del bootcamp
     * @return Flux con las personas inscritas
     */
    Flux<Person> findPeopleByBootcampId(Long bootcampId);
}

