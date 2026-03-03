# Spring Boot + Spring Data JPA + Oracle

Spring Boot project example with JPA and Oracle database.

## Prerequisites

- **Java 8** (JDK 1.8 or later)
- **Maven 3.x**
- **Oracle Database** 11g Express or later
- **Oracle JDBC driver** ojdbc8 (included via Maven)

## Setup & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/COG-GTM/spring-boot-jpa-oracle-project.git
   cd spring-boot-jpa-oracle-project
   ```

2. **Configure the database connection** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
   spring.datasource.username=carsystem
   spring.datasource.password=carsystem
   ```

3. **Build the project:**
   ```bash
   mvn clean package
   ```

4. **Run the application:**
   ```bash
   java -jar target/car-0.0.1-SNAPSHOT.jar
   ```
   Or using Maven:
   ```bash
   mvn spring-boot:run
   ```

5. **Run the tests:**
   ```bash
   mvn test
   ```

## REST API Endpoints

| Method | URL              | Description         |
|--------|------------------|---------------------|
| POST   | /api/cars        | Create a new car    |
| GET    | /api/cars        | Get all cars        |
| GET    | /api/cars/{id}   | Get car by id       |
| PUT    | /api/cars/{id}   | Update car by id    |
| DELETE | /api/cars/{id}   | Delete car by id    |

## Technology Stack

1. Spring Boot 1.5.8.RELEASE
2. Java 8 (with lambdas, streams, Optional, method references)
3. Oracle Database 11g Express
4. Oracle JDBC driver ojdbc8 (Java 8 compatible)
5. Lombok
6. Maven
7. Hibernate Core 5.0.12.Final
8. JUnit 4 + Mockito (unit tests)

## Java 8 Features Used

- **Optional** - Null-safe return types in DAO and Service layers (`findById` returns `Optional<Car>`)
- **Streams API** - Collection processing in `CarDaoImpl.findAll()` using `stream()`, `map()`, and `collect()`
- **Method References** - Used in controller for cleaner code (e.g., `ResponseEntity::ok`)
- **Lambda Expressions** - Used throughout for concise functional-style code
- **Composed Annotations** - `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` instead of verbose `@RequestMapping`

## API Usage Examples

##### CREATE:
```
POST /api/cars
{
	"carBrand": "MAZDA",
    "carModel": "SKYACTIV-G 2.0",
    "horsepower": "165",
    "carEngine": "2000"
}
```

##### GET ALL:
```
GET /api/cars
```

##### GET BY ID:
```
GET /api/cars/1
```

##### UPDATE:
```
PUT /api/cars/1
{
	"carBrand": "TOYOTA",
    "carModel": "Corolla Altis",
    "horsepower": "110",
    "carEngine": "1600"
}
```

##### DELETE:
```
DELETE /api/cars/1
```

---

[Website](https://bamossza.com)

[Medium Blog](https://medium.com/@bamossza)

By. Panusit Khuenkham (bamossza)
