package com.bamossza.project.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;
import com.bamossza.project.repository.CarRepository;

/**
 * Implementation of CarDao using Spring Data JPA repository.
 * Leverages Java 8 Optional and Stream API for cleaner data access.
 */
@Repository
public class CarDaoImpl implements CarDao {

    private static final Logger logger = LoggerFactory.getLogger(CarDaoImpl.class);

    @Autowired
    private CarRepository carRepository;

    public CarDaoImpl() {
    }

    /**
     * Find a car by its ID using Java 8 Optional.
     *
     * @param id the car ID
     * @return an Optional containing the car if found, or empty otherwise
     */
    @Override
    public Optional<Car> findById(int id) {
        try {
            return Optional.ofNullable(carRepository.findByCarId(id));
        } catch (Exception e) {
            logger.error("Error finding car by id {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void remove(int id) {
        try {
            Car car = new Car();
            car.setCarId(id);
            carRepository.delete(car);
        } catch (Exception e) {
            logger.error("Error removing car with id {}: {}", id, e.getMessage(), e);
        }
    }

    @Override
    public void add(Car car) {
        try {
            carRepository.save(car);
        } catch (Exception e) {
            logger.error("Error adding car: {}", e.getMessage(), e);
        }
    }

    @Override
    public void update(int id, Car car) {
        try {
            car.setCarId(id);
            carRepository.save(car);
        } catch (Exception e) {
            logger.error("Error updating car with id {}: {}", id, e.getMessage(), e);
        }
    }

    /**
     * Retrieve all cars using Java 8 Stream API to transform entities into maps.
     *
     * @return a list of maps containing car ID as key and Car object as value
     */
    @Override
    public List<Map<String, Object>> findAll() {
        try {
            return carRepository.findAll().stream()
                    .map(car -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put(car.getCarId().toString(), car);
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding all cars: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
