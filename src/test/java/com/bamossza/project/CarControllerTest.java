package com.bamossza.project;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.bamossza.project.controller.CarController;
import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for CarController.
 * Tests REST endpoints and verifies Java 8 Optional handling in responses.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    private Car testCar;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        testCar = new Car("MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        testCar.setCarId(1);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateCar_Success() throws Exception {
        String carJson = objectMapper.writeValueAsString(testCar);

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isOk());

        verify(carService).add(any(Car.class));
    }

    @Test
    public void testGetAllCars_Success() throws Exception {
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("1", testCar);
        when(carService.findAll()).thenReturn(Arrays.asList(carMap));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(carService).findAll();
    }

    @Test
    public void testGetAllCars_EmptyList() throws Exception {
        when(carService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());

        verify(carService).findAll();
    }

    /**
     * Verifies that the controller correctly uses Java 8 Optional.map()
     * to return a 200 response when a car is found.
     */
    @Test
    public void testGetById_Found() throws Exception {
        when(carService.findById(1)).thenReturn(Optional.of(testCar));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carBrand").value("MAZDA"))
                .andExpect(jsonPath("$.carModel").value("SKYACTIV-G 2.0"));

        verify(carService).findById(1);
    }

    /**
     * Verifies that the controller correctly uses Optional.orElse()
     * to return a 404 response when a car is not found.
     */
    @Test
    public void testGetById_NotFound() throws Exception {
        when(carService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().isNotFound());

        verify(carService).findById(999);
    }

    @Test
    public void testUpdateCar_Success() throws Exception {
        String carJson = objectMapper.writeValueAsString(testCar);

        mockMvc.perform(put("/api/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isOk());

        verify(carService).update(eq(1), any(Car.class));
    }

    @Test
    public void testDeleteCar_Success() throws Exception {
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isOk());

        verify(carService).remove(1);
    }
}
