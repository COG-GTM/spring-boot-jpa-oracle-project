package com.bamossza.project.persistence;

import org.bson.Document;

import com.bamossza.project.entities.Car;

/** Explicit Car <-> BSON mapping. _id holds the Oracle CAR_ID as a 32-bit integer. */
public final class CarDocuments {

    public static final String COLLECTION = "car";
    public static final String COUNTERS = "counters";
    public static final String SEQUENCE_ID = "CAR_SEQ";

    private CarDocuments() {
    }

    public static Document toDocument(Car car) {
        return new Document("_id", car.getCarId())
                .append("carBrand", car.getCarBrand())
                .append("carModel", car.getCarModel())
                .append("horsepower", car.getHorsepower())
                .append("carEngine", car.getCarEngine());
    }

    public static Car toCar(Document doc) {
        Car car = new Car(doc.getString("carBrand"), doc.getString("carModel"),
                doc.getString("horsepower"), doc.getString("carEngine"));
        car.setCarId(doc.getInteger("_id"));
        return car;
    }
}
