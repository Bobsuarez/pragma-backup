package co.com.pragma.exception.utils;

import co.com.pragma.exception.dto.ErrorDTO;
import co.com.pragma.exception.dto.ResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ResponseUtils {

    public static <T> DataBuffer buildDataBufferResponse(
            ServerHttpResponse response,
            ResponseDTO<T> responseDTO
    ) {
        final ObjectMapper objectMapper = new ObjectMapper();
        DataBufferFactory bufferFactory = response.bufferFactory();
        try {
            return bufferFactory.wrap(objectMapper.writeValueAsBytes(responseDTO));
        } catch (JsonProcessingException ex) {
            log.error("Error writing response", ex);
            return bufferFactory.wrap(new byte[0]);
        }
    }

    public static ErrorDTO getErrorDTOMap(
            String key,
            String value
    ) {
        HashMap<String, String> errorsMap = new HashMap<>();
        errorsMap.put(key, value);

        return ErrorDTO.builder()
                .details(List.of(errorsMap))
                .build();
    }

    public static ErrorDTO getListErrorDTOMap(
           Map<String , String> errorsMap
    ) {
        return ErrorDTO.builder()
                .details(List.of(errorsMap))
                .build();
    }

    public static ErrorDTO getErrorDTOFromMap(
            final Map<String, String> messageMap
    ) {
        return ErrorDTO.builder()
                .details(List.of(messageMap))
                .build();
    }
}
