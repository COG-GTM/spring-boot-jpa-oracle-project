# Spring Boot + Spring Data JPA + Oracle

Spring Boot project example with Java 8 enhancements.

## Prerequisites

- **Java 8** (JDK 1.8) or higher
- **Maven 3.x**
- **Oracle Database 11g Express** (for production) or **H2** (used automatically for tests)

## Setup and Running

### 1. Clone the repository
```bash
git clone https://github.com/COG-GTM/spring-boot-jpa-oracle-project.git
cd spring-boot-jpa-oracle-project
```

### 2. Configure Oracle Database
Edit `src/main/resources/application.properties` with your Oracle connection details:
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
The application starts on `http://localhost:8080`.

### 5. Run tests
Tests use an embedded H2 database and do not require Oracle:
```bash
mvn test
```

## REST API Endpoints

```
POST    /api/cars           Create a car
GET     /api/cars           Get all cars
GET     /api/cars/{id}      Get car by id
PUT     /api/cars/{id}      Update car by id
DELETE  /api/cars/{id}      Delete car by id
```

## Technology Stack

1. Spring Boot 1.5.8.RELEASE
2. Java 8 (with lambdas, streams, and Optional)
3. Oracle Database 11g Express
4. Oracle JDBC driver ojdbc8.jar (upgraded from ojdbc7 for Java 8 compatibility)
5. Lombok
6. Maven with maven-compiler-plugin (source/target 1.8)
7. Hibernate Core 5.0.12.Final
8. H2 Database (test scope)

## Java 8 Enhancements Applied

This project has been upgraded from Java 7-style code to leverage Java 8 features:

- **Optional**: `CarDao.findById()`, `CarService.findById()`, and `CarController.getById()` use `Optional<Car>` instead of returning `null`
- **Stream API**: `CarDaoImpl.findAll()` uses `stream().map().collect()` instead of imperative for-each loops
- **Lambda expressions**: Used with `Optional.map()` in the controller for cleaner response building
- **Parameterized logging**: Replaced `e.printStackTrace()` with SLF4J parameterized log messages (`logger.error("...", arg, e)`)
- **Collections utilities**: Return `Collections.emptyList()` instead of `null` for empty results

## API Examples

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

[Website](https://bamossza.com) | [Medium Blog](https://medium.com/@bamossza)

By. Panusit Khuenkham (bamossza)
