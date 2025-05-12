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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    @Mock
    private CarDao carDao;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private List<Map<String, Object>> carList;

    @Before
    public void setUp() {
        testCar = new Car("Toyota", "Camry", "200", "2.5L");
        testCar.setCarId(1);

        carList = new ArrayList<>();
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("1", testCar);
        carList.add(carMap);
    }

    @Test
    public void testFindById() {
        when(carDao.findById(1)).thenReturn(testCar);

        Car result = carService.findById(1);

        assertNotNull(result);
        assertEquals(testCar.getCarId(), result.getCarId());
        assertEquals(testCar.getCarBrand(), result.getCarBrand());
        verify(carDao, times(1)).findById(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByIdWithInvalidId() {
        carService.findById(0);
    }

    @Test
    public void testFindAll() {
        when(carDao.findAll()).thenReturn(carList);

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAllEmpty() {
        when(carDao.findAll()).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = carService.findAll();

        assertNull(result);
    }

    @Test
    public void testAdd() {
        carService.add(testCar);

        verify(carDao, times(1)).add(testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWithNullCar() {
        carService.add(null);
    }

    @Test
    public void testUpdate() {
        carService.update(1, testCar);

        verify(carDao, times(1)).update(1, testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithInvalidIdAndNullCar() {
        carService.update(0, null);
    }

    @Test
    public void testRemove() {
        carService.remove(1);

        verify(carDao, times(1)).remove(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWithInvalidId() {
        carService.remove(0);
    }
}
