package com.bamossza.project.controller;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CarController.
 * Uses Mockito to mock the CarService dependency.
 */
public class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreate_success() {
        Car car = new Car("MAZDA", "CX-5", "165", "2000");
        doNothing().when(carService).add(car);

        ResponseEntity<Void> response = carController.create(car);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(carService, times(1)).add(car);
    }

    @Test
    public void testCreate_failure() {
        Car car = new Car("MAZDA", "CX-5", "165", "2000");
        doThrow(new RuntimeException("DB error")).when(carService).add(car);

        ResponseEntity<Void> response = carController.create(car);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetAll_withResults() {
        List<Map<String, Object>> mockResult = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("1", new Car("MAZDA", "CX-5", "165", "2000"));
        mockResult.add(map);
        when(carService.findAll()).thenReturn(mockResult);

        ResponseEntity<List<Map<String, Object>>> response = carController.getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAll_nullResult() {
        when(carService.findAll()).thenReturn(null);

        ResponseEntity<List<Map<String, Object>>> response = carController.getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testGetAll_failure() {
        when(carService.findAll()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<Map<String, Object>>> response = carController.getAll();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testGetById_found() {
        Car car = new Car("MAZDA", "CX-5", "165", "2000");
        car.setCarId(1);
        when(carService.findById(1)).thenReturn(car);

        ResponseEntity<Car> response = carController.getById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("MAZDA", response.getBody().getCarBrand());
    }

    @Test
    public void testGetById_notFound() {
        when(carService.findById(99)).thenReturn(null);

        ResponseEntity<Car> response = carController.getById(99);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetById_failure() {
        when(carService.findById(1)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<Car> response = carController.getById(1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdate_success() {
        Car car = new Car("TOYOTA", "Corolla", "110", "1600");
        doNothing().when(carService).update(1, car);

        ResponseEntity<Void> response = carController.update(1, car);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(carService, times(1)).update(1, car);
    }

    @Test
    public void testUpdate_failure() {
        Car car = new Car("TOYOTA", "Corolla", "110", "1600");
        doThrow(new RuntimeException("DB error")).when(carService).update(1, car);

        ResponseEntity<Void> response = carController.update(1, car);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDelete_success() {
        doNothing().when(carService).remove(1);

        ResponseEntity<Void> response = carController.delete(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(carService, times(1)).remove(1);
    }

    @Test
    public void testDelete_failure() {
        doThrow(new RuntimeException("DB error")).when(carService).remove(1);

        ResponseEntity<Void> response = carController.delete(1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
