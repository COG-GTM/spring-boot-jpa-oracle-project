package com.bamossza.project.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

/**
 * Unit tests for {@link CarService}.
 * Verifies Java 8 Optional return types and service-layer validation.
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
        testCar = new Car("MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        testCar.setCarId(1);
    }

    @Test
    public void testFindById_returnsOptionalWithCar() {
        when(carDao.findById(1)).thenReturn(Optional.of(testCar));

        Optional<Car> result = carService.findById(1);

        assertTrue("Expected car to be present", result.isPresent());
        assertEquals("MAZDA", result.get().getCarBrand());
    }

    @Test
    public void testFindById_returnsEmptyOptionalWhenNotFound() {
        when(carDao.findById(99)).thenReturn(Optional.empty());

        Optional<Car> result = carService.findById(99);

        assertFalse("Expected empty Optional", result.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_throwsForNonPositiveId() {
        carService.findById(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindById_throwsForNegativeId() {
        carService.findById(-1);
    }

    @Test
    public void testAdd_delegatesToDao() {
        doNothing().when(carDao).add(testCar);

        carService.add(testCar);

        verify(carDao).add(testCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_throwsForNullCar() {
        carService.add(null);
    }

    @Test
    public void testRemove_delegatesToDao() {
        doNothing().when(carDao).remove(1);

        carService.remove(1);

        verify(carDao).remove(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_throwsForNonPositiveId() {
        carService.remove(0);
    }

    @Test
    public void testUpdate_delegatesToDao() {
        doNothing().when(carDao).update(1, testCar);

        carService.update(1, testCar);

        verify(carDao).update(1, testCar);
    }

    @Test
    public void testFindAll_returnsList() {
        Map<String, Object> map = new HashMap<>();
        map.put("1", testCar);
        List<Map<String, Object>> expected = Arrays.asList(map);
        when(carDao.findAll()).thenReturn(expected);

        List<Map<String, Object>> result = carService.findAll();

        assertFalse("Expected non-empty list", result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void testFindAll_returnsEmptyListWhenNoCars() {
        when(carDao.findAll()).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = carService.findAll();

        assertTrue("Expected empty list", result.isEmpty());
    }
}
