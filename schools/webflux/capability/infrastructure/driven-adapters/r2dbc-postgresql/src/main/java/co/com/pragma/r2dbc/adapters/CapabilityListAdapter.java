package co.com.pragma.r2dbc.adapters;

import co.com.pragma.model.capablity.Capability;
import co.com.pragma.model.capablity.CapabilityPage;
import co.com.pragma.model.capablity.gateway.capabilitylist.CapabilityListRepository;
import co.com.pragma.r2dbc.entity.CapabilityEntity;
import co.com.pragma.r2dbc.repositories.CapabilityReactiveRepository;
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
public class CapabilityListAdapter implements CapabilityListRepository {

    private final DatabaseClient client;
    private final ObjectMapper mapper;
    private final CapabilityReactiveRepository capabilityReactiveRepository;

    @Override
    public Mono<CapabilityPage> findAll(int page, int size, String sort, String dir) {
        log.debug("Finding all capabilities with pagination, page={}, size={}, sort={}, dir={}", page, size, sort, dir);
        
        String query = """
                SELECT c.id,
                       c.name,
                       c.description,
                       (SELECT JSON_AGG(ct2.technology_id)
                        FROM capability_technology ct2
                        WHERE c.id = ct2.capability_id) AS idstecnologies
                FROM capability c
                ORDER BY CASE WHEN :sortField = 'nombre' AND :direction = 'ASC' THEN c.name END ASC,
                         CASE WHEN :sortField = 'nombre' AND :direction = 'DESC' THEN c.name END DESC,
                         CASE
                             WHEN :sortField = 'tecnologias' AND :direction = 'ASC' THEN
                                 (SELECT COUNT(*) FROM capability_technology ct WHERE ct.capability_id = c.id)
                             END ASC,
                         CASE
                             WHEN :sortField = 'tecnologias' AND :direction = 'DESC' THEN
                                 (SELECT COUNT(*) FROM capability_technology ct WHERE ct.capability_id = c.id)
                             END DESC
                LIMIT :limit OFFSET :offset;
                """;

        return client.sql(query)
                .bind("sortField", sort)
                .bind("direction", dir.toUpperCase())
                .bind("limit", size)
                .bind("offset", (long) page * size)
                .map(row -> new CapabilityEntity(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("idstecnologies", String.class)
                ))
                .all()
                .map(this::toDomain) // Convierte Entity a Dominio (con lista de IDs)
                .collectList()
                .doOnNext(capabilities -> log.debug("Retrieved {} capabilities from database", capabilities.size()))
                .zipWith(capabilityReactiveRepository.countAll())
                .doOnNext(tuple -> log.debug("Total capabilities count: {}", tuple.getT2()))
                .map(t -> new CapabilityPage(t.getT1(), t.getT2()))
                .doOnSuccess(pageData -> log.info("Successfully retrieved capabilities page, itemsCount={}, totalElements={}", 
                        pageData.getItems().size(), pageData.getTotalElements()))
                .doOnError(error -> log.error("Error finding capabilities with pagination, page={}, size={}, sort={}, dir={}", 
                        page, size, sort, dir, error));
    }

    private Capability toDomain(CapabilityEntity entity) {
        try {

            List<Long> ids = (entity.getIdsTechnologies() == null) ? List.of() :
                    mapper.readValue(
                            entity.getIdsTechnologies(), new TypeReference<List<Long>>() {
                            }
                    );

            return Capability.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .technologyIds(ids)
                    .technologies(List.of())
                    .build();

        } catch (Exception e) {
            log.warn("Error parsing technology IDs JSON for capability, capabilityId={}, error={}", 
                    entity.getId(), e.getMessage());
            return Capability.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .technologyIds(List.of())
                    .technologies(List.of())
                    .build();
        }
    }
}


