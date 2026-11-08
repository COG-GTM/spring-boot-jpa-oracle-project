package com.bamossza.project.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.bamossza.project.entities.Car;
import com.bamossza.project.impl.MongoCarDaoImpl;
import com.bamossza.project.migration.CarMigrationService;
import com.bamossza.project.migration.CarMigrationService.Direction;
import com.bamossza.project.migration.CarMigrationService.MigrationResult;
import com.bamossza.project.migration.CarReconciler;
import com.bamossza.project.migration.MongoCarStore;
import com.bamossza.project.migration.OracleCarStore;
import com.bamossza.project.persistence.CarDocuments;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/** Forward migration, reruns, drift repair, reconciliation and rollback against real Oracle XE and MongoDB. */
public class CarMigrationIT {

    private static final String[] BRANDS = {"Synthetic", "Example", "Sample \u00e9", "\u30c6\u30b9\u30c8", ""};

    private SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbc;
    private OracleCarStore oracle;
    private MongoClient client;
    private MongoDatabase db;
    private MongoCarStore mongo;
    private CarMigrationService service;

    @Before
    public void setUp() {
        dataSource = RealServices.oracleDataSource();
        jdbc = new JdbcTemplate(dataSource);
        RealServices.resetOracleSchema(jdbc);
        oracle = new OracleCarStore(jdbc);
        client = RealServices.mongoClient();
        db = client.getDatabase(RealServices.mongoUri().getDatabase() + "_migration");
        db.drop();
        mongo = new MongoCarStore(db);
        service = new CarMigrationService(oracle, mongo);
    }

    @After
    public void tearDown() {
        if (db != null) {
            db.drop();
        }
        if (client != null) {
            client.close();
        }
        if (dataSource != null) {
            dataSource.destroy();
        }
    }

    /** Synthetic Oracle rows with IDs drawn from CAR_SEQ, nulls, unicode and sequence gaps. */
    private List<Integer> seedOracle(int count) {
        Random rng = new Random(20261109L);
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String brand = BRANDS[rng.nextInt(BRANDS.length)];
            Car car = new Car(brand.isEmpty() ? null : brand, rng.nextInt(5) == 0 ? null : "Model-" + i,
                    rng.nextBoolean() ? String.valueOf(60 + rng.nextInt(400)) : null,
                    rng.nextInt(7) == 0 ? "\uD83D\uDD0B" : "1." + rng.nextInt(10));
            car.setCarId((int) oracle.nextSequenceValue());
            if (rng.nextInt(10) == 0) {
                oracle.nextSequenceValue();
            }
            cars.add(car);
        }
        oracle.upsertAll(cars);
        List<Integer> ids = new ArrayList<>();
        for (Car c : cars) {
            ids.add(c.getCarId());
        }
        return ids;
    }

    @Test
    public void forwardMigrationIsIdempotentAndNeverReissuesIds() {
        List<Integer> ids = seedOracle(120);
        int maxOracleId = Collections.max(ids);

        MigrationResult first = service.run(Direction.ORACLE_TO_MONGODB);
        assertTrue(first.isSuccessful());
        assertEquals(120, first.inserted);
        assertEquals(120, first.reconciliation.targetCount);

        MigrationResult second = service.run(Direction.ORACLE_TO_MONGODB);
        assertTrue(second.isSuccessful());
        assertEquals(0, second.inserted);
        assertEquals(0, second.updated);
        assertEquals(0, second.deleted.size());
        assertEquals(120, second.unchanged);
        assertEquals(first.reconciliation.targetChecksum, second.reconciliation.targetChecksum);

        new MongoCarDaoImpl(db).add(new Car("After", "cutover", null, null));
        SortedMap<Integer, Car> rows = mongo.readAll();
        assertTrue(rows.lastKey() > maxOracleId);
        assertTrue(rows.lastKey() > first.sequenceSourceNextValue);
    }

    @Test
    public void rerunRepairsTargetDrift() {
        List<Integer> ids = seedOracle(30);
        service.run(Direction.ORACLE_TO_MONGODB);
        int changed = ids.get(3);
        db.getCollection(CarDocuments.COLLECTION).updateOne(Filters.eq("_id", changed), Updates.set("carModel", "drifted"));
        db.getCollection(CarDocuments.COLLECTION).insertOne(CarDocuments.toDocument(withId(new Car("Stray", null, null, null), 900000)));
        db.getCollection(CarDocuments.COLLECTION).deleteOne(Filters.eq("_id", ids.get(5)));

        MigrationResult reconcile = service.run(Direction.RECONCILE);
        assertFalse(reconcile.isSuccessful());
        assertEquals(Arrays.asList(ids.get(5)), reconcile.reconciliation.missingInTarget);
        assertEquals(Arrays.asList(900000), reconcile.reconciliation.extraInTarget);
        assertEquals(Arrays.asList(changed), reconcile.reconciliation.fieldMismatches);
        assertEquals(30, mongo.readAll().size());

        MigrationResult repair = service.run(Direction.ORACLE_TO_MONGODB);
        assertTrue(repair.isSuccessful());
        assertEquals(1, repair.inserted);
        assertEquals(1, repair.updated);
        assertEquals(Arrays.asList(900000), repair.deleted);
    }

    @Test
    public void rollbackCopiesTargetWritesBackToOracleAndAdvancesSequence() {
        List<Integer> ids = seedOracle(40);
        service.run(Direction.ORACLE_TO_MONGODB);

        MongoCarDaoImpl dao = new MongoCarDaoImpl(db);
        dao.add(new Car("Written", "on mongodb", "99", "1.0"));
        dao.update(ids.get(0), new Car("Edited", null, null, null));
        dao.remove(ids.get(1));
        dao.remove(123456);
        long mongoCounter = mongo.sequence().current();

        MigrationResult rollback = service.run(Direction.MONGODB_TO_ORACLE);
        assertTrue(rollback.isSuccessful());
        assertEquals(1, rollback.inserted);
        assertEquals(1, rollback.updated);
        assertEquals(Arrays.asList(ids.get(1)), rollback.deleted);
        assertEquals(CarReconciler.checksum(mongo.readAll()), CarReconciler.checksum(oracle.readAll()));
        assertTrue(oracle.nextSequenceValue() > mongoCounter);

        MigrationResult rerun = service.run(Direction.MONGODB_TO_ORACLE);
        assertTrue(rerun.isSuccessful());
        assertEquals(0, rerun.inserted + rerun.updated + rerun.deleted.size());
    }

    private static Car withId(Car car, int id) {
        car.setCarId(id);
        return car;
    }
}
