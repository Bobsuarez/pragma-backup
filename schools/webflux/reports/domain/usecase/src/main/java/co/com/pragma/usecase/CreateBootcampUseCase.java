package co.com.pragma.usecase;

import co.com.pragma.model.Bootcamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Log
public class CreateBootcampUseCase {

    private final SaveBootcampReportUseCase saveBootcampReportUseCase;
    
    public Mono<Bootcamp> execute(Bootcamp bootcamp, String traceId, String token) {

        return Mono.just(bootcamp)
                .doOnSuccess(saved -> {

                    log.info("Bootcamp created successfully, id: "+saved.getId()+", traceId: " + traceId);
                    
                    // Disparar el guardado del reporte de forma asíncrona sin bloquear la respuesta
                    // Usando subscribeOn para ejecutar en un hilo diferente y no afectar el rendimiento
                    saveBootcampReportUseCase.execute(saved.getId(), traceId, token)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(
                                    null,
                                    error -> log.severe("Error saving bootcamp report asynchronously, bootcampId: " + saved.getId() + ", traceId: " + traceId + ", error: " + error.getMessage())
                            );
                })
                .doOnError(error -> log.severe("Error creating bootcamp, traceId: " + traceId + ", error: " + error.getMessage()));
    }
}

