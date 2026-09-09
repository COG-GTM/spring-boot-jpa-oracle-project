package com.bamossza.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CarController.class)
class CarControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CarService carService;

	private static Car sampleCar() {
		Car car = new Car("Toyota", "Supra", "340", "3.0L");
		car.setCarId(1);
		return car;
	}

	@Test
	void createReturnsOk() throws Exception {
		mockMvc.perform(post("/api/cars").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sampleCar()))).andExpect(status().isOk());

		then(carService).should().add(any(Car.class));
	}

	@Test
	void createReturnsBadRequestWhenServiceThrows() throws Exception {
		willThrow(new IllegalArgumentException("The passed object cannot be null.")).given(carService)
				.add(any(Car.class));

		mockMvc.perform(post("/api/cars").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sampleCar()))).andExpect(status().isBadRequest());
	}

	@Test
	void getAllReturnsOkWithBody() throws Exception {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("1", sampleCar());
		result.add(entry);
		given(carService.findAll()).willReturn(result);

		mockMvc.perform(get("/api/cars")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].1.carBrand").value("Toyota"));
	}

	@Test
	void getAllReturnsBadRequestWhenServiceThrows() throws Exception {
		given(carService.findAll()).willThrow(new NullPointerException());

		mockMvc.perform(get("/api/cars")).andExpect(status().isBadRequest());
	}

	@Test
	void getByIdReturnsOkWithCar() throws Exception {
		given(carService.findById(1)).willReturn(sampleCar());

		mockMvc.perform(get("/api/cars/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.carId").value(1))
				.andExpect(jsonPath("$.carModel").value("Supra"));
	}

	@Test
	void getByIdReturnsNotFoundWhenMissing() throws Exception {
		given(carService.findById(99)).willReturn(null);

		mockMvc.perform(get("/api/cars/99")).andExpect(status().isNotFound());
	}

	@Test
	void getByIdReturnsBadRequestForNonPositiveId() throws Exception {
		given(carService.findById(0)).willThrow(new IllegalArgumentException("ID cannot be 0 or < 0"));

		mockMvc.perform(get("/api/cars/0")).andExpect(status().isBadRequest());
	}

	@Test
	void updateReturnsOk() throws Exception {
		mockMvc.perform(put("/api/cars/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sampleCar()))).andExpect(status().isOk());

		then(carService).should().update(eq(1), any(Car.class));
	}

	@Test
	void updateReturnsBadRequestWhenServiceThrows() throws Exception {
		willThrow(new IllegalArgumentException()).given(carService).update(anyInt(), any(Car.class));

		mockMvc.perform(put("/api/cars/5").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sampleCar()))).andExpect(status().isBadRequest());
	}

	@Test
	void deleteReturnsOk() throws Exception {
		mockMvc.perform(delete("/api/cars/1")).andExpect(status().isOk());

		then(carService).should().remove(1);
	}

	@Test
	void deleteReturnsBadRequestForNonPositiveId() throws Exception {
		willThrow(new IllegalArgumentException("ID cannot be 0 or < 0 or this id do not exist")).given(carService)
				.remove(0);

		mockMvc.perform(delete("/api/cars/0")).andExpect(status().isBadRequest());
	}
}
