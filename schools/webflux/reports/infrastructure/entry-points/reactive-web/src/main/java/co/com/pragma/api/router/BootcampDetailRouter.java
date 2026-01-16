package co.com.pragma.api.router;

import co.com.pragma.api.handler.BootcampDetailHandler;
import co.com.pragma.validator.dto.response.BootcampDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Bootcamp Details", description = "API para obtener información detallada de bootcamps")
public class BootcampDetailRouter {
    
    @Bean
    @RouterOperation(
            path = "/api/v1/bootcamps/{bootcampId}/detail",
            method = org.springframework.web.bind.annotation.RequestMethod.GET,
            beanClass = BootcampDetailHandler.class,
            beanMethod = "getBootcampDetail",
            operation = @Operation(
                    operationId = "getBootcampDetail",
                    summary = "Obtener información detallada de un bootcamp",
                    description = "Retorna toda la información del bootcamp incluyendo capabilities, tecnologías y personas inscritas con nombre y correo",
                    tags = {"Bootcamp Details"},
                    parameters = {
                            @Parameter(
                                    name = "bootcampId",
                                    description = "ID del bootcamp",
                                    required = true,
                                    in = ParameterIn.PATH,
                                    schema = @Schema(type = "integer", format = "int64", example = "1")
                            ),
                            @Parameter(
                                    name = "Authorization",
                                    description = "Token de autorización (Bearer token)",
                                    in = ParameterIn.HEADER,
                                    schema = @Schema(type = "string", example = "Bearer token123")
                            ),
                            @Parameter(
                                    name = "X-B3-TraceId",
                                    description = "ID de trazabilidad para el request (opcional)",
                                    in = ParameterIn.HEADER,
                                    schema = @Schema(type = "string", example = "123e4567-e89b-12d3-a456-426614174000")
                            )
                    },
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Información del bootcamp obtenida exitosamente",
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BootcampDetailResponseDto.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "404",
                                    description = "Bootcamp no encontrado"
                            ),
                            @ApiResponse(
                                    responseCode = "500",
                                    description = "Error interno del servidor"
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> bootcampDetailRoutes(BootcampDetailHandler handler) {
        return route()
                .path("/api/v1/bootcamps", builder -> builder
                        .GET("/{bootcampId}/detail",
                                accept(MediaType.APPLICATION_JSON),
                                handler::getBootcampDetail)
                )
                .build();
    }
}
