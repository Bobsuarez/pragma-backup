package co.com.pragma.exception.commands.factories;

import co.com.pragma.exception.commands.handlers.ExceptionHandlerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public final class ExceptionHandlerFactory {

    private final Map<String, ExceptionHandlerRegistry> handlers;

    public ExceptionHandlerFactory(List<ExceptionHandlerRegistry> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        handler -> handler.getExceptionClass().getSimpleName(),
                        handler ->
                                handler, (existing, replacement) -> existing // Por si hay duplicados, conserva el primero
                ));
    }

    public Optional<ExceptionHandlerRegistry> getExceptionHandler(String methodClass) {
        return Optional.ofNullable(handlers.get(methodClass));
    }

}
