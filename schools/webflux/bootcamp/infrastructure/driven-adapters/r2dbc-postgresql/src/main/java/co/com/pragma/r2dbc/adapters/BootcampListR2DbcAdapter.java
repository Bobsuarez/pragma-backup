package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.bootcamp.BootcampList;
import co.com.pragma.model.bootcamp.BootcampPage;
import co.com.pragma.model.bootcamp.gateway.BootcampListRepository;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import co.com.pragma.r2dbc.dto.BootcampListEntity;
import co.com.pragma.r2dbc.repositories.BootcampR2dbcRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BootcampListR2DbcAdapter implements BootcampListRepository {

    private final DatabaseClient databaseClient;
    private final BootcampR2dbcRepository bootcampReactiveRepository;
    private final ObjectMapper mapper;

    @Override
    public Mono<BootcampPage> findAll(int page, int size, BootcampSortField sortField, SortDirection sortDirection) {
        log.debug(
                "Finding all capabilities with pagination, page={}, size={}, sort={}, dir={}",
                page,
                size,
                sortField,
                sortDirection
        );

        String query = """
                SELECT b.id,
                       b.name,
                       b.description,
                       (SELECT JSON_AGG(bc2.capability_id)
                        FROM bootcamp_capability bc2
                        WHERE b.id = bc2.bootcamp_id) AS idCapabilities
                FROM bootcamp b
                ORDER BY CASE WHEN :sortField = 'nombre' AND :direction = 'ASC' THEN b.name END ASC,
                         CASE WHEN :sortField = 'nombre' AND :direction = 'DESC' THEN b.name END DESC,
                         CASE
                             WHEN :sortField = 'capacidades' AND :direction = 'ASC' THEN
                                 (SELECT COUNT(*) FROM bootcamp_capability bc WHERE bc.bootcamp_id = b.id)
                             END ASC,
                         CASE
                             WHEN :sortField = 'capacidades' AND :direction = 'DESC' THEN
                                 (SELECT COUNT(*) FROM bootcamp_capability ct WHERE ct.bootcamp_id = b.id)
                             END DESC
                LIMIT :limit OFFSET :offset;
                """;

        return databaseClient.sql(query)
                .bind("sortField", sortField.getName())
                .bind("direction", sortDirection.name())
                .bind("limit", size)
                .bind("offset", (long) page * size)
                .map(row -> new BootcampListEntity(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("idcapabilities", String.class)
                ))
                .all()
                .map(this::toDomain) // Convierte Entity a Dominio (con lista de IDs)
                .collectList()
                .doOnNext(capabilities -> log.debug("Retrieved {} bootcamps from database", capabilities.size()))
                .zipWith(bootcampReactiveRepository.countAll())
                .doOnNext(tuple -> log.debug("Total bootcamps count: {}", tuple.getT2()))
                .map(t -> new BootcampPage(t.getT1(), t.getT2()))
                .doOnSuccess(pageData -> log.info(
                        "Successfully retrieved bootcamps page, itemsCount={}, totalElements={}",
                        pageData.getContent()
                                .size(), pageData.getTotalElements()
                ))
                .doOnError(error -> log.error(
                        "Error finding capabilities with pagination, page={}, size={}, sort={}, dir={}",
                        page, size, sortField, sortDirection, error
                ));
    }

    private BootcampList toDomain(BootcampListEntity entity) {
        try {

            List<Long> ids = (entity.getIdCapabilities() == null) ? List.of() :
                    mapper.readValue(
                            entity.getIdCapabilities(), new TypeReference<List<Long>>() {
                            }
                    );

            return BootcampList.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .idCapabilities(ids)
                    .build();

        } catch (Exception e) {
            log.error(
                    "Error parsing technology IDs JSON for capability, capabilityId={}, error={}",
                    entity.getId(), e.getMessage()
            );
            return BootcampList.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .build();
        }
    }
}

