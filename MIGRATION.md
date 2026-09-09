# Migration: Spring Boot 1.5.8 / Java 8 -> Spring Boot 3.3.13 / Java 17

This document describes the upgrade of this project from its original stack
(Spring Boot 1.5.8.RELEASE, Java 8, Hibernate 5.0, ojdbc7) to Spring Boot 3.3.13
on Java 17, and the pitfalls encountered along the way.

## Resulting stack

| | Before | After |
| --- | --- | --- |
| Spring Boot | 1.5.8.RELEASE | 3.3.13 |
| Spring Framework | 4.3.x | 6.1.21 |
| Spring Data JPA | 1.11.x | 3.3.13 |
| Java | 8 | 17 |
| Hibernate ORM | 5.0.12.Final | 6.5.3.Final |
| Persistence API | javax.persistence (JPA 2.1) | jakarta.persistence 3.1 |
| Oracle JDBC | com.oracle:ojdbc7:12.1.0.2 | com.oracle.database.jdbc:ojdbc11:21.9.0.0 |

## Why

Spring Boot 1.5 reached end of life in 2019 and receives no security fixes; the
same is true of Java 8 for most vendors' free support. Spring Boot 3.x requires
Java 17 as a baseline, so the JDK and framework upgrades had to happen together.
Boot 3 also brings the Jakarta EE 9+ namespace, Hibernate 6, and a Boot-managed
Oracle JDBC driver, which removes the need for the third-party Maven repository
the old build depended on.

## What changed

### 1. Build (`pom.xml`)

- Parent POM `spring-boot-starter-parent` 1.5.8.RELEASE -> 3.3.13.
- `java.version` 1.8 -> 17.
- Oracle driver `com.oracle:ojdbc7:12.1.0.2` -> `com.oracle.database.jdbc:ojdbc11`
  with no explicit version — the Boot BOM manages it (21.9.0.0).
- Removed the `jahia` repository (`http://maven.jahia.org/maven2/`). It was only
  there to resolve ojdbc7, which was never published to Maven Central. It is also
  plain HTTP, which modern Maven blocks by default and which is unsafe for
  dependency resolution; Oracle now publishes drivers to Maven Central, so the
  repository is no longer needed.
- Excluded Lombok from the repackaged fat jar via the `spring-boot-maven-plugin`
  configuration (Lombok is compile-only).

### 2. Jakarta namespace break

Jakarta EE 9 renamed the `javax.*` packages to `jakarta.*`. Spring Boot 3 /
Hibernate 6 only recognise annotations from the new namespace: an entity still
annotated with `javax.persistence.Entity` is silently not an entity, and the
application fails at startup when the repository is bootstrapped.

`Car` was updated accordingly:

```java
- import javax.persistence.Entity;
+ import jakarta.persistence.Entity;
```

(same for `Column`, `GeneratedValue`, `GenerationType`, `Id`, `SequenceGenerator`,
`Table`). The mapping itself — the `CAR` table and the `CAR_SEQ` sequence
generator — is unchanged.

### 3. Spring Data 3 `save` ambiguity

`CarRepository` declared its own `save` override to attach a transaction timeout:

```java
<S extends Car> S save(Car car);
```

In Spring Data 1.x this compiled. In Spring Data 3 `CrudRepository` declares
`<S extends T> S save(S entity)`, and the declaration above is neither a valid
override nor a valid overload of it — the erasures clash — so it fails to
compile. It was changed to match the inherited signature:

```java
@Transactional
<S extends Car> S save(S car);
```

Behaviour is identical; only the parameter type is narrowed to the type variable.

## Known caveat: ORA-01882 with ojdbc11 against Oracle 11g

The modern Oracle driver sends the client's timezone as a named region. Oracle
11g Express ships an older timezone file and rejects regions it does not know:

```
java.sql.SQLException: ORA-01882: timezone region not found
```

Start the application with the driver flag that sends a fixed GMT offset instead
of a region name:

```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Doracle.jdbc.timezoneAsRegion=false"
```

The same system property must be set when the packaged jar is run
(`java -Doracle.jdbc.timezoneAsRegion=false -jar target/car-0.0.1-SNAPSHOT.jar`).
This is a driver/database-version mismatch, not an application bug — it goes away
against a modern Oracle database.
