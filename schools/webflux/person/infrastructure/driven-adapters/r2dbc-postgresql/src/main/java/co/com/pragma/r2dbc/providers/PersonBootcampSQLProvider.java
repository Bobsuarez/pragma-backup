package co.com.pragma.r2dbc.providers;

import org.springframework.stereotype.Component;

@Component
public class PersonBootcampSQLProvider {

    /**
     * Query SQL para insertar inscripción de persona en bootcamp
     * @return SQL INSERT statement
     */
    public String insertPersonBootcamp() {
        return """
                INSERT INTO person_bootcamp (person_id, bootcamp_id)
                VALUES (:personId, :bootcampId)
                RETURNING id, person_id, bootcamp_id
                """;
    }

    /**
     * Query SQL para consultar inscripciones de una persona
     * @return SQL SELECT statement
     */
    public String findEnrollmentsByPersonId() {
        return """
                SELECT pb.id, pb.person_id, pb.bootcamp_id
                FROM person_bootcamp pb
                WHERE pb.person_id = :personId
                """;
    }

    /**
     * Query SQL para consultar bootcamps de una persona con sus detalles
     * @return SQL SELECT statement
     */
    public String findBootcampsByPersonId() {
        return """
                SELECT b.id, b.name, b.description, b.launch_date, b.duration_months
                FROM bootcamp b
                INNER JOIN person_bootcamp pb ON b.id = pb.bootcamp_id
                WHERE pb.person_id = :personId
                ORDER BY b.launch_date
                """;
    }

    /**
     * Query SQL para consultar un bootcamp por ID
     * @return SQL SELECT statement
     */
    public String findBootcampById() {
        return """
                SELECT id, name, description, launch_date, duration_months
                FROM bootcamp
                WHERE id = :bootcampId
                """;
    }

    /**
     * Query SQL para consultar una persona por ID
     * @return SQL SELECT statement
     */
    public String findPersonById() {
        return """
                SELECT id, name, email
                FROM person
                WHERE id = :personId
                """;
    }

    /**
     * Query SQL para contar inscripciones activas de una persona
     * @return SQL SELECT statement
     */
    public String countEnrollmentsByPersonId() {
        return """
                SELECT COUNT(*) as count
                FROM person_bootcamp
                WHERE person_id = :personId
                """;
    }

    /**
     * Query SQL para verificar si ya existe la inscripción
     * @return SQL SELECT statement
     */
    public String existsEnrollment() {
        return """
                SELECT COUNT(*) as count
                FROM person_bootcamp
                WHERE person_id = :personId AND bootcamp_id = :bootcampId
                """;
    }

    /**
     * Query SQL para consultar personas inscritas en un bootcamp
     * @return SQL SELECT statement
     */
    public String findPeopleByBootcampId() {
        return """
                SELECT p.id, p.name, p.email
                FROM person p
                INNER JOIN person_bootcamp pb ON p.id = pb.person_id
                WHERE pb.bootcamp_id = :bootcampId
                ORDER BY p.name
                """;
    }
}

