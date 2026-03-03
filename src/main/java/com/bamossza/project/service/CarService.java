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
 * Uses Java 8 {@link Optional} for null-safe return values
 * and collection utilities for cleaner empty-checking.
 */
@Service
@Transactional
public class CarService {

	@Autowired
    private CarDao carDao;

    public CarService() {
    }

    /**
     * Finds a car by its ID, returning an {@link Optional} to avoid null returns.
     *
     * @param id the car ID (must be positive)
     * @return an Optional containing the car if found, or empty if not
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

    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> result = carDao.findAll();
        return result.isEmpty() ? Collections.emptyList() : result;
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
