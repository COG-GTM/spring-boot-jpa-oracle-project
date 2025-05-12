package com.bamossza.project.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CarTest {

    @Test
    public void testCarCreation() {
        String brand = "Toyota";
        String model = "Camry";
        String horsepower = "200";
        String engine = "2.5L";
        
        Car car = new Car(brand, model, horsepower, engine);
        
        assertNotNull(car);
        assertEquals(brand, car.carBrand);
        assertEquals(model, car.carModel);
        assertEquals(horsepower, car.horsepower);
        assertEquals(engine, car.carEngine);
    }
    
    @Test
    public void testCarSettersAndGetters() {
        Car car = new Car();
        String brand = "Honda";
        String model = "Civic";
        String horsepower = "180";
        String engine = "1.8L";
        Integer id = 1;
        
        car.carId = id;
        car.carBrand = brand;
        car.carModel = model;
        car.horsepower = horsepower;
        car.carEngine = engine;
        
        assertEquals(id, car.carId);
        assertEquals(brand, car.carBrand);
        assertEquals(model, car.carModel);
        assertEquals(horsepower, car.horsepower);
        assertEquals(engine, car.carEngine);
    }
}
