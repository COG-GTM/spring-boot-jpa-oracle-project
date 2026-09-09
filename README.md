# Spring Boot + Spring Data JPA + Oracle
Spring Boot project example.

##### Restful Pattern:

```
POST 	/api/cars		Create
GET 	/api/cars		Get all
GET 	/api/cars/{id}		Get car by id
PUT 	/api/cars/{id}		Update car by id
DELETE 	/api/cars/{id}		Delete car by id
```
  
##### เครื่องมือที่ใช้:

1. Spring Boot 3.3.13
2. Java 17
3. Oracle database 11g express
4. Oracle JDBC driver ojdbc11 (com.oracle.database.jdbc:ojdbc11:21.9.0.0, managed by the Spring Boot BOM)
5. Lombok
6. Maven
7. Hibernate Core 6.5.3.Final (Jakarta Persistence 3.1)

##### Running locally:

Requirements: JDK 17 and Maven. Docker is used for the Oracle database.

1. Start Oracle XE:

```
docker run -d --name oracle-xe -p 1521:1521 -e ORACLE_PASSWORD=oracle -e APP_USER=carsystem -e APP_USER_PASSWORD=carsystem gvenzl/oracle-xe:11-slim
```

Wait until the container reports it is healthy (first start initializes the database and takes a few minutes). On later sessions just run `docker start oracle-xe`.

2. Build and run:

```
mvn clean package
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Doracle.jdbc.timezoneAsRegion=false"
```

The `-Doracle.jdbc.timezoneAsRegion=false` flag avoids `ORA-01882: timezone region not found` when ojdbc11 connects to Oracle 11g (see MIGRATION.md).

The app listens on port 8080 and recreates the `CAR` table and `CAR_SEQ` sequence on startup (`spring.jpa.hibernate.ddl-auto=create-drop`).

3. Call the API:

```
# Create
curl -i -X POST http://localhost:8080/api/cars \
  -H 'Content-Type: application/json' \
  -d '{"carBrand":"MAZDA","carModel":"SKYACTIV-G 2.0","horsepower":"165","carEngine":"2000"}'

# Get all
curl -i http://localhost:8080/api/cars

# Get by id
curl -i http://localhost:8080/api/cars/1

# Update by id
curl -i -X PUT http://localhost:8080/api/cars/1 \
  -H 'Content-Type: application/json' \
  -d '{"carBrand":"TOYOTA","carModel":"Corolla Altis","horsepower":"110","carEngine":"1600"}'

# Delete by id
curl -i -X DELETE http://localhost:8080/api/cars/1
```

Upgrading from the original Spring Boot 1.5 / Java 8 stack is documented in [MIGRATION.md](MIGRATION.md).


##### Project Run test:

```
...
2561-03-28 11:16:07 INFO  o.s.o.j.LocalContainerEntityManagerFactoryBean - Initialized JPA EntityManagerFactory for persistence unit 'default'
2561-03-28 11:16:08 INFO  o.s.w.s.m.m.a.RequestMappingHandlerAdapter - Looking for @ControllerAdvice: org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@1c72da34: startup date [Wed Mar 28 11:16:02 ICT 2018]; root of context hierarchy
2561-03-28 11:16:08 INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/api/cars/{id}],methods=[PUT]}" onto public org.springframework.http.ResponseEntity<java.lang.Void> com.bamossza.project.controller.CarController.update(int,com.bamossza.project.entities.Car)
2561-03-28 11:16:08 INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/api/cars/{id}],methods=[DELETE]}" onto public org.springframework.http.ResponseEntity<java.lang.Void> com.bamossza.project.controller.CarController.delete(int)
2561-03-28 11:16:08 INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/api/cars],methods=[POST]}" onto public org.springframework.http.ResponseEntity<java.lang.Void> com.bamossza.project.controller.CarController.create(com.bamossza.project.entities.Car)
2561-03-28 11:16:08 INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/api/cars/{id}],methods=[GET]}" onto public org.springframework.http.ResponseEntity<com.bamossza.project.entities.Car> com.bamossza.project.controller.CarController.getById(int)
2561-03-28 11:16:08 INFO  o.s.w.s.m.m.a.RequestMappingHandlerMapping - Mapped "{[/api/cars],methods=[GET]}" onto public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<java.lang.String, java.lang.Object>>> com.bamossza.project.controller.CarController.getAll()

```

##### After run auto create database:
![Image of runtest](https://www.bamossza.com/static/images/upload/20180328120845b4180m4785z8858.JPG)

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
![Image of runtest](https://www.bamossza.com/static/images/upload/20180328115352b3920m8718z70.JPG)

##### GET ALL:
```
GET /api/cars
```
![Image of runtest](https://www.bamossza.com/static/images/upload/20180328115929b866m8931z4900.JPG)


##### GET BY ID:
```
GET /api/cars/1
```
![Image of runtest](https://www.bamossza.com/static/images/upload/20180328120517b7695m503z4842.JPG)

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
![Image of runtest](https://www.bamossza.com/static/images/upload/20180328120424b3927m2155z3938.JPG)


##### DELETE:
```
DELETE /api/cars/1
```
![Image of runtest](https://www.bamossza.com/static/images/upload/20180328120616b6722m2141z8062.JPG)

# 

[Website](https://bamossza.com)

[Medium Blog](https://medium.com/@bamossza)

By. Panusit Khuenkham (bamossza)