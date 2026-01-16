package co.com.pragma.r2dbc.providers;

import org.springframework.stereotype.Component;

@Component
public class BootcampSQLProvider {

    /**
     * Query SQL para insertar relación bootcamp-capability
     * @return SQL INSERT statement
     */
    public String insertBootcampCapabilityRelationship() {
        return """
                INSERT INTO bootcamp_capability (bootcamp_id, capability_id)
                VALUES (:bootcamp_id, :capability_id)
                """;
    }

    /**
     * Query SQL para eliminar relaciones de un bootcamp
     * @return SQL DELETE statement
     */
    public String deleteBootcampCapabilityRelationships() {
        return """
                DELETE FROM bootcamp_capability
                WHERE bootcamp_id = :bootcamp_id
                """;
    }

    /**
     * Query SQL para obtener capacidades de un bootcamp
     * @return SQL SELECT statement
     */
    public String findCapabilitiesByBootcampId() {
        return """
                SELECT c.id, c.name, c.description
                FROM capability c
                INNER JOIN bootcamp_capability bc ON c.id = bc.capability_id
                WHERE bc.bootcamp_id = :bootcamp_id
                """;
    }

    /**
     * Query SQL para obtener IDs de capacidades de un bootcamp
     * @return SQL SELECT statement
     */
    public String findCapabilityIdsByBootcampId() {
        return """
                SELECT capability_id
                FROM bootcamp_capability
                WHERE bootcamp_id = :bootcamp_id
                """;
    }

    /**
     * Query SQL para contar cuántos bootcamps (excluyendo el especificado) usan una capacidad
     * @return SQL SELECT statement
     */
    public String countBootcampsByCapabilityId() {
        return """
                SELECT COUNT(*) as count
                FROM bootcamp_capability
                WHERE capability_id = :capability_id
                AND bootcamp_id != :bootcamp_id
                """;
    }
}


