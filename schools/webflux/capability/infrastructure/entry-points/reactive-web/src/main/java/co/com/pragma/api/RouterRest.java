package co.com.pragma.api;

import co.com.pragma.api.handler.CapabilityListHandler;
import co.com.pragma.api.handler.CapabilityHandler;
import co.com.pragma.validator.dto.request.CapabilityIdsRequestDto;
import co.com.pragma.validator.dto.request.CapabilityRequestDto;
import co.com.pragma.validator.dto.request.query.QueryDto;
import co.com.pragma.validator.dto.respose.CapabilityResponseDto;
import co.com.pragma.validator.dto.respose.CapabilityStatusResponseDto;
import co.com.pragma.validator.dto.respose.capabilities.CapabilityResultsPageDto;
import io.swagger.v3.oas.annotations.Operation;
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
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "capabilities", description = "API para gestión de Capacidades")
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/capabilities",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = CapabilityHandler.class,
                    beanMethod = "capabilityRegister",
                    operation = @Operation(
                            operationId = "registerCapability",
                            summary = "Registrar una nueva Capacidad",
                            description = "Crea y registra una nueva Capacidad en el sistema. " +
                                    "Una capacidad debe tener un nombre, una descripción y al menos 2 tecnologías asociadas " +
                                    "sin duplicados (máximo 20 tecnologías).",
                            tags = {"capabilities"},
                            requestBody = @RequestBody(
                                    description = "Datos de la Capacidad a registrar. Todos los campos son obligatorios. " +
                                            "El campo 'technologyIds' debe contener entre 2 y 20 IDs de tecnologías únicos.",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CapabilityRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Capacidad registrada exitosamente. Retorna la información de la capacidad creada con su ID asignado.",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = CapabilityResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos de entrada. " +
                                                    "Puede ser por: campos requeridos faltantes, valores fuera de rango, " +
                                                    "tecnologías duplicadas o reglas de negocio violadas."
                                    ),
                                    @ApiResponse(
                                            responseCode = "412",
                                            description = "Error de precondición. La capacidad ya existe en el sistema " +
                                                    "(violación de restricción única)."
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor. Error no esperado durante el procesamiento."
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(CapabilityHandler capabilityHandler) {
        return route(POST("/api/v1/capabilities"), capabilityHandler::capabilityRegister);
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/capabilities",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = CapabilityListHandler.class,
                    beanMethod = "getAllByPageable",
                    operation = @Operation(
                            operationId = "getAllByPageable",
                            summary = "Obtener lista paginada de capacidades",
                            description = "Permite consultar capacidades con paginación y filtros opcionales. " +
                                    "El tamaño máximo de página es 10 elementos. " +
                                    "Requiere rol ADMIN.",
                            tags = {"Capabilities"},
                            requestBody = @RequestBody(
                                    description = "Parámetros de paginación y filtros",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = QueryDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Consulta exitosa",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = CapabilityResultsPageDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los parámetros de entrada"
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "No autorizado"
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "No tiene permisos para acceder al recurso"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/capabilities/items",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = CapabilityListHandler.class,
                    beanMethod = "capabilitySearch",
                    operation = @Operation(
                            operationId = "searchCapability",
                            summary = "Buscar lista por ids de la Capacidad",
                            description = "Crea y registra una nueva Capacidad en el sistema. " +
                                    "Una capacidad debe tener un nombre, una descripción y al menos 2 tecnologías asociadas " +
                                    "sin duplicados (máximo 20 tecnologías).",
                            tags = {"capabilities"},
                            requestBody = @RequestBody(
                                    description = "Datos de la Capacidad a registrar. Todos los campos son obligatorios. " +
                                            "El campo 'technologyIds' debe contener entre 2 y 20 IDs de tecnologías únicos.",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CapabilityIdsRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Capacidad registrada exitosamente. Retorna la información de la capacidad creada con su ID asignado.",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = CapabilityStatusResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos de entrada. " +
                                                    "Puede ser por: campos requeridos faltantes, valores fuera de rango, " +
                                                    "tecnologías duplicadas o reglas de negocio violadas."
                                    ),
                                    @ApiResponse(
                                            responseCode = "412",
                                            description = "Error de precondición. La capacidad ya existe en el sistema " +
                                                    "(violación de restricción única)."
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor. Error no esperado durante el procesamiento."
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionGets(CapabilityListHandler capabilityListHandler) {
        return route(GET("/api/v1/capabilities"), capabilityListHandler::getAllByPageable)
                .andRoute(POST("/api/v1/capabilities/items"), capabilityListHandler::getListCapabilitiesIds)
                .andRoute(POST("/api/v1/capabilities/technologies/items"), capabilityListHandler::getCapabilitiesByIds);
    }

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/capabilities/{id}",
                    method = org.springframework.web.bind.annotation.RequestMethod.DELETE,
                    beanClass = CapabilityHandler.class,
                    beanMethod = "deleteCapability",
                    operation = @Operation(
                            operationId = "deleteCapability",
                            summary = "Eliminar una Capacidad",
                            description = "Elimina una capacidad del sistema. También elimina todas sus relaciones con tecnologías.",
                            tags = {"capabilities"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Capacidad eliminada exitosamente"
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
            )
    })
    public RouterFunction<ServerResponse> routerFunctionCapabilityOperations(CapabilityHandler capabilityHandler) {
        return route(DELETE("/api/v1/capabilities/{id}"), capabilityHandler::deleteCapability);
    }
}
