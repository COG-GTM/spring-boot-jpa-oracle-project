package com.bamossza.project.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    private ObjectMapper objectMapper;
    private Car car;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        car = new Car("Toyota", "Corolla", "150", "2.0");
    }

    @Test
    public void createReturnsOkAndDelegates() throws Exception {
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        verify(carService).add(any(Car.class));
    }

    @Test
    public void createReturnsBadRequestWhenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException()).when(carService).add(any(Car.class));

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllReturnsServiceList() throws Exception {
        List<Map<String, Object>> cars = Collections.<Map<String, Object>>singletonList(
                Collections.<String, Object>singletonMap("1", car));
        when(carService.findAll()).thenReturn(cars);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cars)));
    }

    @Test
    public void getByIdReturnsCarWhenFound() throws Exception {
        car.setCarId(1);
        when(carService.findById(1)).thenReturn(car);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(car)));
    }

    @Test
    public void getByIdReturnsNotFoundWhenServiceReturnsNull() throws Exception {
        when(carService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getByIdReturnsBadRequestWhenServiceThrows() throws Exception {
        when(carService.findById(1)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateReturnsOkAndDelegates() throws Exception {
        mockMvc.perform(put("/api/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        verify(carService).update(eq(1), any(Car.class));
    }

    @Test
    public void deleteReturnsOkAndDelegates() throws Exception {
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isOk());

        verify(carService).remove(1);
    }
}
