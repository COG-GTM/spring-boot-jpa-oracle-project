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

    @Mock
    private CarDao carDao;

    @InjectMocks
    private CarService carService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById() {
        int id = 1;
        Car car = new Car("Toyota", "Camry", "200", "2.5L");
        car.setCarId(id);
        
        when(carDao.findById(id)).thenReturn(car);
        
        Car result = carService.findById(id);
        
        assertNotNull(result);
        assertEquals(id, result.getCarId().intValue());
        assertEquals("Toyota", result.getCarBrand());
        verify(carDao, times(1)).findById(id);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdWithInvalidId() {
        carService.findById(0);
    }
    
    @Test
    public void testFindAll() {
        List<Map<String, Object>> carList = new ArrayList<>();
        Map<String, Object> carMap = new HashMap<>();
        Car car = new Car("Toyota", "Camry", "200", "2.5L");
        car.setCarId(1);
        carMap.put("1", car);
        carList.add(carMap);
        
        when(carDao.findAll()).thenReturn(carList);
        
        List<Map<String, Object>> result = carService.findAll();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carDao, times(1)).findAll();
    }
    
    @Test
    public void testFindAllEmpty() {
        List<Map<String, Object>> emptyList = new ArrayList<>();
        when(carDao.findAll()).thenReturn(emptyList);
        
        List<Map<String, Object>> result = carService.findAll();
        
        assertNull(result);
        verify(carDao, times(1)).findAll();
    }
    
    @Test
    public void testAdd() {
        Car car = new Car("Toyota", "Camry", "200", "2.5L");
        
        carService.add(car);
        
        verify(carDao, times(1)).add(car);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddWithNullCar() {
        carService.add(null);
    }
    
    @Test
    public void testUpdate() {
        int id = 1;
        Car car = new Car("Toyota", "Camry", "200", "2.5L");
        
        carService.update(id, car);
        
        verify(carDao, times(1)).update(id, car);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithInvalidIdAndNullCar() {
        carService.update(0, null);
    }
    
    @Test
    public void testRemove() {
        int id = 1;
        
        carService.remove(id);
        
        verify(carDao, times(1)).remove(id);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithInvalidId() {
        carService.remove(0);
    }
}
