package com.bamossza.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
import com.bamossza.project.repository.CarRepository;

/**
 * Unit tests for {@link CarDaoImpl}.
 * Verifies Java 8 stream-based findAll and Optional-based findById.
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
        testCar = new Car("TOYOTA", "Corolla", "110", "1600");
        testCar.setCarId(1);
    }

    @Test
    public void testFindById_returnsOptionalWithCar() {
        when(carRepository.findByCarId(1)).thenReturn(testCar);

        Optional<Car> result = carDaoImpl.findById(1);

        assertTrue("Car should be present", result.isPresent());
        assertEquals("TOYOTA", result.get().getCarBrand());
    }

    @Test
    public void testFindById_returnsEmptyOptionalWhenNull() {
        when(carRepository.findByCarId(99)).thenReturn(null);

        Optional<Car> result = carDaoImpl.findById(99);

        assertFalse("Expected empty Optional", result.isPresent());
    }

    @Test
    public void testFindAll_usesStreamsToMapCars() {
        Car car2 = new Car("HONDA", "Civic", "158", "2000");
        car2.setCarId(2);
        when(carRepository.findAll()).thenReturn(Arrays.asList(testCar, car2));

        List<Map<String, Object>> result = carDaoImpl.findAll();

        assertEquals(2, result.size());
        assertTrue("First entry should contain car ID as key",
                result.get(0).containsKey("1"));
        assertTrue("Second entry should contain car ID as key",
                result.get(1).containsKey("2"));
    }

    @Test
    public void testFindAll_returnsEmptyListWhenNoCars() {
        when(carRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        List<Map<String, Object>> result = carDaoImpl.findAll();

        assertTrue("Expected empty list", result.isEmpty());
    }

    @Test
    public void testAdd_savesCarToRepository() {
        when(carRepository.save(testCar)).thenReturn(testCar);

        carDaoImpl.add(testCar);

        verify(carRepository).save(testCar);
    }

    @Test
    public void testRemove_deletesCarFromRepository() {
        carDaoImpl.remove(1);

        verify(carRepository).delete(org.mockito.Matchers.any(Car.class));
    }

    @Test
    public void testUpdate_setsIdAndSaves() {
        Car updateCar = new Car("MAZDA", "CX-5", "187", "2500");

        carDaoImpl.update(1, updateCar);

        assertEquals(Integer.valueOf(1), updateCar.getCarId());
        verify(carRepository).save(updateCar);
    }
}
