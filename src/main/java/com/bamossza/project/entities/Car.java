package com.bamossza.project.entities;

import java.io.Serializable;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a Car.
 *
 * <p>Uses Lombok for getter/setter generation and Java 8 StringJoiner
 * for a readable toString() implementation.</p>
 */
@Entity
@Table(name = "CAR")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CAR_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CAR_SEQ", sequenceName = "CAR_SEQ", allocationSize = 1)
    @Column(name = "CAR_ID", unique = true, nullable = false, precision = 10, scale = 0)
    @Getter @Setter
    private Integer carId;

    @Column(name = "CAR_BRAND", nullable = true, length = 50)
    @Getter @Setter
    private String carBrand;

    @Column(name = "CAR_MODEL", nullable = true, length = 50)
    @Getter @Setter
    private String carModel;

    @Column(name = "HORSEPOWER", nullable = true, length = 6)
    @Getter @Setter
    private String horsepower;

    @Column(name = "CAR_ENGINE", nullable = true, length = 6)
    @Getter @Setter
    private String carEngine;

    public Car() {
    }

    public Car(String carBrand, String carModel, String horsepower, String carEngine) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.horsepower = horsepower;
        this.carEngine = carEngine;
    }

    /**
     * Returns a string representation of this Car using Java 8 StringJoiner.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("carId=" + carId)
                .add("carBrand='" + carBrand + "'")
                .add("carModel='" + carModel + "'")
                .add("horsepower='" + horsepower + "'")
                .add("carEngine='" + carEngine + "'")
                .toString();
    }
}
