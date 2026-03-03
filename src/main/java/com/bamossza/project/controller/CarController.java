package com.bamossza.project.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bamossza.project.entities.Car;
import com.bamossza.project.service.CarService;

/**
 * REST controller for managing Car resources.
 * Uses Java 8 Optional for null-safe handling and composed annotations
 * ({@code @GetMapping}, {@code @PostMapping}, etc.) for cleaner request mappings.
 */
@RestController
@RequestMapping(value = "/api/cars")
public class CarController {

	private static final Logger logger = LoggerFactory.getLogger(CarController.class);
	
	private final CarService carService;

	@Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }
	
	@PostMapping
    public ResponseEntity<Void> create(@RequestBody Car car) {
        try {
        	logger.info("Creating car: brand={}, model={}, engine={}, hp={}",
        			car.getCarBrand(), car.getCarModel(), car.getCarEngine(), car.getHorsepower());
            carService.add(car);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("Error creating car: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        try {
        	List<Map<String, Object>> result = carService.findAll();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
        	logger.error("Error fetching all cars: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Car> getById(@PathVariable("id") int id) {
        try {
            Optional<Car> car = carService.findById(id);
            return car.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
        	logger.error("Error fetching car by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
	
	@PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") int id, @RequestBody Car car) {
        try {
            carService.update(id, car);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error updating car {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        try {
            carService.remove(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting car {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
