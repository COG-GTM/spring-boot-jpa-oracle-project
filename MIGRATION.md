# Oracle to MongoDB persistence migration

Scope: the car REST service in this repository. The HTTP API, controller, service and validation code
are unchanged; only the persistence layer gains a MongoDB implementation selected by Spring profile.

## Stack decisions

| Item | Value | Why |
|---|---|---|
| Spring Boot / Java | 1.5.8.RELEASE / Java 8 (unchanged) | No framework upgrade. The `java-17-upgrade*` branches are separate work and were not used. |
| Oracle driver | `ojdbc7` 12.1.0.2 from the Jahia repo, now over **https** | Maven 3.8+ blocks plain-http repositories, so the build failed before this change. |
| `CarRepository.save(Car)` override | removed | Redundant override made `save` ambiguous under javac 8 (`reference to save is ambiguous`). Inherited `CrudRepository.save` has identical behavior. |
| MongoDB driver | `mongodb.version` 3.12.14 (Boot 1.5.8 manages 3.4.3) | 3.4.3 only speaks the legacy `OP_QUERY` protocol, which MongoDB 6.0+ rejects (`Unsupported OP_QUERY command: find`). 3.12.x speaks `OP_MSG` and keeps the same API used by Spring Data MongoDB 1.10.8. |
| Oracle JVM flag | `-Doracle.jdbc.timezoneAsRegion=false` | Needed for ojdbc7 against XE 11g in UTC containers (`ORA-01882`). Unrelated to the migration. |

## Profiles

| Profile | Persistence | Notes |
|---|---|---|
| `mongodb` (default) | `MongoCarDaoImpl`, collection `car`, counter `counters/CAR_SEQ` | `spring.data.mongodb.uri` defaults to `mongodb://localhost:27017/carsystem`. JPA/JDBC auto-config is excluded. |
| `oracle` | original `CarDaoImpl` (JPA) | Same settings as before, including `ddl-auto=create-drop`. Use `--spring.jpa.hibernate.ddl-auto=none` to attach to an existing schema without dropping it. |
| `migrate` (with `mongodb`) | batch runner, no web server | Runs one migration direction, prints a JSON report, exit code 0 = reconciled, 2 = reconciliation failed. |

## Data mapping

| Oracle `CAR` | MongoDB `car` | Notes |
|---|---|---|
| `CAR_ID NUMBER(10)` | `_id` (int32) | IDs are preserved exactly. |
| `CAR_BRAND VARCHAR2(50 CHAR)` | `carBrand` | Explicit `null` is stored, never omitted. |
| `CAR_MODEL VARCHAR2(50 CHAR)` | `carModel` | |
| `HORSEPOWER VARCHAR2(6 CHAR)` | `horsepower` | String, not number (as in Oracle). |
| `CAR_ENGINE VARCHAR2(6 CHAR)` | `carEngine` | |
| `CAR_SEQ` (increment 1) | `counters` doc `{_id: "CAR_SEQ", seq}` | Atomic `$inc`. Migration only ever raises it (`$max`). |

## Preserved legacy semantics (observed on Oracle, reproduced on MongoDB)

These are recorded by the 38-step scenario in `migration/parity/scenario.json`. Several are unsafe or
surprising; they are preserved on purpose and flagged here rather than "fixed" during a persistence migration.

1. `GET /api/cars` returns a list of single-entry objects keyed by the ID as a string, e.g. `[{"3":{...}}]`.
2. `GET /api/cars` with no rows returns **200 with an empty body**, not `[]` (service turns an empty list into `null`).
3. `POST` ignores a client-supplied `carId`; the server always allocates the next sequence value. Unknown JSON fields are ignored.
4. Empty strings are stored as `null` (Oracle treats `''` as `NULL`).
5. Length limits count characters (code points), not bytes: brand/model 50, horsepower/engine 6. Overlength values return 400 and **still consume an ID**.
6. **Unsafe:** `PUT /api/cars/{id}` replaces every field (omitted fields become `null`). It is not a patch.
7. **Unsafe:** `PUT` on a missing ID returns 200 and inserts a new row under a **new** ID, not the one in the URL.
8. **Unsafe:** `PUT /api/cars/0` and negative IDs are accepted when a body is present (guard is `id <= 0 && car == null`), and behave like rule 7.
9. `DELETE` of a missing positive ID returns 200 and consumes one sequence value.
10. `GET`/`DELETE` with `id <= 0` return 400. Non-numeric or overflowing IDs return 400. Missing `GET` returns 404.
11. Malformed JSON and missing body return the Spring Boot 1.5 JSON error body with 400; wrong content type returns 415.

### Not identical / not covered

- **Ordering of `GET /api/cars`.** The JPA code issues `SELECT ... FROM CAR` without `ORDER BY`, so Oracle returns heap order
  (after updates, e.g. ID 146 came first in the seeded data set). MongoDB returns ascending ID. Contents are identical; order was never specified.
  `parity.py compare-snapshots` reports both checks and fails on order only with `--strict-order`.
- **Concurrency.** Oracle sequence allocation with `allocationSize=1` and MongoDB `$inc` are both atomic, but concurrent
  write interleavings were not load-tested.
- **Transactions.** Every MongoDB write touches one document plus the atomic counter, so the service's `@Transactional`
  boundaries become no-ops in the `mongodb` profile. There is no multi-document atomicity requirement in this API.
- **Sequence gaps** differ in absolute value only if Oracle's sequence cache was lost (instance restart). IDs are never reissued.

## Cutover runbook

Prerequisites: application writes frozen (or the app stopped) for the final sync, MongoDB 6.0+ reachable.

```bash
# 0. build
mvn -B clean package

# 1. dry check (no writes): compares Oracle and MongoDB, exit 2 if they differ
java -Doracle.jdbc.timezoneAsRegion=false -jar target/car-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=mongodb,migrate --migration.direction=reconcile \
  --migration.oracle.url=jdbc:oracle:thin:@HOST:1521:xe --migration.oracle.username=... --migration.oracle.password=... \
  --spring.data.mongodb.uri=mongodb://HOST:27017/carsystem --migration.report=reconcile.json

# 2. copy Oracle -> MongoDB (safe to run repeatedly while Oracle is still live; only differing rows are written)
...same command... --migration.direction=oracle-to-mongodb --migration.report=forward.json

# 3. freeze writes, run step 2 once more (final delta), check exit code 0 and "matched" in the report

# 4. start the service on MongoDB
java -jar target/car-0.0.1-SNAPSHOT.jar --spring.profiles.active=mongodb --spring.data.mongodb.uri=mongodb://HOST:27017/carsystem
```

What a run does: reads both stores fully, inserts missing rows, replaces rows whose fields differ, deletes target rows
absent from the source, carries the ID sequence forward (MongoDB counter raised to at least the probed Oracle sequence value
and the max ID), then re-reads both stores and reconciles count, ID sets, per-row fields and a SHA-256 checksum.
A second run with no source changes reports `inserted=0 updated=0 deleted=[]`.

Note: step 2 draws one value from `CAR_SEQ` to learn its position. That value is never issued by either store, so every forward run leaves a one-ID gap.

## Rollback

```bash
# stop the MongoDB-backed service, then copy MongoDB -> Oracle (writes made after cutover are carried back)
java -Doracle.jdbc.timezoneAsRegion=false -jar target/car-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=mongodb,migrate --migration.direction=mongodb-to-oracle ...same connection flags...

# restart on Oracle WITHOUT recreating the schema
java -Doracle.jdbc.timezoneAsRegion=false -jar target/car-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=oracle --spring.jpa.hibernate.ddl-auto=none
```

The rollback advances `CAR_SEQ` past the MongoDB counter (Oracle 11g has no `ALTER SEQUENCE ... RESTART`, so it temporarily
changes `INCREMENT BY`), so the next Oracle insert never collides with an ID issued on MongoDB.
Do not start the `oracle` profile with the default `create-drop` against production data.

## Verification

| What | Command |
|---|---|
| Unit tests (no services) | `mvn -B clean test` |
| Unit + integration tests on real Oracle XE and MongoDB | `mvn -B -Pit clean verify` (override with `IT_ORACLE_URL`, `IT_ORACLE_USERNAME`, `IT_ORACLE_PASSWORD`, `IT_MONGODB_URI`) |
| HTTP parity of both profiles vs committed Oracle baseline | `migration/parity/ci-parity.sh target/car-0.0.1-SNAPSHOT.jar target/parity` |
| Ad hoc transcript / seed / snapshot tools | `python3 migration/parity/parity.py {run,compare,seed,snapshot,compare-snapshots} --help` |

`migration/parity/baseline/oracle-baseline.json` was recorded from the pre-migration code (master plus the two build fixes
above) on Oracle XE 11.2.0.2, and reproduced byte-for-byte on a second run before any persistence change was made.
All fixtures are synthetic.
