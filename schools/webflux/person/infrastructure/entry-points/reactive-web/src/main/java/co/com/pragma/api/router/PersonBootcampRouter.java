package co.com.pragma.api.router;

import co.com.pragma.api.handler.PersonBootcampHandler;
import co.com.pragma.validator.dto.request.PersonBootcampRequestDto;
import co.com.pragma.validator.dto.response.PersonBootcampCountResponseDto;
import co.com.pragma.validator.dto.response.PersonBootcampResponseDto;
import co.com.pragma.validator.dto.response.PersonResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Person Bootcamp", description = "API para gestionar inscripciones de personas en bootcamps")
public class PersonBootcampRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/person-bootcamp",
                    operation = @Operation(
                            operationId = "registerBootcamp",
                            summary = "Inscribir persona en bootcamp",
                            description = "Permite inscribir una persona en un bootcamp. La información del bootcamp se obtiene de una API externa (/api/v1/bootcamp/{id}). Valida que no exceda el límite de 5 bootcamps simultáneos y que no haya solapamiento de fechas y duración.",
                            tags = {"Person Bootcamp"},
                            requestBody = @RequestBody(
                                    description = "Datos de una incripción a un bootcamp",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = PersonBootcampRequestDto.class)
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
                                            description = "Inscripción exitosa",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = PersonBootcampResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación - Datos de entrada inválidos",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/person-bootcamp/{id-person}",
                    operation = @Operation(
                            operationId = "countPersonBootcamps",
                            summary = "Contar bootcamps de una persona",
                            description = "Obtiene el número de bootcamps en los que está inscrita una persona.",
                            tags = {"Person Bootcamp"},
                            parameters = {
                                    @Parameter(
                                            name = "id-person",
                                            description = "ID de la persona",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            schema = @Schema(type = "integer", example = "1")
                                    ),
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
                                            description = "Conteo exitoso",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = PersonBootcampCountResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación - La persona no existe",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Persona no encontrada",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/person-bootcamp/{id}/people",
                    operation = @Operation(
                            operationId = "getPeopleByBootcamp",
                            summary = "Obtener personas inscritas en un bootcamp",
                            description = "Obtiene las personas inscritas en un bootcamp específico. La información del bootcamp se valida contra una API externa.",
                            tags = {"Person Bootcamp"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "ID del bootcamp",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            schema = @Schema(type = "integer", example = "1")
                                    ),
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
                                            description = "Consulta exitosa",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = PersonResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación - El bootcamp no existe",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Bootcamp no encontrado",
                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> personBootcampRoutes(PersonBootcampHandler handler) {
        return route()
                .path("/api/v1/person-bootcamp", builder -> builder
                        .POST("",
                                accept(MediaType.APPLICATION_JSON)
                                        .and(contentType(MediaType.APPLICATION_JSON)),
                                handler::enrollPersonInBootcamp)
                        .GET("/{id-person}",
                                accept(MediaType.APPLICATION_JSON),
                                handler::countPersonBootcamps)
                        .GET("/{id}/people",
                                accept(MediaType.APPLICATION_JSON),
                                handler::getPeopleByBootcamp)
                )
                .build();
    }
}

