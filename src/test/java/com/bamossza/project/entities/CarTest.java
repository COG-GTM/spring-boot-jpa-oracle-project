package com.bamossza.project.entities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the Car entity.
 */
public class CarTest {

    @Test
    public void testDefaultConstructor() {
        Car car = new Car();
        assertNull(car.getCarId());
        assertNull(car.getCarBrand());
        assertNull(car.getCarModel());
        assertNull(car.getHorsepower());
        assertNull(car.getCarEngine());
    }

    @Test
    public void testParameterizedConstructor() {
        Car car = new Car("MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        assertEquals("MAZDA", car.getCarBrand());
        assertEquals("SKYACTIV-G 2.0", car.getCarModel());
        assertEquals("165", car.getHorsepower());
        assertEquals("2000", car.getCarEngine());
    }

    @Test
    public void testGettersAndSetters() {
        Car car = new Car();
        car.setCarId(1);
        car.setCarBrand("TOYOTA");
        car.setCarModel("Corolla");
        car.setHorsepower("110");
        car.setCarEngine("1600");

        assertEquals(Integer.valueOf(1), car.getCarId());
        assertEquals("TOYOTA", car.getCarBrand());
        assertEquals("Corolla", car.getCarModel());
        assertEquals("110", car.getHorsepower());
        assertEquals("1600", car.getCarEngine());
    }

    @Test
    public void testToStringContainsAllFields() {
        Car car = new Car("MAZDA", "CX-5", "165", "2000");
        car.setCarId(10);
        String str = car.toString();
        assertTrue(str.contains("Car["));
        assertTrue(str.contains("carId=10"));
        assertTrue(str.contains("carBrand='MAZDA'"));
        assertTrue(str.contains("carModel='CX-5'"));
        assertTrue(str.contains("horsepower='165'"));
        assertTrue(str.contains("carEngine='2000'"));
    }

    @Test
    public void testToStringWithNullFields() {
        Car car = new Car();
        String str = car.toString();
        assertTrue(str.contains("Car["));
        assertTrue(str.contains("carId=null"));
        assertTrue(str.contains("carBrand='null'"));
    }
}
