package com.bamossza.project.dto;

import com.bamossza.project.entities.Car;

public record CarDto(
        Integer carId,
        String carBrand,
        String carModel,
        String horsepower,
        String carEngine) {

    public static CarDto from(Car car) {
        return new CarDto(
                car.getCarId(),
                car.getCarBrand(),
                car.getCarModel(),
                car.getHorsepower(),
                car.getCarEngine());
    }
}
