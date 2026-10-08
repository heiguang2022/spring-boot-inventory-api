# Spring Boot E-commerce Inventory API

A portfolio-ready REST API that demonstrates focused Java backend delivery: product creation, SKU normalization, inventory updates, duplicate prevention, structured errors, OpenAPI documentation, and automated integration tests.

> This is a self-initiated, reproducible portfolio demonstration. It contains no employer or client code, credentials, or business data.

## Portfolio snapshot

- Java 17 and Spring Boot 3.5
- REST endpoints for product and inventory operations
- SKU normalization and database-backed duplicate protection
- Bean Validation with field-level error responses
- Optimistic locking for safer concurrent inventory updates
- H2 for a zero-setup local demo
- MySQL 8.4 deployment profile with Docker Compose
- Swagger UI and OpenAPI JSON
- JUnit and MockMvc integration tests
- GitHub Actions CI

## API endpoints

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/products` | Create a validated product |
| `GET` | `/api/products` | List products |
| `GET` | `/api/products/{id}` | Get one product |
| `PATCH` | `/api/products/{id}/inventory` | Set a non-negative inventory quantity |
| `DELETE` | `/api/products/{id}` | Delete a product |

## Run locally

Requirements: Java 17+ and Maven 3.6.3+.

```bash
mvn spring-boot:run
```

Then open:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Health check: <http://localhost:8080/actuator/health>

The default profile uses an in-memory H2 database so the demo runs without external infrastructure.

## Try the API

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"sku":"TSH_001","name":"Classic T-Shirt","price":19.99,"quantity":25}'
```

The response contains a normalized `TSH-001` SKU. Repeating the request with `tsh-001` returns HTTP `409 Conflict`.

The included [`requests.http`](requests.http) file provides requests for every endpoint.

## Run with MySQL and Docker

```bash
docker compose up --build
```

The Compose stack starts MySQL, initializes the schema, waits for database health, and then starts the API with the `mysql` Spring profile.

The credentials in `compose.yml` are local demonstration values. Use secrets or an environment file outside version control for any real deployment.

## Test

```bash
mvn verify
```

The integration suite verifies product creation, SKU normalization, duplicate rejection, validation errors, inventory updates, structured `404` responses, listing, and deletion.

## Design decisions

- **Application and database duplicate checks:** the service returns a clear conflict while the unique constraint protects against concurrent inserts.
- **Optimistic locking:** the JPA version column prevents concurrent inventory writes from silently overwriting each other.
- **DTO boundary:** request and response records keep persistence details out of the public API.
- **Structured errors:** clients receive a consistent status, message, request path, and field-level validation map.
- **Two database modes:** H2 makes evaluation fast; MySQL demonstrates a realistic deployment path.

The source architecture is also available as editable Mermaid in [`docs/architecture.mmd`](docs/architecture.mmd).

## Portfolio gallery

Five upload-ready 4:3 images are included in [`portfolio/gallery`](portfolio/gallery), together with their recommended Upwork order and generation prompts. The images describe only features and test results that are present in this repository.

## Project structure

```text
src/main/java/.../inventory/
├── config/       # OpenAPI configuration
├── product/      # Entity, DTOs, repository, service, controller
└── web/          # Consistent API error handling

src/test/java/    # End-to-end MockMvc tests
docker/mysql/     # Production-like MySQL schema
portfolio/        # Upwork-ready copy and gallery assets
```

## Deliberate scope

This demo intentionally excludes authentication, order processing, payments, and multi-warehouse reservations. Those features require explicit business rules and are better scoped as separate deliverables.

## License

Available under the [MIT License](LICENSE).
