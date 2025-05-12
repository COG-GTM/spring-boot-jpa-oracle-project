package com.bamossza.project.entities;

import java.io.Serializable;

public class TestCar implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer carId;
    private String carBrand;
    private String carModel;
    private String horsepower;
    private String carEngine;
    
    public TestCar() {}
    
    public TestCar(String carBrand, String carModel, String horsepower, String carEngine) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.horsepower = horsepower;
        this.carEngine = carEngine;
    }
    
    public Integer getCarId() {
        return carId;
    }
    
    public void setCarId(Integer carId) {
        this.carId = carId;
    }
    
    public String getCarBrand() {
        return carBrand;
    }
    
    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
    
    public String getCarModel() {
        return carModel;
    }
    
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }
    
    public String getHorsepower() {
        return horsepower;
    }
    
    public void setHorsepower(String horsepower) {
        this.horsepower = horsepower;
    }
    
    public String getCarEngine() {
        return carEngine;
    }
    
    public void setCarEngine(String carEngine) {
        this.carEngine = carEngine;
    }
}
