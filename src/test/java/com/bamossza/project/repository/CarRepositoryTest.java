package com.bamossza.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.bamossza.project.entities.Car;

@DataJpaTest
@SpringJUnitConfig
@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Test
    public void testFindByCarId_Success() {
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        entityManager.persist(car);
        entityManager.flush();

        Car found = carRepository.findByCarId(car.getCarId());

        assertNotNull(found);
        assertEquals(car.getCarBrand(), found.getCarBrand());
        assertEquals(car.getCarModel(), found.getCarModel());
        assertEquals(car.getHorsepower(), found.getHorsepower());
        assertEquals(car.getCarEngine(), found.getCarEngine());
    }

    @Test
    public void testFindByCarId_NotFound() {
        Car found = carRepository.findByCarId(999);

        assertNull(found);
    }

    @Test
    public void testFindAll_Success() {
        Car car1 = new Car("Toyota", "Corolla", "120", "1800");
        Car car2 = new Car("Honda", "Civic", "130", "1600");
        entityManager.persist(car1);
        entityManager.persist(car2);
        entityManager.flush();

        List<Car> cars = carRepository.findAll();

        assertEquals(2, cars.size());
    }

    @Test
    public void testSave_Success() {
        Car car = new Car("Toyota", "Corolla", "120", "1800");

        Car saved = carRepository.save(car);

        assertNotNull(saved);
        assertNotNull(saved.getCarId());
        assertEquals(car.getCarBrand(), saved.getCarBrand());
        assertEquals(car.getCarModel(), saved.getCarModel());
        assertEquals(car.getHorsepower(), saved.getHorsepower());
        assertEquals(car.getCarEngine(), saved.getCarEngine());
    }

    @Test
    public void testDelete_Success() {
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        entityManager.persist(car);
        entityManager.flush();
        Integer carId = car.getCarId();

        carRepository.delete(car);
        Car found = carRepository.findByCarId(carId);

        assertNull(found);
    }
}
