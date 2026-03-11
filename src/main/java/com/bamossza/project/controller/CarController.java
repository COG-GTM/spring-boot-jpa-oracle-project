package com.bamossza.project.controller;

import java.util.Collections;
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
 * REST controller for managing Car entities.
 * Provides CRUD operations via RESTful endpoints.
 *
 * <p>Updated to use Java 8 features including Optional and
 * Spring 4.3+ shortcut mapping annotations.</p>
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

    /**
     * Creates a new car entry.
     *
     * @param car the car object to create
     * @return ResponseEntity with OK status on success, BAD_REQUEST on failure
     */
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Car car) {
        try {
            logger.info("Creating car: brand={}, model={}, engine={}, horsepower={}",
                    car.getCarBrand(), car.getCarModel(), car.getCarEngine(), car.getHorsepower());
            carService.add(car);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("Error creating car: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves all cars.
     *
     * @return ResponseEntity containing a list of all cars, or an empty list if none found
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        try {
            List<Map<String, Object>> result = Optional.ofNullable(carService.findAll())
                    .orElse(Collections.emptyList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error retrieving all cars: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves a car by its ID.
     *
     * @param id the car ID
     * @return ResponseEntity containing the car if found, NOT_FOUND otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getById(@PathVariable("id") int id) {
        try {
            return Optional.ofNullable(carService.findById(id))
                    .map(car -> ResponseEntity.ok(car))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            logger.error("Error retrieving car with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates an existing car by its ID.
     *
     * @param id  the car ID to update
     * @param car the updated car data
     * @return ResponseEntity with OK status on success, BAD_REQUEST on failure
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") int id, @RequestBody Car car) {
        try {
            carService.update(id, car);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("Error updating car with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Deletes a car by its ID.
     *
     * @param id the car ID to delete
     * @return ResponseEntity with OK status on success, BAD_REQUEST on failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        try {
            carService.remove(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("Error deleting car with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
