# Ecommerce App

Spring Boot ecommerce API using PostgreSQL and MyBatis.

## Run Locally

The default configuration connects to the local PostgreSQL instance:

- Host: `localhost`
- Port: `5432`
- Database: `postgres`
- Username: `postgres`
- Password: `1234`

Make sure PostgreSQL is running and that the `postgres` user has access to the `postgres` database. The application creates its tables in the database's default schema when it starts.

Run the application on Windows:

```powershell
.\gradlew.bat bootRun
```

Run tests without a local PostgreSQL server:

```powershell
.\gradlew.bat test
```

Tests activate the `test` profile and use an in-memory H2 database in PostgreSQL compatibility mode.

## Configure a Server

Do not put server credentials in `application.properties`. Set the production profile and database connection as environment variables instead. The JDBC URL must identify the PostgreSQL host, port, and database name.

PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:DATABASE_URL = "jdbc:postgresql://db.example.com:5432/ecommerce"
$env:DATABASE_USERNAME = "ecommerce_app"
$env:DATABASE_PASSWORD = "replace-with-a-secret"
java -jar build/libs/java0-0.0.1-SNAPSHOT.jar
```

Linux:

```sh
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://db.example.com:5432/ecommerce
export DATABASE_USERNAME=ecommerce_app
export DATABASE_PASSWORD='replace-with-a-secret'
java -jar build/libs/java0-0.0.1-SNAPSHOT.jar
```

Use the server or container platform's secret manager for `DATABASE_PASSWORD`, create a dedicated database/user with only the permissions the app needs, and allow port 5432 only from trusted application hosts. `application-prod.properties` uses the same `schema.sql` to create missing tables at startup; this is suitable for initial setup, but use a migration tool such as Flyway for controlled schema changes in ongoing production deployments.

## Persistence and DTO Mapping

- `repository/` contains MyBatis interfaces; SQL statements are in `src/main/resources/mappers/*.xml`.
- `mapper/` contains Java converters between request/response DTOs and entities. It does not contain database queries, validation, or business rules.
- `service/` owns business logic and invokes repositories and DTO mappers.