# Java 8 → 11 Migration Notes

## Summary

This project has been migrated from **Java 8** (JDK 1.8) to **Java 11** (LTS).

## Build Changes

| Component | Before | After |
|-----------|--------|-------|
| Java version | 1.8 | 11 |
| Spring Boot | 1.5.8.RELEASE | 2.7.18 |
| Oracle JDBC | `com.oracle:ojdbc7:12.1.0.2` | `com.oracle.database.jdbc:ojdbc11:21.9.0.0` |
| maven-compiler-plugin | (Spring Boot default) | 3.11.0 with `<release>11</release>` |
| maven-surefire-plugin | (Spring Boot default) | 3.2.5 |
| maven-failsafe-plugin | — | 3.2.5 |
| maven-enforcer-plugin | — | 3.5.0 (requires JDK 11+) |

## Removed JDK Module Replacements

Java 11 removed several Java EE modules from the JDK. External dependencies added:

- `javax.xml.bind:jaxb-api:2.3.1` — JAXB API
- `org.glassfish.jaxb:jaxb-runtime:2.3.9` — JAXB runtime implementation
- `javax.annotation:javax.annotation-api:1.3.2` — Common annotations (`@PostConstruct`, `@Resource`, etc.)

## Repository Changes

- **Removed** the `jahia` Maven repository (`http://maven.jahia.org/maven2/`). The old `com.oracle:ojdbc7` artifact required this HTTP repository; the replacement `ojdbc11` is published to Maven Central.

## Spring Boot 1.5 → 2.7 Compatibility

- Removed redundant `save(Car)` and `delete(Car)` method declarations from `CarRepository` — these conflicted with the updated `CrudRepository` generics in Spring Data JPA 2.x.
- Fixed malformed `spring.datasource.driver-class-oracle.jdbc.driver.OracleDriver` property to `spring.datasource.driver-class-name=oracle.jdbc.OracleDriver`.

## Encapsulation / Reflection

No illegal reflective access warnings observed. No `--add-opens` flags required.

## GC / Logging

- Default GC is **G1** on JDK 11 (was Parallel on JDK 8). No explicit GC flags are configured.
- No legacy GC logging flags to migrate.

## TLS / Security

- JDK 11 enables **TLS 1.3** by default. No TLS-specific configuration is used by this project.
- Default keystore type is now **PKCS12** (was JKS). No keystores are configured.

## CI

- Added `.github/workflows/java11-build.yml` — builds and verifies on JDK 11 (Temurin) via GitHub Actions.

## Known Issues

- No test classes exist in the project. Coverage is N/A.
- The application requires an Oracle database at `localhost:1521:xe` to start successfully.

## Follow-ups

- Consider adding unit/integration tests.
- Consider migrating from `javax.persistence` → `jakarta.persistence` (Spring Boot 3.x).
- Evaluate upgrading to Spring Boot 3.x + Java 17 in a future iteration.
