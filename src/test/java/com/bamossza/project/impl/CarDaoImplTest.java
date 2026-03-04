package com.bamossza.project.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bamossza.project.entities.Car;
import com.bamossza.project.repository.CarRepository;

/**
 * Unit tests for {@link CarDaoImpl}.
 * Verifies DAO layer including Java 8 Optional and stream-based findAll.
 */
@RunWith(MockitoJUnitRunner.class)
public class CarDaoImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarDaoImpl carDao;

    private Car testCar;

    @Before
    public void setUp() {
        testCar = new Car("HONDA", "Civic", "158", "2000");
        testCar.setCarId(1);
    }

    @Test
    public void testFindById_found() {
        when(carRepository.findByCarId(1)).thenReturn(testCar);

        Optional<Car> result = carDao.findById(1);

        assertTrue("Expected car to be present", result.isPresent());
        assertEquals("HONDA", result.get().getCarBrand());
        verify(carRepository).findByCarId(1);
    }

    @Test
    public void testFindById_notFound() {
        when(carRepository.findByCarId(99)).thenReturn(null);

        Optional<Car> result = carDao.findById(99);

        assertFalse("Expected empty optional", result.isPresent());
        verify(carRepository).findByCarId(99);
    }

    @Test
    public void testFindById_exception() {
        when(carRepository.findByCarId(1)).thenThrow(new RuntimeException("DB error"));

        Optional<Car> result = carDao.findById(1);

        assertFalse("Expected empty optional on exception", result.isPresent());
    }

    @Test
    public void testFindAll_withResults() {
        Car car2 = new Car("TOYOTA", "Camry", "203", "2500");
        car2.setCarId(2);
        List<Car> cars = Arrays.asList(testCar, car2);

        when(carRepository.findAll()).thenReturn(cars);

        List<Map<String, Object>> result = carDao.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify stream-based transformation produced correct keys
        assertTrue("Expected key '1'", result.get(0).containsKey("1"));
        assertTrue("Expected key '2'", result.get(1).containsKey("2"));
        verify(carRepository).findAll();
    }

    @Test
    public void testFindAll_empty() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = carDao.findAll();

        assertNotNull(result);
        assertTrue("Expected empty list", result.isEmpty());
        verify(carRepository).findAll();
    }

    @Test
    public void testFindAll_exception() {
        when(carRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        List<Map<String, Object>> result = carDao.findAll();

        assertNotNull("Should return empty list on exception, not null", result);
        assertTrue("Expected empty list on exception", result.isEmpty());
    }

    @Test
    public void testAdd() {
        carDao.add(testCar);
        verify(carRepository).save(testCar);
    }

    @Test
    public void testUpdate() {
        Car updatedCar = new Car("HONDA", "Civic Type R", "306", "2000");
        carDao.update(1, updatedCar);
        assertEquals(Integer.valueOf(1), updatedCar.getCarId());
        verify(carRepository).save(updatedCar);
    }

    @Test
    public void testRemove() {
        carDao.remove(1);
        verify(carRepository).delete(any(Car.class));
    }
}
