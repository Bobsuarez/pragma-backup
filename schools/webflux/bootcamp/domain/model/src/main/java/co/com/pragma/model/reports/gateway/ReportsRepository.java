package co.com.pragma.model.reports.gateway;

import co.com.pragma.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface ReportsRepository {

    Mono<Void> sendToReports(Bootcamp bootcamp, String token, String traceId);
}
