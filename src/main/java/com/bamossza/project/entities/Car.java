package com.bamossza.project.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CAR")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Car implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final Logger logger = LoggerFactory.getLogger(Car.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CAR_ID", unique=true, nullable=false)
	@Getter @Setter
	private Integer carId;
    
	@Column(name="CAR_BRAND", nullable = true, length = 50)
	@Getter @Setter
	private String carBrand;
	
	@Column(name="CAR_MODEL", nullable = true, length = 50)
	@Getter @Setter
    private String carModel;
	
	@Column(name="HORSEPOWER", nullable = true, length = 6)
	@Getter @Setter
    private String horsepower;
	
	@Column(name="CAR_ENGINE", nullable = true, length = 6)
	@Getter @Setter
    private String carEngine;
	
	public Car(){}  
	
    public Car(String carBrand, String carModel, String horsepower, String carEngine) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.horsepower = horsepower;
        this.carEngine = carEngine;
    }
}
