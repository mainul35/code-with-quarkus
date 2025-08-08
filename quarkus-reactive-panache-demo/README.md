
# Quarkus Reactive Panache Demo

A minimal Quarkus application demonstrating **reactive REST endpoints** with **Hibernate Reactive Panache** and a **MySQL** backend. This project is designed for non-blocking, scalable CRUD operations on a simple `Fruit` entity.

---

## Table of Contents

- [Project Structure](#project-structure)
- [Dependency and Code Explanations](#dependency-and-code-explanations)
  - [pom.xml](#pomxml)
  - [mysql.docker-compose.yml & sql_scripts/init.sql](#mysqldocker-composeyml--sql_scriptsinitsql)
  - [src/main/resources/application.properties](#srcmainresourcesapplicationproperties)
  - [src/main/java/dev/mainul35/fruits/Fruit.java](#srcmainjavadevmainul35fruitsfruitjava)
  - [src/main/java/dev/mainul35/fruits/FruitResource.java](#srcmainjavadevmainul35fruitsfruitresourcejava)
  - [Testing Files](#testing-files)
- [How to Run](#how-to-run)
- [Troubleshooting](#troubleshooting)

---

## Project Structure
```
├── README.md
├── mysql.docker-compose.yml
├── pom.xml
├── sql_scripts/
│ └── init.sql
├── src/
│ ├── main/
│ │ ├── java/dev/mainul35/fruits/
│ │ │ ├── Fruit.java
│ │ │ └── FruitResource.java
│ │ └── resources/application.properties
│ └── test/java/dev/mainul35/
│ ├── GreetingResourceIT.java
│ └── GreetingResourceTest.java
```
---

## Dependency and Code Explanations

### `pom.xml`

**Purpose:**  
Defines all dependencies, plugins, and build configuration for the project.

#### Key Dependencies

- **Quarkus BOM**:  
  Ensures all Quarkus dependencies are compatible (`quarkus-bom`).

- **RESTEasy Reactive**:  
  - `quarkus-resteasy-reactive`: Enables reactive REST endpoints.
  - `quarkus-resteasy-reactive-jackson`: JSON (de)serialization for REST.
  - `quarkus-vertx-http`: Underlying reactive HTTP server.

- **Hibernate Reactive Panache**:  
  - `quarkus-hibernate-reactive-panache`: Non-blocking ORM for database access.

- **Reactive MySQL Client**:  
  - `quarkus-reactive-mysql-client`: Non-blocking MySQL driver.

- **Scheduler**:  
  - `quarkus-scheduler`: (Optional) For scheduled jobs, not directly used in this demo but included for future extensibility.

- **Testing**:  
  - `quarkus-junit5`, `rest-assured`: For writing and running tests.

- **Build Plugins**:  
  - `quarkus-maven-plugin`: For building and running Quarkus apps.
  - `maven-compiler-plugin`, `maven-surefire-plugin`, `maven-failsafe-plugin`: Standard Maven build/test plugins.

#### Why these dependencies?

- **Reactive stack**: All database and HTTP operations are non-blocking, enabling high scalability.
- **Panache**: Simplifies entity and repository code.
- **MySQL**: Chosen as the demo database.
- **Testing**: Ensures endpoints work as expected.

---

### `mysql.docker-compose.yml` & `sql_scripts/init.sql`

**Purpose:**  
Provides a reproducible, isolated MySQL environment for local development.

#### Key Points

- **MySQL Service**:  
  - Exposes port `3308` (host) → `3306` (container) to avoid conflicts.
  - Uses a named volume for persistent data.
  - Mounts `sql_scripts/init.sql` to initialize the database and user.

- **init.sql**:  
  - Creates `fruits_db` database.
  - Creates user `fruits_db_user` with password `fruits_db_password`.
  - Grants all privileges on `fruits_db` to this user.

**Why?**  
- Ensures the database and user are always present and ready for the Quarkus app.
- Avoids manual DB setup.

---

### `src/main/resources/application.properties`

**Purpose:**  
Configures Quarkus to connect to the MySQL database using the reactive driver.

#### Key Properties

- `quarkus.http.port=8050`:  
  Runs the app on port 8050 (not default 8080).

- `quarkus.datasource.devservices.enabled=false`:  
  Disables Quarkus DevServices (since we use our own Docker DB).

- `quarkus.datasource.db-kind=mysql`:  
  Specifies MySQL as the DB type.

- `quarkus.datasource.username` / `password`:  
  Matches the user created in `init.sql`.

- `quarkus.datasource.reactive.url`:  
  Connects to MySQL on port 3308, database `fruits_db`.

- `quarkus.hibernate-orm.database.generation=update`:  
  Automatically updates the schema to match entities.

- `quarkus.hibernate-orm.log.sql=true`:  
  Logs SQL statements for debugging.

**Why?**  
- Ensures the app connects to the correct DB, with the right user, and uses the reactive stack.

---

### `src/main/java/dev/mainul35/fruits/Fruit.java`

**Purpose:**  
Defines the `Fruit` entity, mapped to a database table.

#### Key Points

- `@Entity`:  
  Marks this as a JPA entity.

- `@Cacheable`:  
  Enables second-level caching (optional, for performance).

- `extends PanacheEntity`:  
  Inherits `id` field and CRUD helpers from Panache.

- `@Column(length = 40, unique = true)`:  
  - `name` is unique and max 40 chars.

**Why?**  
- Minimal entity for demo purposes.
- Panache reduces boilerplate (no getters/setters, no repository needed).

---

### `src/main/java/dev/mainul35/fruits/FruitResource.java`

**Purpose:**  
Exposes REST endpoints for CRUD operations on `Fruit`.

#### Key Endpoints

- `GET /fruits`:  
  List all fruits, sorted by name.

- `GET /fruits/{id}`:  
  Get a single fruit by ID.

- `POST /fruits`:  
  Create a new fruit.  
  - Validates that `id` is not set (should be auto-generated).

- `PUT /fruits/{id}`:  
  Update a fruit's name.

- `DELETE /fruits/{id}`:  
  Delete a fruit by ID.

#### Error Handling

- **Custom `ErrorMapper`**:  
  - Catches all exceptions and returns a JSON error response.
  - Handles `WebApplicationException` for validation errors.
  - Handles `CompositeException` from Mutiny (e.g., DB constraint violations).

**Why?**  
- Demonstrates reactive REST endpoints with non-blocking DB access.
- Shows how to handle errors in a user-friendly way.

---

### Testing Files

- `GreetingResourceTest.java`, `GreetingResourceIT.java`:  
  - Standard Quarkus test stubs.
  - You can add tests for `/fruits` endpoints here.

---

## How to Run

### 1. Start MySQL with Docker Compose

```bash
docker compose -f mysql.docker-compose.yml up -d
```
* This will start MySQL on port 3308 and initialize the database and user.

### 2. Build and Run the Quarkus Application
```
./mvnw clean compile quarkus:dev
```

or (if you have Maven installed)
```
mvn clean compile quarkus:dev
```

The app will start on http://localhost:8050.

### 3. Test the Endpoints

#### List Fruits:
```GET http://localhost:8050/fruits```

#### Add Fruit:
```POST http://localhost:8050/fruits```
Body:
```
{ "name": "Banana" }
```

#### Update Fruit:
```PUT http://localhost:8050/fruits/1```
Body:
```
{ "name": "Apple" }
```
#### Delete Fruit:
```
DELETE http://localhost:8050/fruits/1
```

## Troubleshooting
### * Port Conflicts:
Port Conflicts:

If port 3308 or 8050 is in use, change them in mysql.docker-compose.yml and application.properties.
### * Database Connection Issues:

Ensure MySQL is running and accessible on port 3308.
Check logs for authentication errors.
### * Schema Not Updating:
* The property quarkus.hibernate-orm.database.generation=update should auto-update the schema.
* For manual control, use validate or none.


