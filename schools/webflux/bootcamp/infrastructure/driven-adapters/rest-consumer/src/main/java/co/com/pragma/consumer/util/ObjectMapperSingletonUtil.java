package co.com.pragma.consumer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public final class ObjectMapperSingletonUtil {

    private static ObjectMapper instance;

    private ObjectMapperSingletonUtil() {
        // Constructor privado para evitar la creación de instancias adicionales
    }

    public static ObjectMapper getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ObjectMapper();
        }
        return instance;
    }

    /**
     * Method of parse object to String
     *
     * @param <T>    Param of Exit
     * @param object parameter of out
     *
     * @return String
     */
    public static <T> String objectToJson(final T object) {

        final ObjectMapper objectMapper = getInstance();
        try {
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            System.out.println("Error [objectToJson]: " + ex.getMessage());
            return (String) object;
        }
    }
}
