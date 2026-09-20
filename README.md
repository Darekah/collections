# Collections

Personal microservice to manage a collection of cultural items and their metadata.

## Stack

| Component   | Technology                          |
|-------------|-------------------------------------|
| Language    | Java 25 (LTS)                       |
| Framework   | Spring Boot 4.1.x                   |
| Build       | Maven (`mvnw` / `mvnw.cmd`)         |
| Persistence | Spring Data JPA (Hibernate)         |
| Database    | H2, file mode (`./data/collections-db`) |

## Run

```bash
./mvnw spring-boot:run
```

On Windows: `.\mvnw.cmd spring-boot:run`. The application listens on port `8080`.

## Configuration

The datasource is configured exclusively through environment variables — no connection
values are committed to the repository.

| Variable      | Required | Description                                                         |
|---------------|----------|---------------------------------------------------------------------|
| `DB_URL`      | Yes      | JDBC URL of the database (e.g. `jdbc:h2:file:./data/collections-db`) |
| `DB_USERNAME` | Yes      | Database username                                                   |
| `DB_PASSWORD` | Yes      | Database password (can be empty for H2)                             |
| `DDL_AUTO`    | No       | Hibernate `ddl-auto` mode (default: `update`)                       |
| `SHOW_SQL`    | No       | Log SQL statements (default: `true`)                                |
| `H2_CONSOLE`  | No       | Enable the H2 web console (default: `true`)                         |

Example (Windows PowerShell):

```powershell
$env:DB_URL="jdbc:h2:file:./data/collections-db"; $env:DB_USERNAME="sa"; $env:DB_PASSWORD=""
.\mvnw.cmd spring-boot:run
```

## Test

```bash
./mvnw clean verify
```

On Windows: `.\mvnw.cmd clean verify`.

## REST API

Base path: `/api/items`

| Method | Path                | Description         | Success |
|--------|---------------------|---------------------|---------|
| GET    | `/api/items`        | List all items      | `200`   |
| GET    | `/api/items/{id}`   | Get item by id      | `200`   |
| POST   | `/api/items`        | Create an item      | `201`   |
| PUT    | `/api/items/{id}`   | Full update         | `200`   |
| DELETE | `/api/items/{id}`   | Delete an item      | `204`   |

## AI agent documentation

See [`AGENTS.md`](AGENTS.md) for the project conventions, package structure, verification
commands and known gotchas.
