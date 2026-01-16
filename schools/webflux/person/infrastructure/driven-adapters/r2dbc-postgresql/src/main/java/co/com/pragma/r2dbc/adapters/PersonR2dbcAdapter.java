package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.Person;
import co.com.pragma.model.PersonBootcamp;
import co.com.pragma.model.gateway.PersonRepository;
import co.com.pragma.r2dbc.entity.PersonBootcampEntity;
import co.com.pragma.r2dbc.entity.PersonEntity;
import co.com.pragma.r2dbc.mappers.PersonBootcampEntityMapper;
import co.com.pragma.r2dbc.providers.PersonBootcampSQLProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PersonR2dbcAdapter implements PersonRepository {

    private final DatabaseClient databaseClient;
    private final PersonBootcampEntityMapper entityMapper;
    private final PersonBootcampSQLProvider sqlProvider;
    
    @Override
    public Mono<Person> findById(Long id) {
        String sql = sqlProvider.findPersonById();
        return databaseClient.sql(sql)
                .bind("personId", id)
                .map((row, metadata) -> PersonEntity.builder()
                        .id(row.get("id", Long.class))
                        .name(row.get("name", String.class))
                        .email(row.get("email", String.class))
                        .build())
                .one()
                .map(entityMapper::toPersonDomain)
                .doOnError(error -> log.error("Error finding person by id: {}", id, error));
    }
    
    @Override
    @Transactional
    public Mono<PersonBootcamp> enrollPersonInBootcamp(Long personId, Long bootcampId) {
        String sql = sqlProvider.insertPersonBootcamp();
        return databaseClient.sql(sql)
                .bind("personId", personId)
                .bind("bootcampId", bootcampId)
                .map((row, metadata) -> PersonBootcampEntity.builder()
                        .id(row.get("id", Long.class))
                        .personId(row.get("person_id", Long.class))
                        .bootcampId(row.get("bootcamp_id", Long.class))
                        .build())
                .one()
                .map(saved -> entityMapper.toDomain(saved, null)) // Bootcamp se obtiene de API externa
                .doOnError(error -> log.error("Error enrolling person {} in bootcamp {}", personId, bootcampId, error));
    }
    
    @Override
    public Flux<PersonBootcamp> findEnrollmentsByPersonId(Long personId) {
        String sql = sqlProvider.findEnrollmentsByPersonId();
        return databaseClient.sql(sql)
                .bind("personId", personId)
                .map((row, metadata) -> PersonBootcampEntity.builder()
                        .id(row.get("id", Long.class))
                        .personId(row.get("person_id", Long.class))
                        .bootcampId(row.get("bootcamp_id", Long.class))
                        .build())
                .all()
                .map(entity -> entityMapper.toDomain(entity, null)) // Bootcamp se obtiene de API externa
                .doOnError(error -> log.error("Error finding enrollments for person: {}", personId, error));
    }
    
    @Override
    public Flux<Long> findBootcampIdsByPersonId(Long personId) {
        String sql = sqlProvider.findEnrollmentsByPersonId();
        return databaseClient.sql(sql)
                .bind("personId", personId)
                .map((row, metadata) -> row.get("bootcamp_id", Long.class))
                .all()
                .doOnError(error -> log.error("Error finding bootcamp IDs for person: {}", personId, error));
    }
    
    @Override
    public Mono<Long> countEnrollmentsByPersonId(Long personId) {
        String sql = sqlProvider.countEnrollmentsByPersonId();
        return databaseClient.sql(sql)
                .bind("personId", personId)
                .map((row, metadata) -> row.get("count", Long.class))
                .one()
                .defaultIfEmpty(0L);
    }

    @Override
    public Flux<Person> findPeopleByBootcampId(Long bootcampId) {
        String sql = sqlProvider.findPeopleByBootcampId();
        return databaseClient.sql(sql)
                .bind("bootcampId", bootcampId)
                .map((row, metadata) -> PersonEntity.builder()
                        .id(row.get("id", Long.class))
                        .name(row.get("name", String.class))
                        .email(row.get("email", String.class))
                        .build())
                .all()
                .map(entityMapper::toPersonDomain)
                .doOnError(error -> log.error("Error finding people for bootcamp: {}", bootcampId, error));
    }
}

