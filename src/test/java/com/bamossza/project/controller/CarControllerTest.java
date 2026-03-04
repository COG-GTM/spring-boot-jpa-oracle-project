package com.bamossza.project.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

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
 * Tests REST endpoints with MockMvc, verifying Java 8 Optional integration.
 */
@RunWith(MockitoJUnitRunner.class)
public class CarControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

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
    public void testCreate_success() throws Exception {
        doNothing().when(carService).add(any(Car.class));

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isOk());

        verify(carService).add(any(Car.class));
    }

    @Test
    public void testCreate_failure() throws Exception {
        doThrow(new RuntimeException("DB error")).when(carService).add(any(Car.class));

        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAll_success() throws Exception {
        List<Map<String, Object>> carList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("1", testCar);
        carList.add(map);

        when(carService.findAll()).thenReturn(carList);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(carService).findAll();
    }

    @Test
    public void testGetAll_empty() throws Exception {
        when(carService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());

        verify(carService).findAll();
    }

    @Test
    public void testGetById_found() throws Exception {
        when(carService.findById(1)).thenReturn(Optional.of(testCar));

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carBrand").value("MAZDA"))
                .andExpect(jsonPath("$.carModel").value("SKYACTIV-G 2.0"));

        verify(carService).findById(1);
    }

    @Test
    public void testGetById_notFound() throws Exception {
        when(carService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cars/99"))
                .andExpect(status().isNotFound());

        verify(carService).findById(99);
    }

    @Test
    public void testUpdate_success() throws Exception {
        doNothing().when(carService).update(eq(1), any(Car.class));

        mockMvc.perform(put("/api/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isOk());

        verify(carService).update(eq(1), any(Car.class));
    }

    @Test
    public void testDelete_success() throws Exception {
        doNothing().when(carService).remove(1);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isOk());

        verify(carService).remove(1);
    }
}
