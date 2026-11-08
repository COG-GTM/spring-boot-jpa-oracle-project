package com.bamossza.project.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.bamossza.project.entities.Car;

/**
 * Rerunnable, idempotent sync between the Oracle CAR table and the MongoDB car collection.
 * Each run reads both sides, writes only rows that differ, removes rows absent from the source,
 * carries the ID sequence forward, and finishes with a full reconciliation.
 */
public class CarMigrationService {

    public enum Direction { ORACLE_TO_MONGODB, MONGODB_TO_ORACLE, RECONCILE }

    private final OracleCarStore oracle;
    private final MongoCarStore mongo;

    public CarMigrationService(OracleCarStore oracle, MongoCarStore mongo) {
        this.oracle = oracle;
        this.mongo = mongo;
    }

    public MigrationResult run(Direction direction) {
        long started = System.currentTimeMillis();
        MigrationResult result = new MigrationResult();
        result.direction = direction.name();
        SortedMap<Integer, Car> oracleRows = oracle.readAll();
        SortedMap<Integer, Car> mongoRows = mongo.readAll();

        if (direction == Direction.ORACLE_TO_MONGODB) {
            Plan plan = plan(oracleRows, mongoRows, result);
            mongo.upsertAll(plan.writes);
            mongo.deleteAll(result.deleted);
            long oracleNext = oracle.nextSequenceValue();
            long maxId = oracleRows.isEmpty() ? 0 : oracleRows.lastKey();
            // The probed value is consumed in Oracle; skip it so no ID drawn from CAR_SEQ is ever issued again.
            mongo.sequence().raiseTo(Math.max(oracleNext, maxId));
            result.sequenceSourceNextValue = oracleNext;
        } else if (direction == Direction.MONGODB_TO_ORACLE) {
            Plan plan = plan(mongoRows, oracleRows, result);
            oracle.upsertAll(plan.writes);
            oracle.deleteAll(result.deleted);
            long counter = mongo.sequence().current();
            long maxId = mongoRows.isEmpty() ? 0 : mongoRows.lastKey();
            oracle.advanceSequencePast(Math.max(counter, maxId));
            result.sequenceSourceNextValue = counter + 1;
        }

        boolean forward = direction != Direction.MONGODB_TO_ORACLE;
        SortedMap<Integer, Car> oracleAfter = oracle.readAll();
        SortedMap<Integer, Car> mongoAfter = mongo.readAll();
        result.reconciliation = forward
                ? CarReconciler.compare("oracle", oracleAfter, "mongodb", mongoAfter)
                : CarReconciler.compare("mongodb", mongoAfter, "oracle", oracleAfter);
        result.mongoSequenceValue = mongo.sequence().current();
        long maxId = oracleAfter.isEmpty() ? 0 : oracleAfter.lastKey();
        result.sequenceCoversAllIds = result.mongoSequenceValue >= maxId;
        result.elapsedMillis = System.currentTimeMillis() - started;
        return result;
    }

    private static Plan plan(SortedMap<Integer, Car> source, SortedMap<Integer, Car> target, MigrationResult result) {
        Plan plan = new Plan();
        for (Car car : source.values()) {
            Car existing = target.get(car.getCarId());
            if (existing == null) {
                plan.writes.add(car);
                result.inserted++;
            } else if (!CarReconciler.sameFields(car, existing)) {
                plan.writes.add(car);
                result.updated++;
            } else {
                result.unchanged++;
            }
        }
        for (Integer id : target.keySet()) {
            if (!source.containsKey(id)) {
                result.deleted.add(id);
            }
        }
        return plan;
    }

    private static class Plan {
        final List<Car> writes = new ArrayList<>();
    }

    public static class MigrationResult {
        public String direction;
        public int inserted;
        public int updated;
        public int unchanged;
        public List<Integer> deleted = new ArrayList<>();
        public Long sequenceSourceNextValue;
        public long mongoSequenceValue;
        public boolean sequenceCoversAllIds;
        public long elapsedMillis;
        public ReconciliationReport reconciliation;

        public boolean isSuccessful() {
            return reconciliation.isMatched() && sequenceCoversAllIds;
        }
    }
}
