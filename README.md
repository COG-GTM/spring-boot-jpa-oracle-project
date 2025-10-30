# Spring Boot JPA Oracle Project

A RESTful API for managing car information, built with Spring Boot, Spring Data JPA, and Oracle Database persistence.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Contributing](#contributing)

## Overview

This project demonstrates a complete Spring Boot application implementing a car management system with full CRUD (Create, Read, Update, Delete) operations. The application uses Spring Data JPA for data persistence with an Oracle database backend and follows a clean layered architecture pattern.

## Features

- RESTful API for car management
- Complete CRUD operations
- Oracle Database integration
- Connection pooling with HikariCP
- Automatic table generation via Hibernate
- Clean layered architecture (Controller → Service → DAO → Repository)
- Uses Lombok to reduce boilerplate code

## Technology Stack

- **Spring Boot** 1.5.8.RELEASE
- **Java** 8
- **Maven** - Dependency management and build tool
- **Oracle Database** 11g Express Edition
- **Oracle JDBC Driver** ojdbc7 (12.1.0.2)
- **Hibernate** 5.0.12.Final - ORM framework
- **Lombok** - Reduces boilerplate code
- **HikariCP** - High-performance JDBC connection pool
- **SLF4J** - Logging facade

## Prerequisites

Before running this application, ensure you have the following installed:

- Java Development Kit (JDK) 8 or higher
- Apache Maven 3.x
- Oracle Database 11g Express Edition (or compatible version)
- Oracle user account with appropriate privileges (default: `carsystem/carsystem`)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/COG-GTM/spring-boot-jpa-oracle-project.git
cd spring-boot-jpa-oracle-project
```

### 2. Configure Database Connection

Update the database configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=carsystem
spring.datasource.password=carsystem
```

**Note:** The application uses `spring.jpa.hibernate.ddl-auto=create-drop`, which will automatically create tables on startup and drop them on shutdown. For production use, change this to `validate` or `update`.

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

### 5. Verify Installation

Once the application starts, you should see log messages indicating successful startup and mapped REST endpoints:

```
Mapped "{[/api/cars],methods=[POST]}" onto public ResponseEntity<Void> CarController.create(Car)
Mapped "{[/api/cars],methods=[GET]}" onto public ResponseEntity<List<Map<String, Object>>> CarController.getAll()
Mapped "{[/api/cars/{id}],methods=[GET]}" onto public ResponseEntity<Car> CarController.getById(int)
Mapped "{[/api/cars/{id}],methods=[PUT]}" onto public ResponseEntity<Void> CarController.update(int, Car)
Mapped "{[/api/cars/{id}],methods=[DELETE]}" onto public ResponseEntity<Void> CarController.delete(int)
```

## Project Structure

```
src/main/java/com/bamossza/project/
├── CarApplication.java          # Main Spring Boot application entry point
├── controller/
│   └── CarController.java       # REST API endpoints
├── service/
│   └── CarService.java          # Business logic layer
├── dao/
│   └── CarDao.java             # Data access interface
├── impl/
│   └── CarDaoImpl.java         # DAO implementation
├── repository/
│   └── CarRepository.java      # Spring Data JPA repository
└── entities/
    └── Car.java                # JPA entity representing a car

src/main/resources/
└── application.properties       # Application configuration
```

### Architecture Layers

1. **Controller Layer** (`CarController`) - Handles HTTP requests and responses
2. **Service Layer** (`CarService`) - Contains business logic and transaction management
3. **DAO Layer** (`CarDao`, `CarDaoImpl`) - Provides data access abstraction
4. **Repository Layer** (`CarRepository`) - Spring Data JPA interface for database operations

## API Endpoints

All endpoints are prefixed with `/api/cars`.

### Create a New Car

```http
POST /api/cars
Content-Type: application/json

{
  "carBrand": "MAZDA",
  "carModel": "SKYACTIV-G 2.0",
  "horsepower": "165",
  "carEngine": "2000"
}
```

**Response:** `200 OK` on success, `400 Bad Request` on error

### Get All Cars

```http
GET /api/cars
```

**Response:** JSON array of car objects

```json
[
  {
    "1": {
      "carId": 1,
      "carBrand": "MAZDA",
      "carModel": "SKYACTIV-G 2.0",
      "horsepower": "165",
      "carEngine": "2000"
    }
  }
]
```

### Get Car by ID

```http
GET /api/cars/{id}
```

**Response:** JSON car object or `404 Not Found` if car doesn't exist

```json
{
  "carId": 1,
  "carBrand": "MAZDA",
  "carModel": "SKYACTIV-G 2.0",
  "horsepower": "165",
  "carEngine": "2000"
}
```

### Update a Car

```http
PUT /api/cars/{id}
Content-Type: application/json

{
  "carBrand": "TOYOTA",
  "carModel": "Corolla Altis",
  "horsepower": "110",
  "carEngine": "1600"
}
```

**Response:** `200 OK` on success, `400 Bad Request` on error

### Delete a Car

```http
DELETE /api/cars/{id}
```

**Response:** `200 OK` on success, `400 Bad Request` on error

## Configuration

### Database Settings

The application uses Oracle Database with the following default configuration:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=carsystem
spring.datasource.password=carsystem
```

### Connection Pooling

HikariCP is configured with the following settings:

```properties
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.maximum-pool-size=5
```

### JPA/Hibernate Settings

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

**Important:** Change `spring.jpa.hibernate.ddl-auto` to `validate` or `update` for production environments to prevent data loss.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please feel free to:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a Pull Request

## Acknowledgments

_Originally written and maintained by Panusit Khuenkham (bamossza), with updates from contributors and [Devin](https://app.devin.ai)._

---

For more information, visit:
- [Website](https://bamossza.com)
- [Medium Blog](https://medium.com/@bamossza)
