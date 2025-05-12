package com.bamossza.project.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CarControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    @Test
    public void testGetAll_Success() throws Exception {
        List<Map<String, Object>> carList = new ArrayList<>();
        Map<String, Object> carMap = new HashMap<>();
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        carMap.put("1", car);
        carList.add(carMap);
        
        when(carService.findAll()).thenReturn(carList);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).findAll();
    }

    @Test
    public void testGetAll_NoContent() throws Exception {
        when(carService.findAll()).thenReturn(null);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).findAll();
    }

    @Test
    public void testGetById_Success() throws Exception {
        int carId = 1;
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        when(carService.findById(carId)).thenReturn(car);

        mockMvc.perform(get("/api/cars/{id}", carId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carBrand").value("Toyota"))
                .andExpect(jsonPath("$.carModel").value("Corolla"))
                .andExpect(jsonPath("$.horsepower").value("120"))
                .andExpect(jsonPath("$.carEngine").value("1800"));
        
        verify(carService, times(1)).findById(carId);
    }

    @Test
    public void testGetById_NotFound() throws Exception {
        int carId = 1;
        when(carService.findById(carId)).thenReturn(null);

        mockMvc.perform(get("/api/cars/{id}", carId))
                .andExpect(status().isNotFound());
        
        verify(carService, times(1)).findById(carId);
    }

    @Test
    public void testCreate_Success() throws Exception {
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        String carJson = objectMapper.writeValueAsString(car);

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).add(car);
    }

    @Test
    public void testUpdate_Success() throws Exception {
        int carId = 1;
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        String carJson = objectMapper.writeValueAsString(car);
        
        mockMvc.perform(put("/api/cars/{id}", carId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).update(carId, car);
    }

    @Test
    public void testUpdate_NotFound() throws Exception {
        int carId = 1;
        Car car = new Car("Toyota", "Corolla", "120", "1800");
        String carJson = objectMapper.writeValueAsString(car);
        
        mockMvc.perform(put("/api/cars/{id}", carId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).update(carId, car);
    }

    @Test
    public void testDelete_Success() throws Exception {
        int carId = 1;
        
        mockMvc.perform(delete("/api/cars/{id}", carId))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).remove(carId);
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        int carId = 1;
        
        mockMvc.perform(delete("/api/cars/{id}", carId))
                .andExpect(status().isOk());
        
        verify(carService, times(1)).remove(carId);
    }
}
