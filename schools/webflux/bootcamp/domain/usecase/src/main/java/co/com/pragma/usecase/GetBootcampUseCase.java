package co.com.pragma.usecase;

import co.com.pragma.model.bootcamp.Bootcamp;
import co.com.pragma.model.bootcamp.gateway.BootcampRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RequiredArgsConstructor
@Log
public class GetBootcampUseCase {

    private final BootcampRepository bootcampRepository;

    public Mono<Bootcamp> execute(Long bootcampId, String traceId) {
        log.log(Level.INFO, "Getting bootcamp, bootcampId={0}, traceId={1}", 
            new Object[]{bootcampId, traceId});

        return bootcampRepository.findById(bootcampId)
                .doOnSuccess(bootcamp -> log.log(Level.INFO, 
                    "Bootcamp retrieved successfully, bootcampId={0}, traceId={1}", 
                    new Object[]{bootcampId, traceId}))
                .doOnError(error -> log.log(Level.SEVERE, 
                    "Error getting bootcamp, bootcampId={0}, traceId={1}, error={2}", 
                    new Object[]{bootcampId, traceId, error.getMessage()}));
    }
}
