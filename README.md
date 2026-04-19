# Emulator Server API

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=bugs)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=jonnattangc_java.server&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=jonnattangc_java.server)

---

## Resumen

Emulador de APIs de terceros construido para apoyar el desarrollo mobile y pruebas de integración. Expone endpoints REST que replican el comportamiento de servicios de pago, tokenización, autenticación y gestión de tarjetas, y persiste transacciones, tarjetas, usuarios y configuraciones en MySQL.

## Tecnologías

| Componente          | Versión  |
|---------------------|----------|
| Java                | 21       |
| Spring Boot         | 4.0.5    |
| MySQL Connector     | 8.0.33   |
| Lombok              | 1.18.38  |
| BouncyCastle        | 1.70     |
| Apache HttpClient   | 4.5.14   |
| Log4j2              | 2.21.0   |
| JUnit               | 4.13.1   |
| Maven               | 3.9      |

## Arquitectura

Proyecto Maven multi-módulo:

| Módulo            | Responsabilidad                                                                 |
|-------------------|----------------------------------------------------------------------------------|
| `server.app`      | Aplicación Spring Boot. Controllers REST y punto de entrada (`Application.java`). |
| `server.services` | Lógica de negocio, configuración, seguridad, criptografía y utilidades.          |
| `server.dao`      | Repositorios Spring Data JPA.                                                    |
| `server.domain`   | Entidades JPA (`Card`, `Transaction`, `User`, `Device`, `Configuration`) y enums. |
| `server.json`     | DTOs de request/response y serialización Jackson.                                |

```
java.server/
├── server.app/          # Controllers y bootstrap Spring Boot
├── server.services/     # Servicios, configuración y utilidades
├── server.dao/          # Repositorios JPA
├── server.domain/       # Entidades y enums
├── server.json/         # DTOs
├── Dockerfile
├── docker-compose.yml
└── pom.xml              # POM raíz
```

## Endpoints

> Base path: `${CONTEXT}` (por defecto `/emulator`). Todos los ejemplos omiten el context path.

### Configuración dinámica — `/config`

| Método | Path              | Request                          | Response                          | Descripción                                   |
|--------|-------------------|----------------------------------|-----------------------------------|-----------------------------------------------|
| POST   | `/config/create`  | `AppConfigurationRequestDTO`     | `String`                          | Crea configuración para un endpoint.          |
| POST   | `/config/update`  | `AppConfigurationRequestDTO`     | `String`                          | Actualiza configuración existente.            |
| POST   | `/config/save`    | `AppConfigurationRequestDTO`     | `String`                          | Persiste la configuración.                    |
| GET    | `/config/list`    | —                                | `AppListConfigurationDTOResponse` | Lista todas las configuraciones.              |

### Pagos y Tokens (ApiTec) — `CetipaController`

| Método | Path                                              | Request                                    | Response       | Descripción                                               |
|--------|---------------------------------------------------|--------------------------------------------|----------------|-----------------------------------------------------------|
| POST   | `/pay/ptm/v1/authorizations`                      | `EdrPayAuthorizeRequestDTO`                | `IEmulator`    | Autoriza una transacción de pago.                         |
| POST   | `/pay/ptm/v1/voids`                               | `EdrPaymentReverseRequestDTO`              | `IEmulator`    | Reversa o anula una transacción autorizada.               |
| POST   | `/tsp/ttm/v1/enrollments`                         | `EdrTokenEnrollmentRequestDTO`             | `IEmulator`    | Enrola un dispositivo o tarjeta.                          |
| POST   | `/tsp/ttm/v1/par/search`                          | `EdrTokenGetDigitalPanRequestDTO`          | `IEmulator`    | Búsqueda de tarjeta digital (PAR).                        |
| GET    | `/tsp/ttm/v1/tokens/{token}`                      | —                                          | `IEmulator`    | Obtiene el detalle del token.                             |
| POST   | `/tsp/ttm/v1/tokens/{token}/acquired`             | —                                          | `String`       | Notifica que el token fue adquirido.                      |
| POST   | `/tsp/ttm/v1/tokens/{token}/cryptograms`          | `EdrPaymentCreateCryptogramRequestDTO`     | `IEmulator`    | Genera criptograma para el token.                         |
| POST   | `/pay/prm/v1/requestors/{requestor}/logon`        | `EdrPaymentLogonRequestDTO`                | `IEmulator`    | Logon PRM (Payment Requestor). Retorna `access_token` en header. |
| POST   | `/tsp/trm/v1/requestors/{requestor}/logon`        | `EdrTokenLogonRequestDTO`                  | `IEmulator`    | Logon TRM (Token Requestor). Retorna `access_token` en header. |

### Commerce — `/commerceapps`

| Método | Path                      | Request                   | Response                   | Descripción                              |
|--------|---------------------------|---------------------------|----------------------------|------------------------------------------|
| POST   | `/commerceapps/sendMail`  | Body crudo                | `CommerceMailResponseDTO`  | Simula envío de email desde Commerce.    |
| POST   | `/commerceapps/getToken`  | `MultiValueMap` (headers) | `CommerceTokenResponseDTO` | Emite token de acceso emulado.           |

### CXP (proxy ChileExpress) — `/cxp`

| Método | Path        | Request                 | Response            | Descripción                                              |
|--------|-------------|-------------------------|---------------------|----------------------------------------------------------|
| POST   | `/cxp/**`   | `HttpServletRequest`    | `ICxpResponse`      | Reenvía POST al backend CXP configurado dinámicamente.   |
| GET    | `/cxp/**`   | `HttpServletRequest`    | `String`            | Reenvía GET al backend CXP configurado dinámicamente.    |

### Logon externos — `/edr`

| Método | Path                      | Request                   | Response   | Descripción                     |
|--------|---------------------------|---------------------------|------------|---------------------------------|
| POST   | `/edr/login/tickettest`   | `MultiValueMap` (headers) | `String`   | Logon de prueba (EDR).      |
| POST   | `/edr/login/ticket`       | `MultiValueMap` (headers) | `String`   | Logon EDR.                  |
| POST   | `/edr/login/beanuj`       | `MultiValueMap` (headers) | `String`   | Logon JUNAEB.                   |

### EDR-EGD Foods

| Método | Path                        | Request               | Response      | Descripción                              |
|--------|-----------------------------|-----------------------|---------------|------------------------------------------|
| ANY    | `/api/v1/foods/cards/**`    | `HttpServletRequest`  | `IEmulator`   | Búsqueda y procesamiento de tarjetas.    |

### Página personal — `/page`

| Método | Path                 | Request                              | Response      | Descripción                          |
|--------|----------------------|--------------------------------------|---------------|--------------------------------------|
| POST   | `/page/users/save`   | `MultiValueMap` (form-urlencoded)    | `IEmulator`   | Registra un usuario nuevo (CORS).    |
| GET    | `/page/users`        | —                                    | `IEmulator`   | Lista los usuarios (CORS).           |

## Configuración

La aplicación selecciona su perfil activo mediante la variable `ENV` (`local`, `dev`, `prod`).

### Variables de entorno

| Variable     | Descripción                                     | Default       |
|--------------|-------------------------------------------------|---------------|
| `ENV`        | Perfil Spring activo.                           | `dev`         |
| `PORT`       | Puerto HTTP del servidor.                       | `8089`        |
| `CONTEXT`    | Context path de la aplicación.                  | `/emulator`   |
| `BD_ADDR`    | Host MySQL.                                     | —             |
| `BD_PORT`    | Puerto MySQL.                                   | `3306`        |
| `BD_NAME`    | Nombre del esquema.                             | —             |
| `BD_USER`    | Usuario MySQL.                                  | —             |
| `BD_PASS`    | Password MySQL.                                 | —             |
| `LOG_LEVEL`  | Nivel de logging (`DEBUG`, `INFO`, ...).        | `INFO`        |
| `AES_KEY`    | Clave AES para cifrado (32+ caracteres).        | —             |

### Persistencia

- JDBC URL: `jdbc:mysql://${BD_ADDR}:${BD_PORT}/${BD_NAME}?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true`
- Pool: HikariCP (`max-pool-size=10`, `connection-timeout=30s`).
- DDL: `hibernate.ddl-auto=update` en ambientes de desarrollo.
- Perfil `local` usa H2 en memoria.

## Ejecución

### Maven

```bash
export ENV=local
export PORT=8089
export CONTEXT=/emulator
export LOG_LEVEL=DEBUG
export AES_KEY=mAFaa23csdas5sdf12sght549u87y8adnjk
export BD_ADDR=localhost
export BD_PORT=3306
export BD_NAME=emulator
export BD_USER=emulator
export BD_PASS=emulator

mvn clean package
java -Xmx1024m -Xms512m -jar server.app/target/server.app-1.2.0-SNAPSHOT.jar
```

### Docker

```bash
docker build -t java-server:v1 .
docker-compose up -d
```

`docker-compose.yml` publica el puerto `8089` y lee las variables desde `../envs/java_server.env`. El `Dockerfile` usa build multi-stage con Maven 3.9 y Eclipse Temurin 25 como runtime.

## Seguridad

Consulta [SECURITY.md](./SECURITY.md) para la política de reporte de vulnerabilidades.

## Licencia

Ver [LICENSE](./LICENSE).
