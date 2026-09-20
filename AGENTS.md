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

- **v1.0.0 in progress**: `Item` entity implemented; controller, service,
  repository, DTO and exception layers pending.
- **Goal**: working database + REST API for basic item CRUD, running locally.
- **Scope intentionally reduced**: start simple, expand iteratively.
- Maven coordinates: `com.project:collections:0.0.1-SNAPSHOT` (see `pom.xml`).

## 3. Tech stack

| Component   | Technology                                                     |
|-------------|----------------------------------------------------------------|
| Language    | Java 25 (LTS) — compile target; tested on Java 27 runtime    |
| Framework   | Spring Boot 4.1.x                                              |
| Build       | Maven (wrapper: `mvnw` / `mvnw.cmd`)                           |
| Persistence | Spring Data JPA (Hibernate)                                    |
| Database    | H2, embedded in-memory today; file mode is the target (see §7) |
| Validation  | Jakarta Bean Validation                                        |
| Testing     | JUnit 5 + Mockito + MockMvc                                    |
| IDE         | IntelliJ IDEA Community                                        |

**Spring Boot 4 note**: starters are modularized. The web starter is
`spring-boot-starter-webmvc`; test starters follow the same pattern
(`webmvc-test`, `data-jpa-test`, `validation-test`). Always check `pom.xml`
before naming an artifact.

**Pending decisions** (consult before acting): H2 → PostgreSQL migration;
Flyway/Liquibase once the schema stabilizes; jOOQ or Spring Data JDBC if
queries get complex.

## 4. Package structure

Root package: `com.project.collections`

```
com.project.collections
├── CollectionsApplication   # Main class
├── controller/              # REST endpoints
├── service/                 # Business logic
├── repository/              # Spring Data interfaces
├── model/                   # JPA entities
├── dto/                     # Request/Response DTOs
└── exception/               # Exceptions + @ControllerAdvice
```

**Naming conventions**:
- Classes in PascalCase; methods and variables in camelCase.
- JPA entities: no suffix (`Item`, not `ItemEntity`).
- DTOs: suffix `Request` / `Response` (`ItemRequest`, `ItemResponse`).
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
- Base prefix `/api`; main resource `/api/items`.
- Standard verbs: `GET` (list, by id), `POST` (create), `PUT` (full update),
  `PATCH` (partial), `DELETE`.
- Response codes: `200`, `201`, `204`, `400` (validation), `404`, `409`.
- Error body: common `ErrorResponse` (code, message, details).
- Pagination: `page`, `size`, `sort` (Spring Data format).
- No URL versioning for now (`/v1/...`); revisit if needed.

## 6. Data model

Entity `Item` (table `items`):

| Field         | Type    | Notes                                                                          |
|---------------|---------|--------------------------------------------------------------------------------|
| `id`          | Long    | Auto-generated PK                                                              |
| `title`       | String  | Required, max 255 chars                                                        |
| `description` | String  | Optional, max 2000 chars                                                       |
| `type`        | Enum    | Required, max 20 chars; `BOOK`, `COMIC`, `MOVIE`, `SERIES` (`EnumType.STRING`) |
| `year`        | Integer | Optional, maps to `publication_year` (see §7)                                  |
| `createdAt`   | Instant | Set by JPA `@PrePersist`                                                       |
| `updatedAt`   | Instant | Set by `@PrePersist` / `@PreUpdate`                                            |

**Do not add Spring Data auditing** (`@EnableJpaAuditing`,
`AuditingEntityListener`) on top of the existing JPA callbacks — it would
conflict with them.

**Planned evolution** (do not implement yet): `ItemMetadata` key/value table;
specialized entities (`Book`, `Comic`, …); tags with N:M relation.

## 7. Database & gotchas

**Current state**: H2 runs in **file mode** locally. The configuration below is
applied and verified (2026-09-20).

- **Main config** (`src/main/resources/application.properties`):
  `spring.datasource.url=jdbc:h2:file:./data/collections-db`, user `sa` with an
  empty password, `spring.jpa.hibernate.ddl-auto=update`,
  `spring.jpa.show-sql=true`, and the H2 console **enabled** at
  `spring.h2.console.path=/h2-console`
  (`http://localhost:8080/h2-console`). The `spring-boot-h2console` module is on
  the classpath, but the console only exists because
  `spring.h2.console.enabled=true`.
- **Test config** (`src/test/resources/application.properties`): overrides the
  URL with `jdbc:h2:mem:testdb` and
  `spring.jpa.hibernate.ddl-auto=create-drop`, so tests never touch the real
  file DB — verified by a `clean verify` run that left the database file's
  `LastWriteTime` unchanged. (`@DataJpaTest` additionally substitutes its own
  randomly named in-memory DB.)
- **Database files**: `./data/` is created by H2 on the first application start
  and holds the real database (`collections-db.mv.db`). The folder is
  **gitignored**, so it never reaches the repository. Never point tests at these
  files and never delete the folder.

**Gotchas worth remembering**:

- **`YEAR` is a reserved keyword in H2**. The `Item.year` field therefore maps
  to column `publication_year`. Any new field whose name collides with an H2
  keyword must be renamed at the column level.
- **A green build does not prove the schema exists**. Hibernate logs DDL
  failures at **WARN** (`o.h.t.s.i.ExceptionHandlerLoggedImpl`) and still starts
  the context, so `CollectionsApplicationTests` may pass even when
  `create table items …` failed. After touching an entity, search the log for
  `CommandAcceptanceException` or an H2 syntax error.
- **Once the schema stabilizes**: switch to `ddl-auto=validate` and adopt
  Flyway. Sample data can be loaded via `data.sql` under the `dev` profile.

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

Application default port: `8080`. H2 console (once enabled):
`http://localhost:8080/h2-console`.

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
- **v1.1.x**: DTOs, DTO-level validation (entity validation already implemented), global error handling, pagination.
- **v1.2.x**: type-specific metadata, first specialized type (books).
- **v1.3.x**: search and filtering, tags.
- **v2.0.0**: PostgreSQL migration, Flyway, optional basic auth.

Indicative only. Consult before deviating.

---

*Last updated: 2026-09-20*