package co.com.pragma.model.gateway;

import co.com.pragma.model.BootcampReport;
import reactor.core.publisher.Mono;

public interface BootcampReportRepository {

    Mono<BootcampReport> save(BootcampReport bootcampReport);
}

