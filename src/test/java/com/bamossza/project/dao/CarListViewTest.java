package com.bamossza.project.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bamossza.project.entities.Car;

public class CarListViewTest {

    @Test
    public void keepsLegacyListOfSingleEntryMapsKeyedByStringId() {
        Car a = new Car("A", "x", null, null);
        a.setCarId(5);
        Car b = new Car("B", "y", null, null);
        b.setCarId(12);
        List<Map<String, Object>> view = CarListView.keyedById(Arrays.asList(a, b));
        assertEquals(2, view.size());
        assertEquals(1, view.get(0).size());
        assertSame(a, view.get(0).get("5"));
        assertSame(b, view.get(1).get("12"));
    }
}
