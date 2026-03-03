package com.bamossza.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

/**
 * Service layer for Car operations.
 * Uses Java 8 Optional for safe null handling and Collections utility methods.
 */
@Service
@Transactional
public class CarService {

    @Autowired
    private CarDao carDao;

    public CarService() {
    }

    /**
     * Find a car by ID using Java 8 Optional.
     *
     * @param id the car ID (must be positive)
     * @return an Optional containing the car if found
     * @throws IllegalArgumentException if id is not positive
     */
    public Optional<Car> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID cannot be 0 or < 0");
        }
        return carDao.findById(id);
    }

    public void remove(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID cannot be 0 or < 0 or this id do not exist");
        }
        carDao.remove(id);
    }

    /**
     * Retrieve all cars, returning an empty list instead of null when no results found.
     *
     * @return list of car maps, or empty list if none found
     */
    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> result = carDao.findAll();
        return (result != null && !result.isEmpty()) ? result : Collections.emptyList();
    }

    public void add(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("The passed object cannot be null.");
        }
        carDao.add(car);
    }

    public void update(int id, Car car) {
        if (id <= 0 && car == null) {
            throw new IllegalArgumentException("The passed object cannot be null.");
        }
        carDao.update(id, car);
    }
}
