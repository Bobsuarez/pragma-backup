package co.com.pragma.api.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static Map<String, Object> buildSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("traceId", "traceId");
        response.put("status", "success");
        response.put("data", data);
        return response;
    }


    public static Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("traceId", "traceId");
        response.put("message", message);
        response.put("status", "error");
        return response;
    }
}
