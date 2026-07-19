# Bootcamp - Bootcamp Microservice

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![WebFlux](https://img.shields.io/badge/WebFlux-Reactive-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://docs.spring.io/spring-framework/reference/web/webflux.html)
[![Gradle](https://img.shields.io/badge/Gradle-8.14-02303A?style=flat-square&logo=gradle&logoColor=white)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Descripcion

Microservicio principal de la plataforma **Bootcamp**. Gestiona la creacion, consulta, eliminacion y listing de bootcamps, incluyendo su asociacion con capacidades y notificacion a otros microservicios.

**Puerto:** `8091`
**Base Path:** `/bootcamp-service`

## Stack Tecnologico

| Componente | Tecnologia |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.5 |
| Web | Spring WebFlux (Reactiva) |
| Base de datos | MySQL via R2DBC |
| Pool de conexiones | R2DBC Pool |
| Build Tool | Gradle |
| Mapeo | MapStruct 1.5.5 + Lombok |
| Documentacion API | SpringDoc OpenAPI 2.6.0 (WebFlux) |
| Resiliencia | Resilience4j (CircuitBreaker, Retry, Bulkhead) |
| Comunicacion | WebClient (capacidad-service, tecnologia-service, reporte-service) |
| Monitoreo | Spring Boot Actuator |
| Testing | JUnit 5 + Mockito + Reactor Test |
| Coverage | JaCoCo 0.8.8 |

## Arquitectura

Arquitectura Hexagonal (Puertos y Adaptadores) con stack reactivo:

```
com.onclass.bootcamp
├── application/
│   ├── config/                    # Configuracion (UseCases, WebClient)
│   └── configSwagger/             # Configuracion OpenAPI/Swagger
├── domain/
│   ├── api/                       # Puertos entrantes (BootcampServicePort)
│   ├── spi/                       # Puertos salientes (Persistence + Client ports)
│   ├── model/                     # Modelos de dominio (Bootcamp, BootcampList)
│   ├── usecase/                   # Caso de uso (BootcampUseCase)
│   ├── constants/                 # Constantes de dominio
│   ├── criteria/                  # Criterios de consulta (paginacion, ordenamiento)
│   ├── enums/                     # Mensajes tecnicos
│   ├── exceptions/                # Excepciones de dominio
│   └── utils/                     # CapacidadSummary, TecnologiaSummary, PageResult
└── infrastructure/
    ├── entrypoints/
    │   ├── RouterRest             # Rutas funcionales WebFlux
    │   ├── handler/               # BootcampHandlerImpl
    │   ├── dto/                   # BootcampDTO, BootcampListDTO, BootcampCapacidadDTO, etc.
    │   ├── mapper/                # BootcampMapper
    │   └── util/                  # Constants, APIResponse, ErrorDTO
    └── adapters/
        ├── persistence/
        │   ├── entity/            # BootcampEntity
        │   ├── repository/        # BootcampRepository, CustomBootcampRepository
        │   └── mapper/            # BootcampEntityMapper
        ├── client/                # WebClient adapters
        │   ├── CapacidadClientAdapter
        │   ├── TecnologiaClientAdapter
        │   └── BootcampReporteClientAdapter
        └── util/                  # ClientConstants, EntityConstants, etc.
```

## Endpoints

Todos los endpoints requieren el header `x-message-id` para trazabilidad.

| Metodo | Ruta | Descripcion |
|---|---|---|
| `POST` | `/bootcamp-service/bootcamps` | Crear un nuevo bootcamp |
| `GET` | `/bootcamp-service/bootcamps` | Listar bootcamps (paginado, ordenado) |
| `GET` | `/bootcamp-service/bootcamps/{id}` | Obtener bootcamp por ID |
| `DELETE` | `/bootcamp-service/bootcamps/{id}` | Eliminar bootcamp |

### Request - Crear Bootcamp

```json
{
  "nombre": "Bootcamp Full Stack",
  "descripcion": "Bootcamp intensivo de desarrollo full stack con Java y React",
  "fechaLanzamiento": "2026-08-01",
  "duracion": 12,
  "capacidades": [1, 2, 3]
}
```

**Validaciones:**
- `nombre`: requerido, max 50 caracteres, debe ser unico
- `descripcion`: requerido, max 150 caracteres
- `fechaLanzamiento`: requerida, no puede ser en el pasado
- `duracion`: requerida (semanas)
- `capacidades`: minimo 1, maximo 4 IDs, sin duplicados

**Response (201 Created):**
```json
{
  "code": "201-0",
  "message": "Bootcamp creado exitosamente",
  "identifier": "msg-uuid-123",
  "date": "2026-07-17T10:30:00",
  "data": {
    "id": 1,
    "nombre": "Bootcamp Full Stack",
    "descripcion": "Bootcamp intensivo de desarrollo full stack con Java y React",
    "fechaLanzamiento": "2026-08-01",
    "duracion": 12,
    "capacidades": [1, 2, 3]
  }
}
```

### Request - Listar Bootcamps

**Query Params:**
- `page` (default: 0)
- `size` (default: 10)
- `sort` (opcional: `nombre`, `cantidadcapacidades`)
- `order` (opcional: `asc`, `desc`)

### Request - Eliminar Bootcamp

Elimina el bootcamp y realiza limpieza cascade de capacidades y tecnologias huérfanas.

### Respuesta de Error

```json
{
  "code": "400-2",
  "message": "El bootcamp ya esta registrado",
  "identifier": "msg-uuid-123",
  "date": "2026-07-17T10:30:00",
  "errors": [
    {
      "code": "400-2",
      "message": "El bootcamp ya esta registrado",
      "param": "nombre"
    }
  ]
}
```

## Modelo de Datos

```
┌──────────────────┐
│    bootcamp      │
├──────────────────┤
│ id (PK)          │
│ nombre           │
│ descripcion      │
│ fecha_lanzamiento│
│ duracion         │
└──────────────────┘
```

## Integraciones

| Servicio | Puerto | Uso |
|---|---|---|
| capacidad-service | 8090 | Obtener/gestionar capacidades del bootcamp |
| tecnologia-service | 8080 | Obtener tecnologias asociadas a capacidades |
| reporte-service | 8093 | Notificar creacion de bootcamps |

## Variables de Entorno

| Variable | Descripcion | Ejemplo |
|---|---|---|
| `DB_HOST` | Host de MySQL | `localhost` |
| `DB_PORT` | Puerto de MySQL | `3306` |
| `DB_NAME` | Nombre de la base de datos | `bootcamp` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contrasena de MySQL | `password` |

**Base de datos por defecto:** `bootcamp`

## Resiliencia

| Patron | Nombre | Configuracion |
|---|---|---|
| CircuitBreaker | `bootcampDB` | Proteccion contra fallos de DB |
| Retry | `bootcampRetry` | Max 5 intentos, backoff exponencial |
| Bulkhead | `bootcampBulkhead` | Max 5 llamadas concurrentes |

## Actuator

```
/bootcamp-service/actuator/health
/bootcamp-service/actuator/metrics
/bootcamp-service/actuator/loggers
```

## Ejecutar el Proyecto

```bash
cd bootcamp-service
./gradlew bootRun
```

La aplicacion estara disponible en `http://localhost:8091`

> **Requisito:** MySQL debe estar ejecutandose en `localhost:3306` con la base de datos `bootcamp`

## Documentacion API (Swagger)

```
http://localhost:8091/swagger-ui.html
http://localhost:8091/v3/api-docs
```

## Ejecutar Tests

```bash
./gradlew test
```

## Reglas de Negocio

- Un bootcamp debe tener entre 1 y 4 capacidades asociadas
- No se permiten capacidades duplicadas en un bootcamp
- La fecha de lanzamiento no puede ser en el pasado
- Al eliminar un bootcamp se realiza limpieza cascade de capacidades y tecnologias huérfanas
