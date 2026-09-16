package com.bamossza.project.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CarTest {

    @Test
    public void defaultConstructorLeavesAllFieldsNull() {
        Car car = new Car();

        assertThat(car.getCarId()).isNull();
        assertThat(car.getCarBrand()).isNull();
        assertThat(car.getCarModel()).isNull();
        assertThat(car.getHorsepower()).isNull();
        assertThat(car.getCarEngine()).isNull();
    }

    @Test
    public void allArgsConstructorPopulatesFields() {
        Car car = new Car("Toyota", "Supra", "340", "3.0L");

        assertThat(car.getCarId()).isNull();
        assertThat(car.getCarBrand()).isEqualTo("Toyota");
        assertThat(car.getCarModel()).isEqualTo("Supra");
        assertThat(car.getHorsepower()).isEqualTo("340");
        assertThat(car.getCarEngine()).isEqualTo("3.0L");
    }

    @Test
    public void settersUpdateFields() {
        Car car = new Car();

        car.setCarId(7);
        car.setCarBrand("Honda");
        car.setCarModel("Civic");
        car.setHorsepower("158");
        car.setCarEngine("2.0L");

        assertThat(car.getCarId()).isEqualTo(7);
        assertThat(car.getCarBrand()).isEqualTo("Honda");
        assertThat(car.getCarModel()).isEqualTo("Civic");
        assertThat(car.getHorsepower()).isEqualTo("158");
        assertThat(car.getCarEngine()).isEqualTo("2.0L");
    }
}
