package co.com.pragma.api;

import co.com.pragma.api.handler.TechnologyHandler;
import co.com.pragma.validator.dto.respose.TechnologyDto;
import co.com.pragma.validator.dto.respose.TechnologyUsageCountResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "technologies", description = "API para gestión de Tecnologías")
public class TechnologyRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/technologies/by-capability/{capabilityId}",
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "getTechnologiesByCapability",
                    operation = @Operation(
                            operationId = "getTechnologiesByCapability",
                            summary = "Obtener tecnologías por Capacidad",
                            description = "Retorna la lista de tecnologías asociadas a una capacidad específica. " +
                                    "Primero consulta la base de datos local para obtener los IDs de tecnologías, " +
                                    "luego consulta la API externa para obtener los detalles completos de las tecnologías.",
                            tags = {"technologies"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de tecnologías encontradas",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = TechnologyDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Capacidad no encontrada"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/technologies/{id}/usage-count",
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = TechnologyHandler.class,
                    beanMethod = "getTechnologyUsageCount",
                    operation = @Operation(
                            operationId = "getTechnologyUsageCount",
                            summary = "Obtener conteo de uso de una Tecnología",
                            description = "Retorna el número de veces que una tecnología está siendo utilizada (en capacidades).",
                            tags = {"technologies"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Conteo de uso de la tecnología",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = TechnologyUsageCountResponseDto.class)
                                            )
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
    public RouterFunction<ServerResponse> technologyRoutes(TechnologyHandler technologyHandler) {
        return route(GET("/api/v1/technologies/by-capability/{capabilityId}"), technologyHandler::getTechnologiesByCapability)
                .andRoute(GET("/api/v1/technologies/{id}/usage-count"), technologyHandler::getTechnologyUsageCount);
    }
}
