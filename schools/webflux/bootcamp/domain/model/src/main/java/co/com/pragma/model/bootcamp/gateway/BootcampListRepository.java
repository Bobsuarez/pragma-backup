package co.com.pragma.model.bootcamp.gateway;

import co.com.pragma.model.bootcamp.BootcampPage;
import co.com.pragma.model.enums.BootcampSortField;
import co.com.pragma.model.enums.SortDirection;
import reactor.core.publisher.Mono;

public interface BootcampListRepository {

    Mono<BootcampPage> findAll(int page, int size, BootcampSortField sortField, SortDirection sortDirection);
}

