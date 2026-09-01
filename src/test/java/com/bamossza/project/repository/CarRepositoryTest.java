package com.bamossza.project.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bamossza.project.entities.Car;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    public void saveGeneratesIdAndFindsCarById() {
        Car car = new Car("Toyota", "Corolla", "150", "2.0");

        Car savedCar = carRepository.save(car);

        assertNotNull(savedCar.getCarId());
        Car foundCar = carRepository.findByCarId(savedCar.getCarId());
        assertNotNull(foundCar);
        assertEquals("Toyota", foundCar.getCarBrand());
        assertEquals("Corolla", foundCar.getCarModel());
        assertEquals("150", foundCar.getHorsepower());
        assertEquals("2.0", foundCar.getCarEngine());
    }

    @Test
    public void findAllReturnsSavedEntities() {
        carRepository.save(new Car("Toyota", "Corolla", "150", "2.0"));
        carRepository.save(new Car("Honda", "Civic", "140", "1.8"));

        List<Car> cars = carRepository.findAll();

        assertEquals(2, cars.size());
    }
}
