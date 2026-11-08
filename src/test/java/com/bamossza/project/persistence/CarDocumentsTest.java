package com.bamossza.project.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.bson.Document;
import org.junit.Test;

import com.bamossza.project.entities.Car;
import com.bamossza.project.migration.CarReconciler;

public class CarDocumentsTest {

    @Test
    public void roundTripPreservesIdNullsAndUnicode() {
        Car car = new Car("Synthetic \u00e9", null, "\uD83D\uDD0B", "2.0");
        car.setCarId(42);
        Document doc = CarDocuments.toDocument(car);
        assertEquals(42, doc.get("_id"));
        assertTrue(doc.containsKey("carModel"));
        assertNull(doc.get("carModel"));
        assertTrue(CarReconciler.sameFields(car, CarDocuments.toCar(doc)));
    }
}
