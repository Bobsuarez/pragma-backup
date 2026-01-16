package co.com.pragma.usecase;

import co.com.pragma.model.gateway.BootcampDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RequiredArgsConstructor
@Log
public class GetBootcampDetailUseCase {

    private static final String TRACE_ID_PARAMETER = "traceId=";
    
    private final BootcampDetailRepository bootcampDetailRepository;
    
    public Mono<BootcampDetailRepository.BootcampDetail> execute(Long bootcampId, String traceId, String token) {

        log.info("Getting bootcamp detail, bootcampId=" + bootcampId + ", " + TRACE_ID_PARAMETER + traceId);
        
        return bootcampDetailRepository.getBootcampDetail(bootcampId)
                .contextWrite(Context.of("token", token, "traceId", traceId))
                .doOnSuccess(detail -> log.info("Bootcamp detail retrieved successfully, bootcampId=" + bootcampId +
                                                        ", " + TRACE_ID_PARAMETER + traceId))
                .doOnError(error -> log.severe("Error getting bootcamp detail, bootcampId=" + bootcampId + ", " + TRACE_ID_PARAMETER + traceId + ", error: " + error.getMessage()));
    }
}
