package co.com.pragma.api.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.UUID;

@UtilityClass
public class EntryPointsUtil {

    public String extractTraceId(ServerRequest request) {
        String traceId = request.headers().firstHeader("X-B3-TraceId");
        return traceId != null && !traceId.isEmpty()
                ? traceId.replace("\"", "")
                : UUID.randomUUID().toString();
    }
}
