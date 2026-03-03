package com.bamossza.project;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bamossza.project.entities.Car;
import com.bamossza.project.impl.CarDaoImpl;
import com.bamossza.project.repository.CarRepository;

/**
 * Unit tests for CarDaoImpl.
 * Validates Java 8 Stream API usage in findAll() and Optional in findById().
 */
@RunWith(MockitoJUnitRunner.class)
public class CarDaoImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarDaoImpl carDaoImpl;

    private Car testCar;

    @Before
    public void setUp() {
        testCar = new Car("HONDA", "Civic", "158", "2000");
        testCar.setCarId(1);
    }

    /**
     * Verifies that findById wraps the result in Optional using Optional.ofNullable().
     */
    @Test
    public void testFindById_ReturnsOptionalWithCar() {
        when(carRepository.findByCarId(1)).thenReturn(testCar);

        Optional<Car> result = carDaoImpl.findById(1);

        assertTrue("Expected car to be present", result.isPresent());
        assertEquals("HONDA", result.get().getCarBrand());
    }

    @Test
    public void testFindById_ReturnsEmptyOptionalWhenNotFound() {
        when(carRepository.findByCarId(999)).thenReturn(null);

        Optional<Car> result = carDaoImpl.findById(999);

        assertFalse("Expected empty optional", result.isPresent());
    }

    @Test
    public void testFindById_ReturnsEmptyOptionalOnException() {
        when(carRepository.findByCarId(anyInt())).thenThrow(new RuntimeException("DB error"));

        Optional<Car> result = carDaoImpl.findById(1);

        assertFalse("Expected empty optional on error", result.isPresent());
    }

    /**
     * Verifies that findAll() uses Stream API to transform Car entities into maps.
     */
    @Test
    public void testFindAll_UsesStreamToTransform() {
        Car car2 = new Car("BMW", "320i", "180", "2000");
        car2.setCarId(2);
        when(carRepository.findAll()).thenReturn(Arrays.asList(testCar, car2));

        List<Map<String, Object>> result = carDaoImpl.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue("First map should contain car ID key", result.get(0).containsKey("1"));
        assertTrue("Second map should contain car ID key", result.get(1).containsKey("2"));
    }

    @Test
    public void testFindAll_ReturnsEmptyListWhenNoCars() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = carDaoImpl.findAll();

        assertNotNull("Result should never be null", result);
        assertTrue("Expected empty list", result.isEmpty());
    }

    @Test
    public void testFindAll_ReturnsEmptyListOnException() {
        when(carRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        List<Map<String, Object>> result = carDaoImpl.findAll();

        assertNotNull("Result should never be null on error", result);
        assertTrue("Expected empty list on error", result.isEmpty());
    }

    @Test
    public void testAdd_CallsRepositorySave() {
        carDaoImpl.add(testCar);
        verify(carRepository).save(testCar);
    }

    @Test
    public void testUpdate_SetsIdAndSaves() {
        Car updateCar = new Car("TOYOTA", "Camry", "200", "2500");
        carDaoImpl.update(5, updateCar);

        assertEquals(Integer.valueOf(5), updateCar.getCarId());
        verify(carRepository).save(updateCar);
    }

    @Test
    public void testRemove_CallsRepositoryDelete() {
        carDaoImpl.remove(1);
        verify(carRepository).delete(any(Car.class));
    }
}
