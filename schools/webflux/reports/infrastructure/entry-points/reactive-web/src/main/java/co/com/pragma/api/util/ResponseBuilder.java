package co.com.pragma.api.util;

import co.com.pragma.validator.dto.response.BootcampResponseDto;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {

    private ResponseBuilder() {
    }

    public static Map<String, Object> buildErrorResponse(String message, String traceId) {
        Map<String, Object> response = new HashMap<>();
        response.put("traceId", traceId);
        response.put("message", message);
        response.put("status", "Error interno del servidor");
        return response;
    }

    public static Map<String, Object> buildSuccessResponse(BootcampResponseDto responseDto, String traceId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Bootcamp creado exitosamente");
        response.put("data", responseDto);
        response.put("traceId", traceId);
        return response;
    }

    public static Map<String, Object> buildSuccessResponse(String message, String traceId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("traceId", traceId);
        return response;
    }

    public static <T> Map<String, Object> buildSuccessResponse(T data, String traceId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", data);
        response.put("traceId", traceId);
        return response;
    }
}
