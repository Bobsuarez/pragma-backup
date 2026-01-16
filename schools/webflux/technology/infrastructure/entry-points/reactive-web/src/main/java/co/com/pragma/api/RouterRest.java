package co.com.pragma.api;

import co.com.pragma.api.handler.Handler;
import co.com.pragma.validator.dto.request.TechnologyIdsRequestDto;
import co.com.pragma.validator.dto.request.TechnologyRequestDto;
import co.com.pragma.validator.dto.respose.TechnologyResponseDto;
import co.com.pragma.validator.dto.respose.TechnologySimpleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Technologies", description = "API para gestión de tecnologías")
public class RouterRest {
    
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/technologies",
            method = org.springframework.web.bind.annotation.RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "registerTechnology",
            operation = @Operation(
                operationId = "registerTechnology",
                summary = "Registrar una nueva tecnología",
                description = "Crea y registra una nueva tecnología en el sistema",
                tags = {"Technologies"},
                requestBody = @RequestBody(
                    description = "Datos de la tecnología a registrar",
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TechnologyRequestDto.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201",
                        description = "Tecnología registrada exitosamente",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TechnologyResponseDto.class)
                        )
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Error de validación en los datos de entrada"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor"
                    )
                }
            )
        ),
            @RouterOperation(
                    path = "/api/v1/technologies/items",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "getTechnologiesByIds",
                    operation = @Operation(
                            operationId = "getTechnologiesByIds",
                            summary = "Obtener tecnologías por IDs",
                            description = "Retorna una lista de tecnologías basada en los IDs proporcionados",
                            tags = {"Technologies"},
                            requestBody = @RequestBody(
                                    description = "Lista de IDs de tecnologías a buscar",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = TechnologyIdsRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de tecnologías encontradas",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    array = @ArraySchema(schema = @Schema(implementation = TechnologySimpleResponseDto.class))
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos de entrada"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            ),
        @RouterOperation(
            path = "/api/v1/technologies/{id}",
            method = org.springframework.web.bind.annotation.RequestMethod.DELETE,
            beanClass = Handler.class,
            beanMethod = "deleteTechnology",
            operation = @Operation(
                operationId = "deleteTechnology",
                summary = "Eliminar una tecnología",
                description = "Elimina una tecnología del sistema por su ID",
                tags = {"Technologies"},
                responses = {
                    @ApiResponse(
                        responseCode = "204",
                        description = "Tecnología eliminada exitosamente"
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "ID inválido"
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Tecnología no encontrada"
                    ),
                    @ApiResponse(
                        responseCode = "500",
                        description = "Error interno del servidor"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/technologies"), handler::registerTechnology)
                .andRoute(POST("/api/v1/technologies/items"), handler::getTechnologiesByIds)
                .andRoute(DELETE("/api/v1/technologies/{id}"), handler::deleteTechnology);
    }
}

