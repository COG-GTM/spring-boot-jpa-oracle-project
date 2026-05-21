# Java 8 to 11 Migration Notes

## Summary

This project has been migrated from **Java 8** (Spring Boot 1.5.8) to **Java 11** (Spring Boot 2.7.18).

## Build Changes

| Component | Before | After |
|-----------|--------|-------|
| Java version | 1.8 | 11 |
| Spring Boot | 1.5.8.RELEASE | 2.7.18 |
| Oracle JDBC | com.oracle:ojdbc7:12.1.0.2 | com.oracle.database.jdbc:ojdbc11:21.9.0.0 |
| maven-compiler-plugin | (inherited) | 3.8.1 with `<release>11</release>` |
| maven-surefire-plugin | (inherited) | 2.22.2 |
| maven-enforcer-plugin | (none) | 3.5.0 — requires JDK 11+ |

## Removed JDK Module Replacements

Java 11 removed several Java EE modules from the JDK. The following external dependencies were added:

- `javax.xml.bind:jaxb-api:2.3.1` — JAXB API
- `org.glassfish.jaxb:jaxb-runtime:2.3.9` — JAXB runtime implementation
- `javax.annotation:javax.annotation-api:1.3.2` — `@Generated`, `@PostConstruct`, etc.

## Repository Changes

- Removed the `jahia` Maven repository — the new `ojdbc11` driver is available on Maven Central.

## Code Changes

- `CarRepository.save()`: Updated method signature to match Spring Data JPA 2.x `CrudRepository.save(S entity)` to resolve ambiguity.

## CI

- Added GitHub Actions workflow (`.github/workflows/java11-build.yml`) targeting JDK 11 (Temurin) with Oracle XE service container.

## Encapsulation / Reflection

- No `--add-opens` flags required. All dependencies are compatible with Java 11 module encapsulation.

## GC / Logging

- Default GC is G1 (since Java 9). No custom GC flags were in use.
- No legacy GC logging flags to migrate.

## TLS / Security

- Java 11 enables TLS 1.3 by default. No TLS-specific configuration was present in this project.
- Default keystore type is PKCS12 (since Java 9). No keystore migration needed.

## Known Issues

- No test classes exist in this project. Test coverage is 0%.
- The `application.properties` contains a malformed driver class line (`spring.datasource.driver-class-oracle.jdbc.driver.OracleDriver`) which is a pre-existing issue. Spring Boot auto-detects the driver from the JDBC URL.

## Follow-up Recommendations

1. Add unit and integration tests for the REST API.
2. Consider migrating from `javax.*` to `jakarta.*` namespace if planning a future Spring Boot 3.x upgrade.
3. Consider adopting Java 11 language features (`var`, `HttpClient`, `Files.readString`).
