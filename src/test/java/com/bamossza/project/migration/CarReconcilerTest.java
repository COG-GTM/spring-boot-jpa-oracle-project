package com.bamossza.project.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.bamossza.project.entities.Car;

public class CarReconcilerTest {

    private static Car car(int id, String brand, String model) {
        Car car = new Car(brand, model, "100", "1.6");
        car.setCarId(id);
        return car;
    }

    private static SortedMap<Integer, Car> rows(Car... cars) {
        SortedMap<Integer, Car> map = new TreeMap<>();
        for (Car c : cars) {
            map.put(c.getCarId(), c);
        }
        return map;
    }

    @Test
    public void identicalStoresMatch() {
        ReconciliationReport r = CarReconciler.compare("a", rows(car(1, "A", "x"), car(3, "B", null)),
                "b", rows(car(1, "A", "x"), car(3, "B", null)));
        assertTrue(r.isMatched());
        assertEquals(r.sourceChecksum, r.targetChecksum);
        assertEquals(2, r.sourceCount);
    }

    @Test
    public void reportsMissingExtraAndFieldMismatchesById() {
        ReconciliationReport r = CarReconciler.compare("a", rows(car(1, "A", "x"), car(2, "B", "y"), car(3, "C", "z")),
                "b", rows(car(1, "A", "x"), car(3, "C", "changed"), car(9, "X", "x")));
        assertFalse(r.isMatched());
        assertEquals(Arrays.asList(2), r.missingInTarget);
        assertEquals(Arrays.asList(9), r.extraInTarget);
        assertEquals(Arrays.asList(3), r.fieldMismatches);
        assertNotEquals(r.sourceChecksum, r.targetChecksum);
    }

    @Test
    public void checksumDistinguishesNullFromEmptyString() {
        assertNotEquals(CarReconciler.checksum(rows(car(1, null, "x"))), CarReconciler.checksum(rows(car(1, "", "x"))));
        assertFalse(CarReconciler.sameFields(car(1, null, "x"), car(1, "", "x")));
    }

    @Test
    public void checksumIsIndependentOfInsertionOrder() {
        SortedMap<Integer, Car> forward = rows(car(1, "A", "x"), car(2, "B", "y"));
        SortedMap<Integer, Car> reverse = rows(car(2, "B", "y"), car(1, "A", "x"));
        assertEquals(CarReconciler.checksum(forward), CarReconciler.checksum(reverse));
    }
}
