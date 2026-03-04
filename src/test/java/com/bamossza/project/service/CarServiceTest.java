package com.bamossza.project.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

/**
 * Unit tests for {@link CarService}.
 * Verifies service-layer logic including Optional handling (Java 8).
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
    public void testFindById_found() {
        when(carDao.findById(1)).thenReturn(Optional.of(testCar));

        Optional<Car> result = carService.findById(1);

        assertTrue("Expected car to be present", result.isPresent());
        assertEquals("TOYOTA", result.get().getCarBrand());
        verify(carDao).findById(1);
    }

    @Test
    public void testFindById_notFound() {
        when(carDao.findById(99)).thenReturn(Optional.empty());

        Optional<Car> result = carService.findById(99);

        assertFalse("Expected empty optional", result.isPresent());
        verify(carDao).findById(99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_invalidId() {
        carService.findById(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_negativeId() {
        carService.findById(-1);
    }

    @Test
    public void testFindAll_withResults() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("1", testCar);
        mockList.add(map);

        when(carDao.findAll()).thenReturn(mockList);

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull(result);
        assertFalse("Expected non-empty result", result.isEmpty());
        assertEquals(1, result.size());
        verify(carDao).findAll();
    }

    @Test
    public void testFindAll_emptyResults() {
        when(carDao.findAll()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull("Result should not be null", result);
        assertTrue("Expected empty result", result.isEmpty());
        verify(carDao).findAll();
    }

    @Test
    public void testFindAll_nullResults() {
        when(carDao.findAll()).thenReturn(null);

        List<Map<String, Object>> result = carService.findAll();

        assertNotNull("Result should not be null even when DAO returns null", result);
        assertTrue("Expected empty result", result.isEmpty());
    }

    @Test
    public void testAdd_success() {
        carService.add(testCar);
        verify(carDao).add(testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_nullCar() {
        carService.add(null);
    }

    @Test
    public void testUpdate_success() {
        carService.update(1, testCar);
        verify(carDao).update(1, testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_invalidId() {
        carService.update(0, testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_nullCar() {
        carService.update(1, null);
    }

    @Test
    public void testRemove_success() {
        carService.remove(1);
        verify(carDao).remove(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_invalidId() {
        carService.remove(0);
    }
}
