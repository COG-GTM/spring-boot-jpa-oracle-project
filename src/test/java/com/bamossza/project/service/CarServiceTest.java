package com.bamossza.project.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

public class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarDao carDao;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById_Success() {
        int carId = 1;
        Car expectedCar = new Car("Toyota", "Corolla", "120", "1800");
        when(carDao.findById(carId)).thenReturn(expectedCar);

        Car result = carService.findById(carId);

        assertNotNull(result);
        assertEquals(expectedCar, result);
        verify(carDao, times(1)).findById(carId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_InvalidId() {
        carService.findById(0);
    }

    @Test
    public void testFindAll_Success() {
        List<Map<String, Object>> expectedList = new ArrayList<>();
        Map<String, Object> carMap = new HashMap<>();
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        carMap.put("1", car);
        expectedList.add(carMap);
        
        when(carDao.findAll()).thenReturn(expectedList);

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull(result);
        assertEquals(expectedList, result);
        verify(carDao, times(1)).findAll();
    }

    @Test
    public void testFindAll_EmptyList() {
        List<Map<String, Object>> emptyList = new ArrayList<>();
        when(carDao.findAll()).thenReturn(emptyList);

        List<Map<String, Object>> result = carService.findAll();

        assertNull(result);
        verify(carDao, times(1)).findAll();
    }

    @Test
    public void testAdd_Success() {
        Car car = new Car("Toyota", "Corolla", "120", "1800");

        carService.add(car);

        verify(carDao, times(1)).add(car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_NullCar() {
        carService.add(null);
    }

    @Test
    public void testUpdate_Success() {
        int carId = 1;
        Car car = new Car("Toyota", "Corolla", "120", "1800");

        carService.update(carId, car);

        verify(carDao, times(1)).update(carId, car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_InvalidIdAndNullCar() {
        carService.update(0, null);
    }

    @Test
    public void testRemove_Success() {
        int carId = 1;

        carService.remove(carId);

        verify(carDao, times(1)).remove(carId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_InvalidId() {
        carService.remove(0);
    }
}
