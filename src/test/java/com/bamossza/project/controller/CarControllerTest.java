package com.bamossza.project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;

@RunWith(MockitoJUnitRunner.class)
public class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private static Car car() {
        return new Car("Volvo", "V60", "197", "2.0L");
    }

    @Test
    public void createReturnsOkAndDelegatesToService() {
        Car car = car();

        ResponseEntity<Void> response = carController.create(car);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(carService).add(car);
    }

    @Test
    public void createReturnsBadRequestWhenServiceThrows() {
        Car car = car();
        doThrow(new IllegalArgumentException("nope")).when(carService).add(car);

        assertThat(carController.create(car).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllReturnsServiceResult() {
        Map<String, Object> entry = new HashMap<String, Object>();
        entry.put("1", car());
        List<Map<String, Object>> cars = new ArrayList<Map<String, Object>>();
        cars.add(entry);
        when(carService.findAll()).thenReturn(cars);

        ResponseEntity<List<Map<String, Object>>> response = carController.getAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(cars);
    }

    @Test
    public void getAllReturnsBadRequestWhenServiceThrows() {
        when(carService.findAll()).thenThrow(new RuntimeException("db down"));

        assertThat(carController.getAll().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getByIdReturnsCar() {
        Car car = car();
        when(carService.findById(5)).thenReturn(car);

        ResponseEntity<Car> response = carController.getById(5);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(car);
    }

    @Test
    public void getByIdReturnsNotFoundWhenServiceReturnsNull() {
        when(carService.findById(6)).thenReturn(null);

        ResponseEntity<Car> response = carController.getById(6);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void getByIdReturnsBadRequestForInvalidId() {
        when(carService.findById(0)).thenThrow(new IllegalArgumentException("ID cannot be 0 or < 0"));

        assertThat(carController.getById(0).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateReturnsOkAndDelegatesToService() {
        Car car = car();

        assertThat(carController.update(8, car).getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(carService).update(8, car);
    }

    @Test
    public void updateReturnsBadRequestWhenServiceThrows() {
        Car car = car();
        doThrow(new IllegalArgumentException("nope")).when(carService).update(0, car);

        assertThat(carController.update(0, car).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteReturnsOkAndDelegatesToService() {
        assertThat(carController.delete(2).getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(carService).remove(2);
    }

    @Test
    public void deleteReturnsBadRequestWhenServiceThrows() {
        doThrow(new IllegalArgumentException("nope")).when(carService).remove(0);

        assertThat(carController.delete(0).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
