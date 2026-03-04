# Spring Boot + Spring Data JPA + Oracle

Spring Boot project example demonstrating CRUD operations with JPA and Oracle database.

## Java 8 Upgrade

This project has been upgraded to Java 8, incorporating the following changes:

### Build Configuration
- **Java version**: Explicitly configured for Java 8 (source and target 1.8) via `maven-compiler-plugin`
- **Oracle JDBC driver**: Upgraded from `ojdbc7` (Java 7) to `ojdbc8` (Java 8)
- **H2 database**: Added as a test dependency for running unit tests without Oracle

### Java 8 Language & API Enhancements
- **Optional**: `findById` methods now return `Optional<Car>` instead of nullable `Car`, providing safer null handling
- **Streams**: `CarDaoImpl.findAll()` uses Java 8 Streams API for concise data transformation (replaces manual for-each loop)
- **Lambda expressions**: Used throughout the controller and DAO for cleaner, more readable code
- **Method references**: Applied where applicable for improved readability
- **Collections improvements**: Uses `Collections.emptyList()` and `isEmpty()` instead of null returns and `size() > 0` checks
- **Shorthand annotations**: Controller uses `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` instead of verbose `@RequestMapping`

### Code Quality Improvements
- Replaced `e.printStackTrace()` with proper SLF4J logger calls with parameterized messages
- Constructor injection with `final` fields instead of field injection with `@Autowired`
- Added `toString()` method to `Car` entity using `String.format()`
- Removed ambiguous `save` method from `CarRepository` (inherits from `CrudRepository`)
- Added Javadoc comments to key classes and methods

### Testing
- Added comprehensive unit tests for `CarService`, `CarController`, and `CarDaoImpl` (31 tests total)
- Tests use Mockito for mocking and MockMvc for controller endpoint testing
- H2 in-memory database configured for test profile

## RESTful API Endpoints

```
POST    /api/cars           Create a new car
GET     /api/cars           Get all cars
GET     /api/cars/{id}      Get car by id
PUT     /api/cars/{id}      Update car by id
DELETE  /api/cars/{id}      Delete car by id
```

## Tech Stack

1. Spring Boot 1.5.8.RELEASE
2. Java 8
3. Oracle Database 11g Express (runtime) / H2 (testing)
4. Oracle JDBC driver ojdbc8
5. Lombok
6. Maven
7. Hibernate Core 5.0.12.Final

## Prerequisites

- **JDK 8** (1.8) or higher
- **Maven 3.x**
- **Oracle Database 11g Express** (for production use)

## Setup and Running

### 1. Clone the repository

```bash
git clone https://github.com/COG-GTM/spring-boot-jpa-oracle-project.git
cd spring-boot-jpa-oracle-project
```

### 2. Configure Oracle Database

Edit `src/main/resources/application.properties` to match your Oracle database setup:

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

The application will start on `http://localhost:8080`.

### 5. Run tests

```bash
mvn test
```

Tests use an in-memory H2 database and do not require Oracle to be running.

## Example API Usage

### CREATE
```bash
curl -X POST http://localhost:8080/api/cars \
  -H "Content-Type: application/json" \
  -d '{
    "carBrand": "MAZDA",
    "carModel": "SKYACTIV-G 2.0",
    "horsepower": "165",
    "carEngine": "2000"
  }'
```

### GET ALL
```bash
curl http://localhost:8080/api/cars
```

### GET BY ID
```bash
curl http://localhost:8080/api/cars/1
```

### UPDATE
```bash
curl -X PUT http://localhost:8080/api/cars/1 \
  -H "Content-Type: application/json" \
  -d '{
    "carBrand": "TOYOTA",
    "carModel": "Corolla Altis",
    "horsepower": "110",
    "carEngine": "1600"
  }'
```

### DELETE
```bash
curl -X DELETE http://localhost:8080/api/cars/1
```

---

By. Panusit Khuenkham (bamossza)
