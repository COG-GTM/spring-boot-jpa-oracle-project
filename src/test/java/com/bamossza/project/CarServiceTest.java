package com.bamossza.project;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;

/**
 * Unit tests for CarService.
 * Validates Java 8 Optional return types and service-layer logic.
 */
@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    @Mock
    private CarDao carDao;

    @InjectMocks
    private CarService carService;

    private Car testCar;

    @Before
    public void setUp() {
        testCar = new Car("TOYOTA", "Corolla", "110", "1600");
        testCar.setCarId(1);
    }

    @Test
    public void testFindById_ReturnsOptionalWithCar() {
        when(carDao.findById(1)).thenReturn(Optional.of(testCar));

        Optional<Car> result = carService.findById(1);

        assertTrue("Expected car to be present", result.isPresent());
        assertEquals("TOYOTA", result.get().getCarBrand());
        verify(carDao).findById(1);
    }

    @Test
    public void testFindById_ReturnsEmptyOptional() {
        when(carDao.findById(999)).thenReturn(Optional.empty());

        Optional<Car> result = carService.findById(999);

        assertFalse("Expected empty optional for non-existent car", result.isPresent());
        verify(carDao).findById(999);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_ThrowsExceptionForInvalidId() {
        carService.findById(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_ThrowsExceptionForNegativeId() {
        carService.findById(-1);
    }

    @Test
    public void testFindAll_ReturnsList() {
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("1", testCar);
        List<Map<String, Object>> expected = Arrays.asList(carMap);
        when(carDao.findAll()).thenReturn(expected);

        List<Map<String, Object>> result = carService.findAll();

        assertFalse("Expected non-empty result", result.isEmpty());
        assertEquals(1, result.size());
        verify(carDao).findAll();
    }

    @Test
    public void testFindAll_ReturnsEmptyListWhenNoResults() {
        when(carDao.findAll()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull("Result should never be null", result);
        assertTrue("Expected empty list", result.isEmpty());
    }

    @Test
    public void testFindAll_ReturnsEmptyListWhenNull() {
        when(carDao.findAll()).thenReturn(null);

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull("Result should never be null", result);
        assertTrue("Expected empty list", result.isEmpty());
    }

    @Test
    public void testAdd_Success() {
        carService.add(testCar);
        verify(carDao).add(testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_ThrowsExceptionForNull() {
        carService.add(null);
    }

    @Test
    public void testUpdate_Success() {
        carService.update(1, testCar);
        verify(carDao).update(1, testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_ThrowsExceptionForInvalidIdAndNullCar() {
        carService.update(0, null);
    }

    @Test
    public void testRemove_Success() {
        carService.remove(1);
        verify(carDao).remove(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_ThrowsExceptionForInvalidId() {
        carService.remove(0);
    }
}
