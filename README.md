# Spring Boot + Spring Data JPA + Oracle

Spring Boot project example demonstrating a RESTful CRUD API for managing cars, backed by Oracle database via JPA.

## Prerequisites

- **Java 8** (JDK 1.8) or higher
- **Maven 3.x**
- **Oracle Database 11g Express** (or compatible Oracle instance)

## Technology Stack

1. Spring Boot 1.5.8.RELEASE
2. Java 8
3. Oracle Database 11g Express
4. Oracle JDBC driver ojdbc8 (upgraded for Java 8 compatibility)
5. Lombok
6. Maven
7. Hibernate Core 5.0.12.Final
8. H2 Database (for testing)

## Setup & Configuration

### 1. Clone the repository

```bash
git clone https://github.com/COG-GTM/spring-boot-jpa-oracle-project.git
cd spring-boot-jpa-oracle-project
```

### 2. Configure Oracle database connection

Edit `src/main/resources/application.properties` to match your Oracle database settings:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=carsystem
spring.datasource.password=carsystem
```

### 3. Build the project

```bash
mvn clean package
```

### 4. Run the application

```bash
java -jar target/car-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on the default port (8080).

### 5. Run tests

```bash
mvn clean test
```

Unit tests use Mockito for mocking dependencies and do not require an Oracle database connection.

## REST API Endpoints

| Method | URL              | Description        |
|--------|------------------|--------------------|
| POST   | /api/cars        | Create a new car   |
| GET    | /api/cars        | Get all cars       |
| GET    | /api/cars/{id}   | Get car by ID      |
| PUT    | /api/cars/{id}   | Update car by ID   |
| DELETE | /api/cars/{id}   | Delete car by ID   |

### Example Requests

#### CREATE
```
POST /api/cars
{
    "carBrand": "MAZDA",
    "carModel": "SKYACTIV-G 2.0",
    "horsepower": "165",
    "carEngine": "2000"
}
```

#### GET ALL
```
GET /api/cars
```

#### GET BY ID
```
GET /api/cars/1
```

#### UPDATE
```
PUT /api/cars/1
{
    "carBrand": "TOYOTA",
    "carModel": "Corolla Altis",
    "horsepower": "110",
    "carEngine": "1600"
}
```

#### DELETE
```
DELETE /api/cars/1
```

## Java 8 Upgrade Notes

This project has been upgraded from Java 7 to Java 8. Key changes include:

- **Build configuration**: Updated `pom.xml` with `maven-compiler-plugin` targeting Java 8, explicit `maven.compiler.source` and `maven.compiler.target` properties
- **Oracle JDBC driver**: Upgraded from `ojdbc7` to `ojdbc8` for Java 8 compatibility
- **Lambda expressions**: Used in stream operations for collection processing (e.g., `CarDaoImpl.findAll()`)
- **Streams API**: Replaced traditional for-each loops with `Stream.map().collect()` for cleaner data transformation
- **Optional**: Used `Optional.ofNullable()` in controller methods for null-safe handling of return values
- **Objects.requireNonNull()**: Replaced manual null checks with Java 8 utility methods in service layer
- **StringJoiner**: Added `toString()` implementation using Java 8 `StringJoiner` in the `Car` entity
- **Spring 4.3+ annotations**: Replaced `@RequestMapping(method=...)` with `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- **Parameterized logging**: Replaced `e.printStackTrace()` with SLF4J parameterized logging
- **Repository cleanup**: Removed ambiguous method declarations from `CarRepository`, relying on inherited `JpaRepository` methods
- **Unit tests**: Added comprehensive unit tests for controller, service, and entity layers using JUnit and Mockito

## Author

[Website](https://bamossza.com)

[Medium Blog](https://medium.com/@bamossza)

By. Panusit Khuenkham (bamossza)
