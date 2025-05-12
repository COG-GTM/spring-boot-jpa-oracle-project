package com.bamossza.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    public void testFindById_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.findById(0);
        });
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

    @Test
    public void testAdd_NullCar() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.add(null);
        });
    }

    @Test
    public void testUpdate_Success() {
        int carId = 1;
        Car car = new Car("Toyota", "Corolla", "120", "1800");

        carService.update(carId, car);

        verify(carDao, times(1)).update(carId, car);
    }

    @Test
    public void testUpdate_InvalidIdAndNullCar() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.update(0, null);
        });
    }

    @Test
    public void testRemove_Success() {
        int carId = 1;

        carService.remove(carId);

        verify(carDao, times(1)).remove(carId);
    }

    @Test
    public void testRemove_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            carService.remove(0);
        });
    }
}
