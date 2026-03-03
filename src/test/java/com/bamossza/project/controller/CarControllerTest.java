package com.bamossza.project.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link CarController}.
 * Validates REST endpoints, Java 8 Optional handling, and HTTP status codes.
 */
@RunWith(MockitoJUnitRunner.class)
public class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Car testCar;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
        objectMapper = new ObjectMapper();
        testCar = new Car("MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        testCar.setCarId(1);
    }

    @Test
    public void testCreateCar_returnsCreated() throws Exception {
        doNothing().when(carService).add(org.mockito.Matchers.any(Car.class));

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAll_returnsOk() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("1", testCar);
        List<Map<String, Object>> cars = Arrays.asList(map);
        when(carService.findAll()).thenReturn(cars);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAll_returnsBadRequestOnError() throws Exception {
        when(carService.findAll()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetById_returnsOk() throws Exception {
        when(carService.findById(1)).thenReturn(Optional.of(testCar));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carBrand").value("MAZDA"));
    }

    @Test
    public void testGetById_returnsNotFound() throws Exception {
        when(carService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCar_returnsOk() throws Exception {
        doNothing().when(carService).update(org.mockito.Matchers.eq(1), org.mockito.Matchers.any(Car.class));

        mockMvc.perform(put("/api/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCar_returnsOk() throws Exception {
        doNothing().when(carService).remove(1);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCar_returnsBadRequestOnError() throws Exception {
        doThrow(new RuntimeException("DB error")).when(carService).remove(1);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isBadRequest());
    }
}
