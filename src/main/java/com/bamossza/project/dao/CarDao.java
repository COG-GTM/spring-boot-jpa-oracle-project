package com.bamossza.project.dao;

import java.util.List;
import java.util.Optional;

import com.bamossza.project.dto.CarDto;
import com.bamossza.project.entities.Car;

public interface CarDao {

    public Optional<Car> findById(int id);

    public void remove(int id);

    public void add(Car car);

    public void update(int id, Car car);

    public List<CarDto> findAll();
}
