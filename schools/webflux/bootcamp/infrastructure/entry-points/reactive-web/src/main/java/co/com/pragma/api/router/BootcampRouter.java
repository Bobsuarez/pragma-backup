package co.com.pragma.api.router;

import co.com.pragma.api.handler.BootcampHandler;
import co.com.pragma.api.handler.BootcampListHandler;
import co.com.pragma.validator.dto.request.BootcampRequestDto;
import co.com.pragma.validator.dto.response.BootcampPageResponseDto;
import co.com.pragma.validator.dto.response.BootcampResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Bootcamps", description = "API para gestión de bootcamps")
public class BootcampRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/bootcamps",
                    operation = @Operation(
                            operationId = "createBootcamp",
                            summary = "Crear un nuevo bootcamp",
                            description = "Registra un nuevo bootcamp en el sistema. Un bootcamp debe tener entre 1 y 4 capacidades asociadas.",
                            tags = {"Bootcamps"},
                            requestBody = @RequestBody(
                                    description = "Datos del bootcamp a crear",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BootcampRequestDto.class)
                                    )
                            ),
                            parameters = {
                                    @Parameter(
                                            name = "X-B3-TraceId",
                                            description = "ID de trazabilidad para el request (opcional)",
                                            in = ParameterIn.HEADER,
                                            required = false,
                                            schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Bootcamp creado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = BootcampResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación o regla de negocio",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return route(POST("/api/v1/bootcamps"), handler::createBootcamp);
    }

    @Bean
    @RouterOperation(
            operation = @Operation(
                    summary = "Listar bootcamps",
                    description =
                            "Obtiene una lista paginada de bootcamps con sus capacidades y tecnologías asociadas. " +
                                    "Permite ordenar por nombre o por cantidad de capacidades, en orden ascendente o descendente.",
                    operationId = "listBootcamps",
                    tags = {"Bootcamps"},
                    parameters = {
                            @Parameter(
                                    name = "page",
                                    description = "Número de página (inicia en 0)",
                                    in = ParameterIn.QUERY,
                                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
                            ),
                            @Parameter(
                                    name = "size",
                                    description = "Tamaño de la página",
                                    in = ParameterIn.QUERY,
                                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
                            ),
                            @Parameter(
                                    name = "sortField",
                                    description = "Campo por el cual ordenar",
                                    in = ParameterIn.QUERY,
                                    schema = @Schema(type = "string", allowableValues = {"NAME",
                                            "CAPABILITIES_COUNT"}, defaultValue = "NAME")
                            ),
                            @Parameter(
                                    name = "sortDirection",
                                    description = "Dirección del ordenamiento",
                                    in = ParameterIn.QUERY,
                                    schema = @Schema(type = "string", allowableValues = {"ASC",
                                            "DESC"}, defaultValue = "ASC")
                            ),
                            @Parameter(
                                    name = "X-B3-TraceId",
                                    description = "ID de trazabilidad (opcional)",
                                    in = ParameterIn.HEADER,
                                    schema = @Schema(type = "string"),
                                    required = false
                            )
                    },
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Lista de bootcamps obtenida exitosamente",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BootcampPageResponseDto.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Parámetros de consulta inválidos"
                            ),
                            @ApiResponse(
                                    responseCode = "500",
                                    description = "Error interno del servidor"
                            )
                    }
            ),
            beanClass = BootcampListHandler.class,
            beanMethod = "bootcampRouteISearch",
            path = "/api/v1/bootcamps/items"
    )
    public RouterFunction<ServerResponse> bootcampRouteISearch(BootcampListHandler handler) {
        return route(GET("/api/v1/bootcamps/items"), handler::invoke);
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    operation = @Operation(
                            operationId = "getBootcamp",
                            summary = "Obtener bootcamp por ID",
                            description = "Obtiene un bootcamp por su ID con sus capacidades asociadas",
                            tags = {"Bootcamps"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID del bootcamp a obtener",
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    ),
                                    @Parameter(
                                            name = "X-B3-TraceId",
                                            in = ParameterIn.HEADER,
                                            description = "ID de trazabilidad para el request",
                                            schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Bootcamp obtenido exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = BootcampResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "ID de bootcamp inválido",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Bootcamp no encontrado",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    ),
                    path = "/api/v1/bootcamps/{id}"
            ),
            @RouterOperation(
                    path = "/api/v1/bootcamps/{id}",
                    operation = @Operation(
                            operationId = "deleteBootcamp",
                            summary = "Eliminar bootcamp",
                            description = "Elimina un bootcamp y sus capacidades y tecnologías asociadas. " +
                                    "Las capacidades y tecnologías solo se eliminan si no están referenciadas por otros bootcamps.",

                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID del bootcamp a eliminar",
                                            schema = @Schema(type = "integer", format = "int64", example = "1")
                                    ),
                                    @Parameter(
                                            name = "X-B3-TraceId",
                                            in = ParameterIn.HEADER,
                                            description = "ID de trazabilidad para el request",
                                            schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Bootcamp eliminado exitosamente",
                                            content = @Content()
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "ID de bootcamp inválido",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = "{\"traceId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                                                                            "\"message\":\"Invalid bootcamp ID format\"," +
                                                                            "\"status\":\"error\"}"
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = "{\"traceId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                                                                            "\"message\":\"Error deleting bootcamp\"," +
                                                                            "\"status\":\"error\"}"
                                                            )
                                                    }
                                            )
                                    )
                            }
                    )
            )

    })
    public RouterFunction<ServerResponse> bootcampRouteGet(BootcampHandler handler) {
        return RouterFunctions.route(GET("/api/v1/bootcamps/{id}"), handler::getBootcamp)
                .andRoute(DELETE("/api/v1/bootcamps/{id}"), handler::deleteBootcamp);
    }

}

