package co.com.pragma.r2dbc.providers;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CapabilitySQLProvider {

    private final static String FILTER_NAME = " c.name ILIKE ':CAPABILITY_NAME%' ";
    private final static String FILTER_COUNT = " data.cantidad = :TECH_COUNT ";
    private final static String SELECT_COUNT = " COUNT(*)";
    private final static String FILTER_DATA = " t.id,\n \t t.name ";

    public String findByNameILikeAndCountRowSql(
            String capabilityName,
            Integer techCount,
            boolean hasCount
    ) {
        return """
                SELECT
                """ + builderSelect(hasCount) + """
                FROM
                	capability c
                INNER JOIN capability_technology ct ON
                	c.id = ct.capability_id
                INNER JOIN technologies t ON
                	ct.technology_id = t.id
                INNER JOIN (
                	SELECT
                		ct2.capability_id ,
                		count(*) AS cantidad
                	FROM
                		capability_technology ct2
                	GROUP BY
                		ct2.capability_id) AS DATA ON
                		ct.capability_id = data.capability_id \s
                """
                + builderFilter(capabilityName, techCount) +
                """
                """
                + builderLimit(!hasCount) +  """
                """;
    }

    private String builderLimit(boolean hasLimit) {
        if (hasLimit) {
            return """
                    ORDER BY t.name %s \s
                    LIMIT :LIMIT OFFSET :OFFSET\s
                    """;
        }
        return "";

    }

    private String builderSelect (boolean isCount) {
        if (isCount) {
            return SELECT_COUNT;
        } else {
            return FILTER_DATA;
        }
    }

    private String builderFilter(String capabilityName, Integer techCount) {

        List<String> conditions = Stream.of(
                        (capabilityName != null && !capabilityName.isEmpty()) ? FILTER_NAME : null,
                        (techCount != null) ? FILTER_COUNT : null
                )
                .filter(Objects::nonNull)
                .toList();

        if (conditions.isEmpty()) {
            return "";
        }

        return conditions.stream()
                .collect(Collectors.joining(" AND ", " WHERE ", " "));
    }
}
