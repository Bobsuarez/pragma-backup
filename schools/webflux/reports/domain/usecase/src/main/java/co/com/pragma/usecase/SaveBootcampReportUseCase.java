package co.com.pragma.usecase;

import co.com.pragma.model.BootcampReport;
import co.com.pragma.model.gateway.BootcampMetricsRepository;
import co.com.pragma.model.gateway.BootcampReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.function.Tuple3;

import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Log
public class SaveBootcampReportUseCase {

    private static final String TOKEN_LABEL = "token";
    private static final String TRACE_ID_LABEL = "traceId";
    private final BootcampReportRepository bootcampReportRepository;
    private final BootcampMetricsRepository bootcampMetricsRepository;


    public Mono<Void> execute(Long bootcampId, String traceId, String token) {

        return bootcampMetricsRepository.getBootcampInfo(bootcampId)
                .flatMap(bootcampInfo ->
                                 Mono.zip(
                                                 countCapabilities(bootcampId),
                                                 countTechnologies(bootcampId),
                                                 countPeople(bootcampId)
                                         )
                                         .map(counts -> buildReport(bootcampInfo, counts))
                )
                .flatMap(bootcampReportRepository::save)
                .contextWrite(Context.of(
                        TOKEN_LABEL, token,
                        TRACE_ID_LABEL, traceId
                ))
                .then()
                .doOnSuccess(v ->
                                     log.info(() -> String.format(
                                             "Bootcamp report saved successfully, bootcampId=%d, traceId=%s",
                                             bootcampId, traceId
                                     ))
                )
                .doOnError(error ->
                                   log.severe(() -> String.format(
                                           "Error saving bootcamp report, bootcampId=%d, traceId=%s, error=%s",
                                           bootcampId, traceId, error.getMessage()
                                   ))
                );
    }

    private Mono<Integer> countCapabilities(Long bootcampId) {
        return bootcampMetricsRepository
                .countCapabilitiesByBootcampId(bootcampId)
                .onErrorReturn(0);
    }

    private Mono<Integer> countTechnologies(Long bootcampId) {
        return bootcampMetricsRepository
                .countTechnologiesByBootcampId(bootcampId)
                .onErrorReturn(0);
    }

    private Mono<Integer> countPeople(Long bootcampId) {
        return bootcampMetricsRepository
                .countEnrolledPeopleByBootcampId(bootcampId)
                .onErrorReturn(0);
    }

    private BootcampReport buildReport(
            BootcampMetricsRepository.BootcampInfo bootcampInfo,
            Tuple3<Integer, Integer, Integer> counts
    ) {

        return BootcampReport.builder()
                .bootcampId(bootcampInfo.id())
                .bootcampName(bootcampInfo.name())
                .bootcampDescription(bootcampInfo.description())
                .launchDate(bootcampInfo.launchDate())
                .durationMonths(bootcampInfo.durationMonths())
                .capabilitiesCount(counts.getT1())
                .technologiesCount(counts.getT2())
                .enrolledPeopleCount(counts.getT3())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

