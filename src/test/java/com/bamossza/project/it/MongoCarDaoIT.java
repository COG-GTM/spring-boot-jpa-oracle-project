package com.bamossza.project.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.bamossza.project.entities.Car;
import com.bamossza.project.impl.MongoCarDaoImpl;
import com.bamossza.project.persistence.MongoCarSequence;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/** MongoCarDaoImpl against a real mongod, asserting the Oracle behaviors recorded in the baseline. */
public class MongoCarDaoIT {

    private MongoClient client;
    private MongoDatabase db;
    private MongoCarDaoImpl dao;

    @Before
    public void setUp() {
        client = RealServices.mongoClient();
        db = client.getDatabase(RealServices.mongoUri().getDatabase() + "_dao");
        db.drop();
        dao = new MongoCarDaoImpl(db);
    }

    @After
    public void tearDown() {
        db.drop();
        client.close();
    }

    private static Car car(String brand, String model) {
        return new Car(brand, model, "150", "2.0");
    }

    @Test
    public void addIgnoresClientIdAndAllocatesSequentialIds() {
        Car first = car("A", "one");
        first.setCarId(999);
        dao.add(first);
        dao.add(car("B", "two"));
        assertNull(dao.findById(999));
        assertEquals("one", dao.findById(1).getCarModel());
        assertEquals("two", dao.findById(2).getCarModel());
    }

    @Test
    public void emptyStringsAreStoredAsNull() {
        dao.add(new Car("", "m", "", null));
        Car stored = dao.findById(1);
        assertNull(stored.getCarBrand());
        assertNull(stored.getHorsepower());
    }

    @Test
    public void rejectedInsertStillConsumesAnId() {
        try {
            dao.add(car("A", "this model name is far longer than the fifty character column"));
            fail("expected overlength rejection");
        } catch (DataIntegrityViolationException expected) {
            // Oracle draws NEXTVAL before the insert fails.
        }
        dao.add(car("B", "ok"));
        assertNull(dao.findById(1));
        assertEquals("ok", dao.findById(2).getCarModel());
    }

    @Test
    public void putReplacesAllFieldsAndPutOnMissingIdInsertsNewId() {
        dao.add(car("A", "one"));
        dao.update(1, new Car(null, "renamed", null, null));
        Car replaced = dao.findById(1);
        assertNull(replaced.getCarBrand());
        assertEquals("renamed", replaced.getCarModel());
        assertNull(replaced.getHorsepower());

        dao.update(50, car("C", "via put"));
        assertNull(dao.findById(50));
        assertEquals("via put", dao.findById(2).getCarModel());
    }

    @Test
    public void failedPutLeavesRowUnchanged() {
        dao.add(car("A", "one"));
        try {
            dao.update(1, new Car("A", "one", "1234567", null));
            fail("expected overlength rejection");
        } catch (DataIntegrityViolationException expected) {
            // row must be untouched
        }
        assertEquals("150", dao.findById(1).getHorsepower());
    }

    @Test
    public void deleteOfMissingIdBurnsOneSequenceValue() {
        dao.add(car("A", "one"));
        dao.remove(77);
        dao.remove(1);
        dao.add(car("B", "two"));
        assertNull(dao.findById(1));
        assertEquals("two", dao.findById(3).getCarModel());
        assertEquals(3L, new MongoCarSequence(db).current());
    }

    @Test
    public void findAllReturnsLegacyKeyedShapeInIdOrder() {
        dao.add(car("A", "one"));
        dao.add(car("B", "two"));
        dao.add(car("C", "three"));
        dao.remove(2);
        List<Map<String, Object>> all = dao.findAll();
        assertEquals(2, all.size());
        assertEquals("one", ((Car) all.get(0).get("1")).getCarModel());
        assertEquals("three", ((Car) all.get(1).get("3")).getCarModel());
    }

    @Test
    public void sequenceRaiseNeverLowersCounter() {
        MongoCarSequence seq = new MongoCarSequence(db);
        seq.raiseTo(10);
        seq.raiseTo(4);
        assertEquals(10L, seq.current());
        assertEquals(11, seq.next());
    }
}
