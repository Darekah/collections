# AGENTS.md — Collections Project

## 1. Overview

**Collections** is a personal microservice for managing a private collection of
cultural items and their metadata. Functional inspiration: the **Whakoom** app,
oriented to personal use (single user, no multitenancy, no social features, no
external sync in v1).

**Item types** (planned, not all in v1): books, comics, movies, TV series;
future: video games, music, board games. Each type has common metadata (title,
year, description, cover…) plus type-specific metadata (ISBN, issue number,
runtime…). The design must allow **adding new types without rewriting the core**.

## 2. Status & scope

- **v1.0.0 in progress**: `Item` is an **abstract** JPA root using JOINED table
  inheritance; concrete types `Manga` and `MangaCollection` have REST APIs
  (`/api/mangas`, `/api/manga-collections`). `/api/items` is read-only
  (GET list/by-id, DELETE).
- **Goal**: working database + REST API for item CRUD per concrete type,
  running locally.
- **Scope intentionally reduced**: start simple, expand iteratively.
- Maven coordinates: `com.project:collections:0.0.1-SNAPSHOT` (see `pom.xml`).

## 3. Tech stack

| Component   | Technology                                                     |
|-------------|----------------------------------------------------------------|
| Language    | Java 25 (LTS) — compile target; tested on Java 27 runtime    |
| Framework   | Spring Boot 4.1.x                                              |
| Build       | Maven (wrapper: `mvnw` / `mvnw.cmd`)                           |
| Persistence | Spring Data JPA (Hibernate)                                    |
| Database    | PostgreSQL on Neon (free tier) via env vars; H2 in-memory for tests |
| Validation  | Jakarta Bean Validation                                        |
| Testing     | JUnit 5 + Mockito + MockMvc                                    |
| IDE         | IntelliJ IDEA Community                                        |

**Spring Boot 4 note**: starters are modularized. The web starter is
`spring-boot-starter-webmvc`; test starters follow the same pattern
(`webmvc-test`, `data-jpa-test`, `validation-test`). Always check `pom.xml`
before naming an artifact.

**Pending decisions** (consult before acting): Flyway/Liquibase for schema
migrations; jOOQ or Spring Data JDBC if queries get complex.

## 4. Package structure

Root package: `com.project.collections`

```
com.project.collections
├── CollectionsApplication   # Main class
├── controller/              # Generic item endpoints (ItemController)
│   └── manga/               # MangaController, MangaCollectionController
├── service/                 # ItemService (generic, read-only)
│   └── manga/               # MangaService, MangaCollectionService
├── repository/              # ItemRepository
│   └── manga/               # MangaRepository, MangaCollectionRepository
├── model/                   # JPA base entities (Item, ItemType)
│   └── manga/               # Manga + MangaCollection concrete types
├── dto/                     # ItemResponse
│   └── manga/               # Manga*/MangaCollection* request/response records
└── exception/               # Exceptions + @ControllerAdvice
```

**Naming conventions**:
- Classes in PascalCase; methods and variables in camelCase.
- JPA entities: no suffix (`Item`, not `ItemEntity`).
- DTOs: suffix `Request` / `Response` (`ItemRequest`, `ItemResponse`).
- Type-specific classes (entities, DTOs, services, repositories, controllers)
  live in a `<type>` subpackage under each layer (`model/manga`,
  `dto/manga`, …); generic `Item*` classes stay at the package root.
- Controllers: suffix `Controller`.
- Services: `Service` suffix. Use `Service` + `ServiceImpl` only if there is
  more than one implementation; otherwise a single `XxxService` class.

## 5. Conventions

**Code**:
- Identifiers, comments and Javadoc in English.
- Prefer `record` for DTOs.
- No business logic in controllers; they only orchestrate.
- No repository access from controllers — always through services.
- Transactions at the service layer (`@Transactional`).
- **Do not add dependencies or structurally modify `pom.xml`** without explicit
  confirmation.
- **Do not introduce frameworks** (MapStruct, Lombok, etc.) without consulting;
  minimize magic in v1.
- DTO validation must mirror the entity's column size constraints via `@Size`.

**REST API**:
- Base prefix `/api`; generic read-only resource `/api/items`; type-specific
  resources per concrete item (`/api/mangas` is the first).
- Standard verbs: `GET` (list, by id), `POST` (create), `PUT` (full update),
  `PATCH` (partial), `DELETE`.
- Response codes: `200`, `201`, `204`, `400` (validation), `404`, `409`.
- Error body: common `ErrorResponse` (code, message, details).
- Pagination: `page`, `size`, `sort` (Spring Data format).
- No URL versioning for now (`/v1/...`); revisit if needed.

## 6. Data model

`Item` is an **abstract** entity (table `items`) — it is never instantiated.
Every persisted item is a concrete subclass joined to `items` on the shared PK
`id` via JOINED inheritance with discriminator column `type`. Because `Item` is
abstract, `/api/items` exposes only GET (list, by id) and DELETE — writes go
through type endpoints. Concrete types so far: `Manga` (`mangas` table) and
`MangaCollection` (`manga_collections` table).

Entity `Item` (table `items`):

| Field         | Type    | Notes                                                                          |
|---------------|---------|--------------------------------------------------------------------------------|
| `id`          | Long    | Auto-generated PK, shared with subclass tables (global item identifier)        |
| `title`       | String  | Required, max 255 chars                                                        |
| `originalTitle` | String | Optional, max 255 chars                                                      |
| `description` | String  | Optional, max 2000 chars                                                       |
| `type`        | Enum    | Discriminator (`EnumType.STRING`, `insertable=false, updatable=false`); `BOOK`, `COMIC`, `MOVIE`, `SERIES`, `MANGA`, `MANGA_COLLECTION` |
| `year`        | Integer | Optional, maps to `publication_year` (see §7)                                  |
| `coverUrl`    | String  | Optional, max 500 chars                                                        |
| `createdAt`   | Instant | Set by JPA `@PrePersist`                                                       |
| `updatedAt`   | Instant | Set by `@PrePersist` / `@PreUpdate`                                            |

Entity `Manga` (table `mangas`, `@DiscriminatorValue("MANGA")`) — a volume/edition:

| Field           | Type    | Notes                                                        |
|-----------------|---------|--------------------------------------------------------------|
| `id`            | Long    | PK + FK to `items.id`                                        |
| `author`        | String  | Required, max 255 chars                                      |
| `artist`        | String  | Optional, max 255 chars                                      |
| `volumeNumber`  | Integer | Optional                                                     |
| `isbn`          | String  | Optional, max 20 chars, **unique when present**; normalized on write (spaces/hyphens stripped). Product-level identifier and upsert key |
| `collectionId`  | Long    | Optional FK → `manga_collections.id`. Links a volume to its collection |

Entity `MangaCollection` (table `manga_collections`,
`@DiscriminatorValue("MANGA_COLLECTION")`) — a whole series/box set:

| Field      | Type   | Notes                          |
|------------|--------|--------------------------------|
| `id`       | Long   | PK + FK to `items.id`          |
| `author`   | String | Required, max 255 chars        |
| `artist`   | String | Optional, max 255 chars        |

CRUD for collections lives at `/api/manga-collections` (standard
GET/POST/PUT/DELETE). `MangaRepository.findByCollection_Id` lists the volumes
of a collection.

`POST /api/mangas` upserts a single object or an array: match by `id` first,
then by normalized `isbn`, otherwise create. `id`+`isbn` pointing at different
rows → `409`; unknown `id`/`collectionId` → `404`.

**Do not add Spring Data auditing** (`@EnableJpaAuditing`,
`AuditingEntityListener`) on top of the existing JPA callbacks — it would
conflict with them.

**Planned evolution** (do not implement yet): `ItemMetadata` key/value table;
specialized entities (`Book`, `Comic`, …); tags with N:M relation.

## 7. Database & gotchas

**Current state**: the app runs against **PostgreSQL on Neon** (free tier).
H2 remains only as the **test** database (`jdbc:h2:mem:testdb`, `create-drop`);
the `h2` dependency is `test`-scoped and `org.postgresql:postgresql` is the
runtime driver. The old local H2 file DB in `data/` is legacy — the app can no
longer run on H2 (driver absent at runtime).

**Neon topology** (project `lively-frog-76997729`, managed via the `neon` CLI
at `C:\Users\jesus\tools\neon-win-x64.exe` — not on PATH):

- `dev` branch → database **`collections`** — what the local app uses.
  `production` branch → database `neondb` — untouched for now.
- Roles: `neondb_owner` (admin, owns the schema) and `collections_app`
  (`--access read_write`, scoped to `collections`: SELECT/INSERT/UPDATE/
  DELETE on current and future tables — **no DDL**).
- Because `collections_app` cannot alter schema, env config sets
  `DDL_AUTO=validate`: entity changes require the owner (or future Flyway)
  to evolve the schema first. Schema was generated by Hibernate `update`
  during provisioning.
- Neon CLI cheat sheet: `neon branches list`, `neon databases list --branch dev`,
  `neon roles list --branch dev`, `neon psql dev --database-name collections`,
  `neon connection-string dev --database-name collections --pooled`.

- **Secrets**: real credentials live in the developer's **OS env vars**
  (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DDL_AUTO`) — never commit them.
  A local `.env` (gitignored, see `.env.example`) is an optional fallback,
  auto-loaded via `spring.config.import=optional:file:.env[.properties]`;
  real env vars take precedence. Write `.env` **without BOM** (PowerShell
  `Out-File -Encoding utf8` adds one; use `[IO.File]::WriteAllText` with
  `UTF8Encoding($false)`).
- **Datasource** (`src/main/resources/application.properties`):
  `spring.datasource.url=${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`,
  `spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}`,
  `spring.jpa.show-sql=${SHOW_SQL:true}`. Neon JDBC URL:
  `jdbc:postgresql://<host>-pooler.<region>.aws.neon.tech/<db>?sslmode=require`
  (pooled host recommended for serverless).
- **OS env vars override `.env`**: `DB_*` vars set at OS level silently beat
  `.env` (this already caused one `org.h2.Driver` startup failure). They are
  now the canonical store — if you switch databases, update the OS env vars
  (and `.env` too if you keep one).
- **Pending decision**: adopt Flyway — `ddl-auto` can't drop columns or
  migrate data (see the `collection_id` repoint incident), and the app role
  deliberately lacks DDL. Sample data via `data.sql` under `dev`.

**Gotchas worth remembering**:

- **H2 keyword gotchas** (tests only now): `YEAR` is a reserved keyword in H2.
  The `Item.year` field maps to column `publication_year`. Any new field whose
  name collides with an H2 keyword must be renamed at the column level —
  Postgres is more permissive but keep the mapping for consistency.
- **A green build does not prove the schema exists**. Hibernate logs DDL
  failures at **WARN** and still starts the context, so
  `CollectionsApplicationTests` may pass even when `create table items …`
  failed. After touching an entity, check the run log for DDL errors.
- **Local API testing**: a Bruno collection lives in `bruno/` (open the folder
  in Bruno, select the `local` environment — it targets the locally running
  app, which is backed by the Neon `dev` branch).

## 8. Verification commands

```bash
./mvnw clean verify        # Compile and run tests (primary check)
./mvnw spring-boot:run     # Start the application locally
./mvnw test                # Run tests only
```

On Windows: `.\mvnw.cmd clean verify`.

`clean verify` is the primary check, but **a green build does not prove the schema
was created**: grep the log for `CommandAcceptanceException` (see §7). The
`@DataJpaTest` in `ItemDataJpaTest` guards the `items` mapping.

Application default port: `8080`.

## 9. Testing

- **Unit**: JUnit 5 + Mockito. Location: `src/test/java/...`.
- **Integration**: `@SpringBootTest` + MockMvc for controllers.
- **Persistence**: `@DataJpaTest` with in-memory H2 for repositories.
- **Boot 4 migration**: `DataJpaTest` moved to
  `org.springframework.boot.data.jpa.test.autoconfigure`;
  `TestEntityManager` to `org.springframework.boot.jpa.test.autoconfigure`.
  The Boot 3 packages under `org.springframework.boot.test.autoconfigure.orm.jpa`
  no longer exist.
- **Naming**: `verb_subject_expectedResult`
  (e.g. `validate_blankTitle_returnsViolationOnTitle`).
- **Coverage goal**: not set for v1; prioritize critical paths.

## 10. Working with the agent

When receiving a task:

1. Read only the relevant context. Prefer the IDE index over reading whole files.
2. Propose a brief plan before writing code in multi-file tasks.
3. Write code following the conventions in §4–§6.
4. Verify with the commands in §8.
5. Summarize: files touched and rationale.

**Do not**:
- Invent dependencies or APIs not present in the declared stack.
- Add obvious comments (`// increment i`).
- Generate trivial tests covering only getters/setters.
- Refactor unrelated code.

## 11. Roadmap

- **v1.0.0** (current, unreleased): see §2.
- **v1.1.x**: DTO-level validation review, pagination, filtering.
- **v1.2.x**: type-specific metadata, first specialized type (books).
- **v1.3.x**: search and filtering, tags.
- **v2.0.0**: Flyway migrations, optional basic auth. (PostgreSQL/Neon done.)

Indicative only. Consult before deviating.

---

*Last updated: 2026-11-10*