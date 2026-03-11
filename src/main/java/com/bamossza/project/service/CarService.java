package com.bamossza.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

/**
 * Service layer for Car operations.
 * Handles business logic and validation before delegating to the DAO layer.
 *
 * <p>Updated to use Java 8 features including Objects.requireNonNull()
 * and Collection.isEmpty() for cleaner validation.</p>
 */
@Service
@Transactional
public class CarService {

    private final CarDao carDao;

    @Autowired
    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    /**
     * Finds a car by its ID.
     *
     * @param id the car ID (must be positive)
     * @return the found Car, or null if not found
     * @throws IllegalArgumentException if id is not positive
     */
    public Car findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID cannot be 0 or < 0");
        }
        return carDao.findById(id);
    }

    /**
     * Removes a car by its ID.
     *
     * @param id the car ID (must be positive)
     * @throws IllegalArgumentException if id is not positive
     */
    public void remove(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID cannot be 0 or < 0 or this id do not exist");
        }
        carDao.remove(id);
    }

    /**
     * Retrieves all cars.
     *
     * @return list of car maps, or empty list if none found
     */
    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> result = carDao.findAll();
        if (!result.isEmpty()) {
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Adds a new car.
     *
     * @param car the car to add (must not be null)
     * @throws NullPointerException if car is null
     */
    public void add(Car car) {
        Objects.requireNonNull(car, "The passed object cannot be null.");
        carDao.add(car);
    }

    /**
     * Updates an existing car.
     *
     * @param id  the car ID (must be positive)
     * @param car the updated car data (must not be null)
     * @throws IllegalArgumentException if id is not positive
     * @throws NullPointerException     if car is null
     */
    public void update(int id, Car car) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
        Objects.requireNonNull(car, "The passed object cannot be null.");
        carDao.update(id, car);
    }
}
