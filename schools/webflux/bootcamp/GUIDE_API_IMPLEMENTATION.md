# Guía Genérica de Implementación de APIs - WebFlux + Clean Architecture

Esta guía proporciona un patrón reutilizable para implementar APIs REST reactivas siguiendo Clean Architecture y Spring WebFlux.

## 📋 Tabla de Contenidos

1. [Estructura de Carpetas](#estructura-de-carpetas)
2. [Flujo de Implementación](#flujo-de-implementación)
3. [Componentes por Capa](#componentes-por-capa)
4. [Convenciones y Buenas Prácticas](#convenciones-y-buenas-prácticas)
5. [Ejemplo Completo](#ejemplo-completo)

---

## 📁 Estructura de Carpetas

### Ubicación de Componentes

```
project-root/
├── domain/
│   ├── model/
│   │   ├── [Modelo].java                    # Modelos de dominio
│   │   └── gateway/
│   │       └── [Modelo]Repository.java      # Puertos/Interfaces
│   └── usecase/
│       └── [Accion][Modelo]UseCase.java     # Casos de uso
│
├── infrastructure/
│   ├── helpers/
│   │   └── validator/
│   │       ├── dto/
│   │       │   ├── request/                 # ⚠️ DTOs de REQUEST aquí
│   │       │   │   └── [Modelo]RequestDto.java
│   │       │   └── response/                # ⚠️ DTOs de RESPONSE aquí
│   │       │       └── [Modelo]ResponseDto.java
│   │       └── mappers/
│   │           └── [Modelo]Mapper.java      # Mappers DTO ↔ Dominio
│   │
│   ├── driven-adapters/
│   │   ├── r2dbc-postgresql/
│   │   │   ├── entity/
│   │   │   │   └── [Modelo]Entity.java
│   │   │   ├── mappers/                      # ⚠️ Mappers Dominio ↔ Entity aquí
│   │   │   │   └── [Modelo]EntityMapper.java
│   │   │   ├── repositories/
│   │   │   │   └── [Modelo]R2dbcRepository.java  # ✅ Extiende ReactiveCrudRepository
│   │   │   └── adapters/
│   │   │       └── [Modelo]R2dbcAdapter.java     # ✅ Usa ORM directamente para CRUD
│   │   └── rest-consumer/                   # ⚠️ Módulo para consumir APIs externas
│   │       ├── adapter/
│   │       │   └── [Modelo]Adapter.java      # ✅ Implementa puerto del dominio
│   │       ├── config/
│   │       │   └── [Modelo]ConsumerConfig.java  # Configuración de WebClient
│   │       ├── dto/
│   │       │   └── [Modelo]ApiResponse.java  # DTOs de respuesta de API externa
│   │       └── util/
│   │           └── RestUtil.java            # Utilidades para construir WebClient
│   │
│   └── entry-points/
│       └── reactive-web/
│           ├── api/
│           │   ├── handler/
│           │   │   └── [Modelo]Handler.java
│           │   └── router/
│           │       └── [Modelo]Router.java
│
└── applications/
    └── app-service/
        └── config/
            └── UseCasesConfig.java           # Auto-escaneo de UseCases
```

### ⚠️ Ubicaciones Críticas

- **DTOs**: `infrastructure/helpers/validator/src/main/java/co/com/pragma/validator/dto/`
  - Request: `dto/request/[Modelo]RequestDto.java`
  - Response: `dto/response/[Modelo]ResponseDto.java`

- **Mappers Dominio ↔ Entity**: `infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/pragma/r2dbc/mappers/`
  - Archivo: `[Modelo]EntityMapper.java`

- **Mappers DTO ↔ Dominio**: `infrastructure/helpers/validator/src/main/java/co/com/pragma/validator/mappers/`
  - Archivo: `[Modelo]Mapper.java`

- **REST Consumer (APIs Externas)**: `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/`
  - Adapter: `adapter/[Modelo]Adapter.java` - Implementa el puerto del dominio
  - Config: `config/[Modelo]ConsumerConfig.java` - Configuración de WebClient
  - DTOs API Externa: `dto/[Modelo]ApiResponse.java` - DTOs para respuestas de API externa
  - Utilidades: `util/RestUtil.java` - Helper para construir WebClient

---

## 🔄 Flujo de Implementación

### Paso 1: Modelo de Dominio

**Ubicación**: `domain/model/src/main/java/co/com/pragma/model/`

```java
@Getter
@Builder(toBuilder = true)
public class [Modelo] {
    private Long id;
    private String name;
    // ... otros campos
}
```

### Paso 2: Puerto/Interfaz del Repositorio

**Ubicación**: `domain/model/src/main/java/co/com/pragma/model/gateway/`

```java
public interface [Modelo]Repository {
    Mono<[Modelo]> save([Modelo] modelo);
    Mono<[Modelo]> findById(Long id);
    // ... otros métodos
}
```

### Paso 3: DTOs

**⚠️ IMPORTANTE**: Los DTOs deben ir en:
- Request: `infrastructure/helpers/validator/src/main/java/co/com/pragma/validator/dto/request/`
- Response: `infrastructure/helpers/validator/src/main/java/co/com/pragma/validator/dto/response/`

**Request DTO**:
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class [Modelo]RequestDto {
    @NotBlank(message = "El campo es requerido")
    @Size(min = 1, max = 255)
    private String name;
    // ... otros campos con validaciones Jakarta
}
```

**Response DTO**:
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class [Modelo]ResponseDto {
    private Long id;
    private String name;
    // ... otros campos
}
```

### Paso 4: Mapper DTO ↔ Dominio

**Ubicación**: `infrastructure/helpers/validator/src/main/java/co/com/pragma/validator/mappers/`

```java
@Mapper(componentModel = "spring")
public interface [Modelo]Mapper {
    @Mapping(target = "id", ignore = true)
    [Modelo] toDomain([Modelo]RequestDto dto);
    
    [Modelo]ResponseDto toResponseDto([Modelo] modelo);
}
```

### Paso 5: Mapper Dominio ↔ Entity

**⚠️ IMPORTANTE**: Este mapper debe ir en:
`infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/pragma/r2dbc/mappers/`

```java
@Mapper(componentModel = "spring")
public interface [Modelo]EntityMapper {
    /**
     * Convierte modelo de dominio a entidad
     */
    @Mapping(target = "campoFecha", expression = "java(formatLocalDateToString(modelo.getCampoFecha()))")
    [Modelo]Entity toEntity([Modelo] modelo);
    
    /**
     * Convierte entidad a modelo de dominio
     */
    default [Modelo] toDomain([Modelo]Entity entity, List<[Relacion]Entity> relaciones) {
        if (entity == null) {
            return null;
        }
        // Implementación del mapeo
        return [Modelo].builder()
                .id(entity.getId())
                .name(entity.getName())
                // ... otros campos
                .build();
    }
    
    // Métodos helper para conversiones de fechas, etc.
    default String formatLocalDateToString(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
}
```

### Paso 6: Entidad R2DBC

**Ubicación**: `infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/pragma/r2dbc/entity/`

```java
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("[tabla]")
public class [Modelo]Entity {
    @Id
    private Long id;
    
    @Column("nombre_columna")
    private String name;
    // ... otros campos
}
```

### Paso 7: Repositorio R2DBC

**Ubicación**: `infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/pragma/r2dbc/repositories/`

```java
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface [Modelo]R2dbcRepository extends ReactiveCrudRepository<[Modelo]Entity, Long> {
    // ✅ Usar métodos del ReactiveCrudRepository directamente:
    // - save(Entity) -> Mono<Entity>
    // - findById(Long) -> Mono<Entity>
    // - findAll() -> Flux<Entity>
    // - deleteById(Long) -> Mono<Void>
    // - existsById(Long) -> Mono<Boolean>
    // - count() -> Mono<Long>
    
    // Métodos personalizados con @Query si se requieren
    @Query("SELECT * FROM [tabla] WHERE campo = :valor")
    Flux<[Modelo]Entity> findByCampo(String valor);
    
    @Query("SELECT * FROM [tabla] WHERE campo LIKE :pattern")
    Flux<[Modelo]Entity> findByCampoContaining(String pattern);
}
```

### Paso 8: Adaptador R2DBC

**Ubicación**: `infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/pragma/r2dbc/adapters/`

```java
@Repository
@Slf4j
@RequiredArgsConstructor
public class [Modelo]R2dbcAdapter implements [Modelo]Repository {
    
    private final [Modelo]R2dbcRepository repository;
    private final [Relacion]R2dbcRepository relacionRepository;  // Para relaciones
    private final [Modelo]EntityMapper entityMapper;
    private final DatabaseClient databaseClient;  // Solo para consultas complejas (joins, queries personalizados)
    
    @Override
    @Transactional
    public Mono<[Modelo]> save([Modelo] modelo) {
        // Convertir dominio a entidad usando mapper
        [Modelo]Entity entity = entityMapper.toEntity(modelo);
        
        // ✅ Usar ORM directamente para operaciones CRUD
        return repository.save(entity)
                .flatMap(saved -> {
                    // Guardar relaciones usando ORM directamente
                    if (modelo.getRelaciones() != null && !modelo.getRelaciones().isEmpty()) {
                        return Flux.fromIterable(modelo.getRelaciones())
                                .map(relacion -> {
                                    [Relacion]Entity relacionEntity = [Relacion]Entity.builder()
                                            .[modelo]Id(saved.getId())
                                            .relacionId(relacion.getId())
                                            .build();
                                    return relacionEntity;
                                })
                                .flatMap(relacionRepository::save)
                                .collectList()
                                .thenReturn(saved);
                    }
                    return Mono.just(saved);
                })
                .map(saved -> entityMapper.toDomain(saved, modelo.getRelaciones()));
    }
    
    @Override
    public Mono<[Modelo]> findById(Long id) {
        // ✅ Usar ORM directamente para consultas simples
        return repository.findById(id)
                .map(entity -> entityMapper.toDomain(entity, List.of()))
                .switchIfEmpty(Mono.error(new RuntimeException("Modelo no encontrado con id: " + id)));
    }
    
    @Override
    public Mono<Void> deleteById(Long id) {
        // ✅ Usar ORM directamente para eliminaciones
        return repository.deleteById(id)
                .then();
    }
    
    @Override
    public Mono<[Modelo]> update([Modelo] modelo) {
        // ✅ Usar ORM directamente para actualizaciones
        [Modelo]Entity entity = entityMapper.toEntity(modelo);
        return repository.save(entity)
                .map(saved -> entityMapper.toDomain(saved, modelo.getRelaciones()));
    }
    
    // ⚠️ DatabaseClient solo para consultas complejas (joins, queries personalizados, agregaciones)
    public Flux<[Modelo]> findWithComplexQuery(String filter) {
        String sql = """
                SELECT m.*, r.*
                FROM [tabla] m
                LEFT JOIN [tabla_relacion] r ON m.id = r.[modelo]_id
                WHERE m.campo LIKE :filter
                """;
        return databaseClient.sql(sql)
                .bind("filter", "%" + filter + "%")
                .map((row, metadata) -> {
                    // Mapeo manual para consultas complejas
                    return entityMapper.toDomain(...);
                })
                .all();
    }
}
```

### Paso 8.1: Puerto para API Externa (Opcional - Solo si se requiere consumir API externa)

**Ubicación**: `domain/model/src/main/java/co/com/pragma/model/gateway/`

Si necesitas consumir una API externa, primero define el puerto en el dominio:

```java
public interface [Modelo]Repository {
    Mono<[Modelo]Response> findById(Long id);
    Flux<[Modelo]List> findAll([Modelo]Request request);
    // ... otros métodos
}
```

### Paso 8.2: DTOs de API Externa

**Ubicación**: `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/dto/`

Crea DTOs para mapear las respuestas de la API externa:

```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)  // ⚠️ Importante para ignorar campos desconocidos
public class [Modelo]ApiResponse {
    private Long id;
    private String name;
    // ... otros campos según la respuesta de la API externa
}
```

**DTO genérico para errores**:
```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private Integer code;
    private String message;
    private Object error;
}
```

### Paso 8.3: Configuración de WebClient

**Ubicación**: `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/config/`

```java
@Configuration
public class [Modelo]ConsumerConfig {

    @Bean(name = "[modelo]WebClient")
    public WebClient [modelo]WebClient(
            @Value("${adapter.restconsumer.[modelo].host}") String host
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        return RestUtil.buildWebClient(
                host,
                headers,
                10000,  // connectionTimeout (ms)
                10000,  // readTimeout (ms)
                5000    // writeTimeout (ms)
        );
    }
}
```

**Configuración en `application.yaml`**:
```yaml
adapter:
  restconsumer:
    [modelo]:
      host: https://api.externa.com
```

### Paso 8.4: Adaptador REST Consumer

**Ubicación**: `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/adapter/`

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class [Modelo]Adapter implements [Modelo]Repository {

    private final WebClient webClient;  // Inyectado con @Qualifier("[modelo]WebClient")

    public [Modelo]Adapter(@Qualifier("[modelo]WebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<[Modelo]Response> findById(Long id) {
        log.info("Requesting [modelo] from external service, id={}", id);

        // ✅ Usar deferContextual para acceder al contexto reactivo (tokens, traceId, etc.)
        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");  // Recuperar token del contexto
            String traceId = ctx.getOrDefault("traceId", "");  // Recuperar traceId del contexto
            
            log.debug("Calling external service, endpoint=/api/v1/[modelos]/{}, traceId={}", id, traceId);

            return webClient.get()
                    .uri("/api/v1/[modelos]/{id}", id)
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .exchangeToMono(this::handleResponse)
                    .doOnNext(response -> 
                        log.debug("Received [modelo] from external service, id={}, traceId={}", 
                            response.getId(), traceId))
                    .doOnError(error -> 
                        log.error("Error retrieving [modelo] from external service, id={}, traceId={}", 
                            id, traceId, error));
        });
    }

    @Override
    public Flux<[Modelo]List> findAll([Modelo]Request request) {
        log.info("Requesting [modelos] from external service, request={}", 
            ObjectMapperSingletonUtil.objectToJson(request));

        return Flux.deferContextual(ctx -> {
            String token = ctx.getOrDefault("token", "");
            String traceId = ctx.getOrDefault("traceId", "");

            return webClient.post()
                    .uri("/api/v1/[modelos]/search")
                    .header("Authorization", token)
                    .header("X-B3-TraceId", traceId)
                    .bodyValue(request)
                    .exchangeToFlux(this::handleResponseFlux)
                    .doOnNext(item -> 
                        log.debug("Received [modelo] item, id={}, traceId={}", 
                            item.getId(), traceId))
                    .doOnError(error -> 
                        log.error("Error retrieving [modelos] from external service, traceId={}", 
                            traceId, error));
        });
    }

    /**
     * Maneja la respuesta HTTP para Mono
     */
    private Mono<[Modelo]Response> handleResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ApiResponse.class)
                    .flatMap(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        // Mapear error HTTP a excepción de dominio
                        return Mono.error(
                                new BusinessException(body.getMessage(), body)
                        );
                    });
        }

        return clientResponse.bodyToMono([Modelo]Response.class);
    }

    /**
     * Maneja la respuesta HTTP para Flux
     */
    private Flux<[Modelo]List> handleResponseFlux(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ApiResponse.class)
                    .flatMapMany(body -> {
                        log.error(
                                "WebClient error: status={}, headers={}, body={}",
                                clientResponse.statusCode(),
                                clientResponse.headers().asHttpHeaders(),
                                body
                        );
                        return Mono.error(
                                new BusinessException(body.getMessage(), body)
                        );
                    });
        }

        return clientResponse.bodyToFlux([Modelo]List.class);
    }
}
```

**⚠️ IMPORTANTE - Uso del contexto reactivo en UseCases**:

Para pasar información al contexto (como tokens o traceId), usa `Mono.deferContextual` o `contextWrite` en el UseCase:

```java
@RequiredArgsConstructor
@Log
public class [Accion][Modelo]UseCase {
    
    private final [Modelo]Repository externalRepository;
    
    public Mono<[Modelo]> execute([Modelo] modelo, String traceId, String token) {
        return externalRepository.findById(modelo.getId())
                .contextWrite(Context.of("token", token, "traceId", traceId))
                .doOnSuccess(saved -> log.info("Success, traceId: " + traceId))
                .doOnError(error -> log.error("Error, traceId: " + traceId, error));
    }
}
```

### Paso 8.5: Utilidad RestUtil (Ya existe en el proyecto)

**Ubicación**: `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/util/RestUtil.java`

Esta clase ya existe en el proyecto y proporciona métodos para construir `WebClient` con configuración de timeouts:

```java
@UtilityClass
@Slf4j
public class RestUtil {

    public WebClient buildWebClient(
            String host, HttpHeaders headers, 
            int connectionTimeout, int readTimeout, int writeTimeout) {
        
        return WebClient.builder()
                .baseUrl(host)
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
                .clientConnector(getClientHttpConnector(connectionTimeout, readTimeout, writeTimeout))
                .build();
    }

    public ClientHttpConnector getClientHttpConnector(
            int connectionTimeout, int readTimeout, int writeTimeout) {
        
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .compress(true)
                        .keepAlive(true)
                        .option(CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                        .doOnConnected(connection -> {
                            connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, MILLISECONDS));
                            connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, MILLISECONDS));
                        }));
    }

    public BusinessException mapWebClientException(WebClientResponseException ex, String traceId) {
        log.error("WebClient error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new BusinessException("ERROR_NOT_FOUND");
        } else if (ex.getStatusCode().is4xxClientError()) {
            return new BusinessException("VALIDATION_ERROR");
        } else if (ex.getStatusCode().is5xxServerError()) {
            return new BusinessException("ERROR_INTERNAL");
        }
        
        return new BusinessException("ERROR_INTERNAL");
    }
}
```

**Referencia de implementación**: Ver ejemplo completo en:
- `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/adapter/CapabilityAdapter.java`
- `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/config/CapabilityConsumerConfig.java`

### Paso 9: Caso de Uso

**Ubicación**: `domain/usecase/src/main/java/co/com/pragma/usecase/`

```java
@RequiredArgsConstructor
@Log
public class [Accion][Modelo]UseCase {
    
    private final [Modelo]Repository repository;
    
    public Mono<[Modelo]> execute([Modelo] modelo, String traceId) {
        // Validaciones de negocio
        // Lógica de orquestación
        return repository.save(modelo)
                .doOnSuccess(saved -> log.info("Success, traceId: " + traceId))
                .doOnError(error -> log.info("Error, traceId: " + traceId));
    }
}
```

**Nota**: Los UseCases se auto-detectan mediante `@ComponentScan` en `UseCasesConfig.java` con el patrón `*UseCase`.

### Paso 10: Handler

**Ubicación**: `infrastructure/entry-points/reactive-web/src/main/java/co/com/pragma/api/handler/`

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class [Modelo]Handler {
    
    private final [Accion][Modelo]UseCase useCase;
    private final [Modelo]Mapper mapper;
    
    public Mono<ServerResponse> [accion][Modelo](ServerRequest request) {
        String traceId = extractTraceId(request);
        
        return request.bodyToMono([Modelo]RequestDto.class)
                .doOnNext(ValidatorEngine::validate)
                .map(mapper::toDomain)
                .flatMap(modelo -> useCase.execute(modelo, traceId))
                .map(mapper::toResponseDto)
                .flatMap(responseDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-B3-TraceId", traceId)
                        .bodyValue(buildSuccessResponse(responseDto, traceId)))
                .onErrorResume(this::handleError);
    }
    
    private String extractTraceId(ServerRequest request) {
        String traceId = request.headers().firstHeader("X-B3-TraceId");
        return traceId != null && !traceId.isEmpty() 
                ? traceId.replace("\"", "") 
                : UUID.randomUUID().toString();
    }
}
```

### Paso 11: Router

**Ubicación**: `infrastructure/entry-points/reactive-web/src/main/java/co/com/pragma/api/router/`

```java
@Configuration
public class [Modelo]Router {
    
    @Bean
    public RouterFunction<ServerResponse> [modelo]Routes([Modelo]Handler handler) {
        return route()
                .path("/api/v1/[modelos]", builder -> builder
                        .POST("",
                                accept(MediaType.APPLICATION_JSON)
                                        .and(contentType(MediaType.APPLICATION_JSON)),
                                handler::[accion][Modelo])
                )
                .build();
    }
}
```

---

## 🎯 Componentes por Capa

### Domain Layer (Capa de Dominio)

- **Modelos**: Entidades de negocio puras, sin dependencias externas
- **Gateways/Ports**: Interfaces que definen contratos (ej: `Repository`)
- **UseCases**: Orquestación de lógica de negocio

### Infrastructure Layer

#### Helpers/Validator
- **DTOs**: Objetos de transferencia de datos
  - ⚠️ **Request**: `dto/request/`
  - ⚠️ **Response**: `dto/response/`
- **Mappers DTO ↔ Dominio**: Conversión entre DTOs y modelos de dominio

#### Driven Adapters (R2DBC)
- **Entities**: Representación de tablas de BD
- **Mappers Dominio ↔ Entity**: ⚠️ En `r2dbc/mappers/`
- **Repositories**: Interfaces de Spring Data R2DBC (ReactiveCrudRepository)
- **Adapters**: Implementaciones de los puertos del dominio
  - ✅ **OBLIGATORIO**: Usar `ReactiveCrudRepository` directamente para operaciones CRUD (save, findById, delete, update)
  - ✅ **OPCIONAL**: Usar `DatabaseClient` solo para consultas complejas (joins, queries personalizados, agregaciones)
  - ❌ **PROHIBIDO**: Usar JPA ORM (EntityManager, JpaRepository, etc.)

#### Driven Adapters (REST Consumer)
- **Puertos/Interfaces**: Definidos en `domain/model/gateway/` (ej: `[Modelo]Repository`)
- **Adapters**: Implementaciones en `infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/pragma/consumer/adapter/`
- **Configuración**: Beans de `WebClient` en `config/`
- **DTOs de API Externa**: DTOs para mapear respuestas de APIs externas en `dto/`
- **Utilidades**: Clases helper para construir `WebClient` con timeouts y configuración en `util/`
- ✅ **OBLIGATORIO**: Usar `WebClient` de Spring WebFlux para llamadas HTTP reactivas
- ✅ **OBLIGATORIO**: Implementar el puerto del dominio definido en `domain/model/gateway/`
- ✅ **OBLIGATORIO**: Usar contexto reactivo (`deferContextual`) para pasar headers dinámicos (tokens, traceId, etc.)
- ✅ **OBLIGATORIO**: Manejar errores HTTP y mapearlos a excepciones de dominio

#### Entry Points (Reactive Web)
- **Handlers**: Manejo de requests HTTP reactivos
- **Routers**: Configuración de rutas REST

---

## ✅ Convenciones y Buenas Prácticas

### Nomenclatura

- **Modelos de Dominio**: Sustantivos (`Bootcamp`, `Capability`)
- **UseCases**: `[Accion][Modelo]UseCase` (`CreateBootcampUseCase`, `GetUserUseCase`)
- **Repositories (Puertos)**: `[Modelo]Repository`
- **Adapters R2DBC**: `[Modelo]R2dbcAdapter`
- **Adapters REST Consumer**: `[Modelo]Adapter` (en `rest-consumer/adapter/`)
- **Config REST Consumer**: `[Modelo]ConsumerConfig` (en `rest-consumer/config/`)
- **Handlers**: `[Modelo]Handler`
- **Routers**: `[Modelo]Router`
- **Mappers DTO**: `[Modelo]Mapper`
- **Mappers Entity**: `[Modelo]EntityMapper`
- **DTOs API Externa**: `[Modelo]ApiResponse` (en `rest-consumer/dto/`)

### Validaciones

- **DTOs**: Usar Jakarta Validation (`@NotNull`, `@NotBlank`, `@Size`, etc.)
- **Dominio**: Validaciones de reglas de negocio en UseCases
- **Validación centralizada**: Usar `ValidatorEngine.validate()`

### Programación Reactiva

- ✅ Usar `Mono<T>` para 0..1 elementos
- ✅ Usar `Flux<T>` para N elementos
- ❌ Nunca usar `block()`, `blockFirst()`, `blockLast()`
- ✅ Usar `flatMap` para operaciones asíncronas
- ✅ Usar `map` para transformaciones síncronas
- ✅ Manejar errores con `onErrorResume`, `onErrorMap`, `onErrorReturn`

### Trazabilidad

- Extraer `traceId` del header `X-B3-TraceId` o generar UUID
- Incluir `traceId` en todos los logs
- Incluir `traceId` en respuestas HTTP (header y body)

### Transacciones

- Usar `@Transactional` en métodos de adapters que modifican datos
- Asegurar atomicidad en operaciones que involucran múltiples tablas

### Acceso a Datos (R2DBC)

- ✅ **OBLIGATORIO**: Usar `ReactiveCrudRepository` directamente para operaciones CRUD básicas (save, findById, deleteById, findAll, etc.)
- ✅ **OPCIONAL**: Usar `DatabaseClient` solo para consultas complejas que no se pueden hacer con el ORM (joins complejos, queries personalizados, agregaciones)
- ❌ **PROHIBIDO**: Usar JPA ORM (EntityManager, JpaRepository, etc.)

### Consumo de APIs Externas (REST Consumer)

- ✅ **OBLIGATORIO**: Usar `WebClient` de Spring WebFlux para llamadas HTTP reactivas
- ✅ **OBLIGATORIO**: Implementar el puerto del dominio definido en `domain/model/gateway/`
- ✅ **OBLIGATORIO**: Usar `Mono.deferContextual` o `Flux.deferContextual` para acceder al contexto reactivo (tokens, traceId, etc.)
- ✅ **OBLIGATORIO**: Pasar contexto desde UseCases usando `contextWrite(Context.of(...))`
- ✅ **OBLIGATORIO**: Manejar errores HTTP y mapearlos a excepciones de dominio (`BusinessException`)
- ✅ **OBLIGATORIO**: Configurar timeouts apropiados (connection, read, write) en la configuración del WebClient
- ✅ **OBLIGATORIO**: Usar `@JsonIgnoreProperties(ignoreUnknown = true)` en DTOs de API externa para evitar errores por campos desconocidos
- ✅ **RECOMENDADO**: Usar `RestUtil.buildWebClient()` para construir WebClient con configuración estándar
- ✅ **RECOMENDADO**: Incluir `traceId` en headers de llamadas a API externa para trazabilidad
- ✅ **RECOMENDADO**: Logging detallado de requests y responses (con información sensible ofuscada)
- ❌ **PROHIBIDO**: Usar `block()`, `blockFirst()`, `blockLast()` en llamadas a API externa
- ❌ **PROHIBIDO**: Hacer llamadas HTTP bloqueantes (RestTemplate, HttpClient bloqueante, etc.)

**Operaciones CRUD - Usar ORM directamente:**
```java
// ✅ CORRECTO - Usar ReactiveCrudRepository para CRUD básico
@Repository
public interface [Modelo]R2dbcRepository extends ReactiveCrudRepository<[Modelo]Entity, Long> {
    // Métodos personalizados con @Query si se necesitan
    @Query("SELECT * FROM tabla WHERE campo = :valor")
    Flux<[Modelo]Entity> findByCampo(String valor);
}

// En el Adapter
@Override
public Mono<[Modelo]> save([Modelo] modelo) {
    [Modelo]Entity entity = entityMapper.toEntity(modelo);
    return repository.save(entity)  // ✅ Usar ORM directamente
            .map(entityMapper::toDomain);
}

@Override
public Mono<[Modelo]> findById(Long id) {
    return repository.findById(id)  // ✅ Usar ORM directamente
            .map(entityMapper::toDomain);
}

@Override
public Mono<Void> deleteById(Long id) {
    return repository.deleteById(id);  // ✅ Usar ORM directamente
}
```

**Consultas complejas - Usar DatabaseClient:**
```java
// ✅ CORRECTO - DatabaseClient solo para consultas complejas
public Flux<[Modelo]> findWithComplexJoin(Long id) {
    String sql = """
            SELECT m.*, r.*, t.*
            FROM [tabla] m
            LEFT JOIN [tabla_relacion] r ON m.id = r.[modelo]_id
            LEFT JOIN [tabla_tercera] t ON r.id = t.relacion_id
            WHERE m.id = :id
            """;
    return databaseClient.sql(sql)
            .bind("id", id)
            .map((row, metadata) -> {
                // Mapeo manual para resultados complejos
                return buildComplexModel(row);
            })
            .all();
}
```

---

## 📝 Checklist de Implementación

### Para APIs REST (Base de Datos)
- [ ] Crear modelo de dominio en `domain/model/`
- [ ] Crear puerto/interfaz en `domain/model/gateway/`
- [ ] Crear DTOs en `infrastructure/helpers/validator/dto/request/` y `response/`
- [ ] Crear mapper DTO ↔ Dominio en `infrastructure/helpers/validator/mappers/`
- [ ] Crear entidad R2DBC en `infrastructure/driven-adapters/r2dbc-postgresql/entity/`
- [ ] ⚠️ Crear mapper Dominio ↔ Entity en `infrastructure/driven-adapters/r2dbc-postgresql/mappers/`
- [ ] Crear repositorio R2DBC en `infrastructure/driven-adapters/r2dbc-postgresql/repositories/` extendiendo `ReactiveCrudRepository`
- [ ] Crear adaptador en `infrastructure/driven-adapters/r2dbc-postgresql/adapters/` usando `ReactiveCrudRepository` para operaciones CRUD
- [ ] (Opcional) Usar `DatabaseClient` solo si se requieren consultas complejas personalizadas
- [ ] Crear caso de uso en `domain/usecase/` (se auto-detecta con patrón `*UseCase`)
- [ ] Crear handler en `infrastructure/entry-points/reactive-web/api/handler/`
- [ ] Crear router en `infrastructure/entry-points/reactive-web/api/router/`
- [ ] Agregar validaciones Jakarta en DTOs
- [ ] Implementar extracción de traceId
- [ ] Agregar logging con traceId
- [ ] Probar flujo completo

### Para Consumo de APIs Externas (Opcional)
- [ ] Crear puerto/interfaz en `domain/model/gateway/` para la API externa
- [ ] Crear DTOs de API externa en `infrastructure/driven-adapters/rest-consumer/dto/`
- [ ] Crear configuración de WebClient en `infrastructure/driven-adapters/rest-consumer/config/`
- [ ] Crear adaptador en `infrastructure/driven-adapters/rest-consumer/adapter/` implementando el puerto
- [ ] Implementar manejo de errores HTTP y mapeo a excepciones de dominio
- [ ] Configurar timeouts en `application.yaml`
- [ ] Usar contexto reactivo (`deferContextual`) para pasar headers dinámicos (tokens, traceId)
- [ ] Inyectar el adaptador en el UseCase y usar `contextWrite` para pasar contexto
- [ ] Agregar logging con traceId en llamadas a API externa
- [ ] Probar integración con API externa

---

## 🔍 Ejemplo Completo: Bootcamp

Ver implementación de referencia en:
- Modelo: `domain/model/Bootcamp.java`
- DTOs: `infrastructure/helpers/validator/dto/request/BootcampRequestDto.java`
- Mapper DTO: `infrastructure/helpers/validator/mappers/BootcampMapper.java`
- Mapper Entity: `infrastructure/driven-adapters/r2dbc-postgresql/mappers/BootcampEntityMapper.java`
- Repository: `infrastructure/driven-adapters/r2dbc-postgresql/repositories/BootcampR2dbcRepository.java`
- Adapter: `infrastructure/driven-adapters/r2dbc-postgresql/adapters/BootcampR2dbcAdapter.java`
- UseCase: `domain/usecase/CreateBootcampUseCase.java`
- Handler: `infrastructure/entry-points/reactive-web/api/handler/BootcampHandler.java`
- Router: `infrastructure/entry-points/reactive-web/api/router/BootcampRouter.java`

## 🔍 Ejemplo Completo: Capability (REST Consumer)

Ver implementación de referencia para consumo de API externa en:
- Puerto: `domain/model/capability/gateway/CapabilityRepository.java`
- Adapter: `infrastructure/driven-adapters/rest-consumer/adapter/CapabilityAdapter.java`
- Config: `infrastructure/driven-adapters/rest-consumer/config/CapabilityConsumerConfig.java`
- DTOs API Externa: `infrastructure/driven-adapters/rest-consumer/dto/CapabilityApiResponse.java`
- Utilidades: `infrastructure/driven-adapters/rest-consumer/util/RestUtil.java`

---

## 🚀 Notas Finales

- Esta guía es genérica y puede adaptarse a diferentes proyectos
- Mantener el desacoplamiento entre capas
- Seguir principios SOLID y Clean Architecture
- Priorizar programación reactiva no bloqueante
- Documentar casos de uso complejos

### ⚡ Optimización de Rendimiento

- **Operaciones CRUD**: Usar `ReactiveCrudRepository` directamente para mayor rapidez y simplicidad
- **Consultas complejas**: Usar `DatabaseClient` solo cuando el ORM no sea suficiente (joins complejos, agregaciones, queries personalizados)
- Esta aproximación reduce la complejidad del código y mejora el rendimiento al aprovechar las optimizaciones del ORM de Spring Data R2DBC

---

**Última actualización**: 2024
**Versión**: 2.0

