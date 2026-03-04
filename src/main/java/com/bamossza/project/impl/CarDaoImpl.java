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
 * Implementation of {@link CarDao} using Spring Data JPA.
 * Updated to use Java 8 features: Optional, streams, and method references.
 */
@Repository
public class CarDaoImpl implements CarDao {

    private static final Logger logger = LoggerFactory.getLogger(CarDaoImpl.class);

    private final CarRepository carRepository;

    @Autowired
    public CarDaoImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

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
     * Retrieves all cars and returns them as a list of maps.
     * Uses Java 8 streams for concise data transformation.
     *
     * @return list of maps containing car data, or empty list on error
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
