# Collections

Personal microservice to manage a collection of cultural items and their metadata.

## Stack

| Component   | Technology                          |
|-------------|-------------------------------------|
| Language    | Java 25 (LTS)                       |
| Framework   | Spring Boot 4.1.x                   |
| Build       | Maven (`mvnw` / `mvnw.cmd`)         |
| Persistence | Spring Data JPA (Hibernate)         |
| Database    | PostgreSQL (Neon, free tier) — H2 in-memory for tests |

## Run

```bash
./mvnw spring-boot:run
```

On Windows: `.\mvnw.cmd spring-boot:run`. The application listens on port `8080`.

## Configuration

The datasource is configured through OS environment variables (canonical) or a
local `.env` file (gitignored, auto-loaded via `spring.config.import`; copy
`.env.example`). OS environment variables take precedence over `.env`. No
connection values are committed to the repository.

| Variable      | Required | Description                                                         |
|---------------|----------|---------------------------------------------------------------------|
| `DB_URL`      | Yes      | JDBC URL, e.g. `jdbc:postgresql://<host>-pooler.<region>.aws.neon.tech/<db>?sslmode=require` |
| `DB_USERNAME` | Yes      | Database username                                                   |
| `DB_PASSWORD` | Yes      | Database password                                                   |
| `DDL_AUTO`    | No       | Hibernate `ddl-auto` mode (default: `update`; env sets `validate`) |
| `SHOW_SQL`    | No       | Log SQL statements (default: `true`)                                |

The developer's OS env vars point at the Neon `dev` branch: database
`collections`, user `collections_app` (read/write role — schema is owned and
managed by `neondb_owner`, so `DDL_AUTO=validate` is used). The `production`
branch keeps the original `neondb` database.

Tests always run against in-memory H2 (`src/test/resources/application.properties`)
and never touch Neon.

## Test

```bash
./mvnw clean verify
```

On Windows: `.\mvnw.cmd clean verify`.

## REST API

`Item` is an abstract base entity — items are always created through
type-specific endpoints. `/api/items` is a read-only view over every item
regardless of its concrete type.

### Items (generic, read-only)

| Method | Path              | Description            | Success |
|--------|-------------------|------------------------|---------|
| GET    | `/api/items`      | List all items         | `200`   |
| GET    | `/api/items/{id}` | Get item by id         | `200`   |
| DELETE | `/api/items/{id}` | Delete item (any type) | `204`   |

### Manga collections

A `MangaCollection` is a whole series/box set; individual volumes (`Manga`)
link to it via `collectionId`.

| Method | Path                          | Description              | Success |
|--------|-------------------------------|--------------------------|---------|
| GET    | `/api/manga-collections`      | List all collections     | `200`   |
| GET    | `/api/manga-collections/{id}` | Get collection by id     | `200`   |
| POST   | `/api/manga-collections`      | Create a collection      | `201`   |
| PUT    | `/api/manga-collections/{id}` | Full update              | `200`   |
| DELETE | `/api/manga-collections/{id}` | Delete a collection      | `204`   |

### Mangas (volumes/editions)

| Method | Path                | Description                                    | Success |
|--------|---------------------|------------------------------------------------|---------|
| GET    | `/api/mangas`       | List all mangas                                | `200`   |
| GET    | `/api/mangas/{id}`  | Get manga by id                                | `200`   |
| POST   | `/api/mangas`       | Upsert single object or array of mangas        | `200`   |
| DELETE | `/api/mangas/{id}`  | Delete a manga                                 | `204`   |

`POST /api/mangas` accepts a single object or an array. Each entry upserts by
`id` when present, otherwise by `isbn` (normalized: spaces and hyphens are
stripped, unique when present), otherwise creates a new manga. `collectionId`
optionally links the volume to a `MangaCollection`. Conflicts return `409`;
unknown ids return `404`; validation failures return `400`.

## Local API testing

A [Bruno](https://www.usebruno.com/) collection lives in [`bruno/`](bruno/).
Open that folder in Bruno, select the `local` environment
(`baseUrl=http://localhost:8080`), and run the requests — the local app is
backed by the Neon `dev` branch (`collections` database).

## AI agent documentation

See [`AGENTS.md`](AGENTS.md) for the project conventions, package structure, verification
commands and known gotchas.
