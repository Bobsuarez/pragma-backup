package co.com.pragma.api.router;

import co.com.pragma.api.handler.BootcampHandler;
import co.com.pragma.validator.dto.request.BootcampRequestDto;
import co.com.pragma.validator.dto.response.BootcampResponseDto;
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
@Tag(name = "Bootcamps", description = "API para la gestión de bootcamps")
public class BootcampRouter {
    
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/bootcamps",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = BootcampHandler.class,
                    beanMethod = "createBootcamp",
                    operation = @Operation(
                            operationId = "createBootcamp",
                            summary = "Crear un nuevo bootcamp",
                            description = "Crea un nuevo bootcamp y automáticamente genera un reporte con las métricas asociadas",
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
                                            schema = @Schema(type = "string", example = "123e4567-e89b-12d3-a456-426614174000")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Bootcamp creado exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = BootcampResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación - Datos inválidos en el request"
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno del servidor"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return route()
                .path("/api/v1/bootcamp-reports", builder -> builder
                        .POST("",
                                accept(MediaType.APPLICATION_JSON)
                                        .and(contentType(MediaType.APPLICATION_JSON)),
                                handler::createBootcamp)
                )
                .build();
    }
}

