package com.bamossza.project;

import com.bamossza.project.entities.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateCar() throws Exception {
        Car car = new Car("MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllCars() throws Exception {
        Car car = new Car("TOYOTA", "Corolla", "110", "1600");
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetCarById() throws Exception {
        Car car = new Car("HONDA", "Civic", "158", "1500");
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn404ForMissingCar() throws Exception {
        mockMvc.perform(get("/api/cars/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteCar() throws Exception {
        Car car = new Car("BMW", "M3", "425", "3000");
        mockMvc.perform(post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isOk());
    }
}
