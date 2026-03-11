package com.bamossza.project.service;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CarService.
 * Uses Mockito to mock the CarDao dependency.
 */
public class CarServiceTest {

    @Mock
    private CarDao carDao;

    @InjectMocks
    private CarService carService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById_validId() {
        Car car = new Car("MAZDA", "CX-5", "165", "2000");
        car.setCarId(1);
        when(carDao.findById(1)).thenReturn(car);

        Car result = carService.findById(1);
        assertNotNull(result);
        assertEquals("MAZDA", result.getCarBrand());
        verify(carDao, times(1)).findById(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_invalidId_zero() {
        carService.findById(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_invalidId_negative() {
        carService.findById(-1);
    }

    @Test
    public void testRemove_validId() {
        doNothing().when(carDao).remove(1);
        carService.remove(1);
        verify(carDao, times(1)).remove(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_invalidId() {
        carService.remove(0);
    }

    @Test
    public void testFindAll_withResults() {
        List<Map<String, Object>> mockResult = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("1", new Car("MAZDA", "CX-5", "165", "2000"));
        mockResult.add(map);
        when(carDao.findAll()).thenReturn(mockResult);

        List<Map<String, Object>> result = carService.findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAll_emptyResults() {
        when(carDao.findAll()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = carService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAdd_validCar() {
        Car car = new Car("TOYOTA", "Corolla", "110", "1600");
        doNothing().when(carDao).add(car);
        carService.add(car);
        verify(carDao, times(1)).add(car);
    }

    @Test(expected = NullPointerException.class)
    public void testAdd_nullCar() {
        carService.add(null);
    }

    @Test
    public void testUpdate_validIdAndCar() {
        Car car = new Car("TOYOTA", "Corolla", "110", "1600");
        doNothing().when(carDao).update(1, car);
        carService.update(1, car);
        verify(carDao, times(1)).update(1, car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_invalidId() {
        Car car = new Car("TOYOTA", "Corolla", "110", "1600");
        carService.update(0, car);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdate_nullCar() {
        carService.update(1, null);
    }
}
