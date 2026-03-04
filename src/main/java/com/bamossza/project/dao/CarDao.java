package com.bamossza.project.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bamossza.project.entities.Car;

/**
 * Data Access Object interface for Car entity.
 * Updated to use Java 8 Optional for nullable return types.
 */
public interface CarDao {

    Optional<Car> findById(int id);

    void remove(int id);

    void add(Car car);

    void update(int id, Car car);

    List<Map<String, Object>> findAll();
}
