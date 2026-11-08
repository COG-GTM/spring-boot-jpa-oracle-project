package com.bamossza.project.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.dao.CarListView;
import com.bamossza.project.entities.Car;
import com.bamossza.project.persistence.CarDocuments;
import com.bamossza.project.persistence.CarFieldRules;
import com.bamossza.project.persistence.MongoCarSequence;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

/**
 * MongoDB implementation of {@link CarDao} that reproduces the observable behavior of the Oracle/JPA
 * implementation captured in the baseline (see MIGRATION.md, "Preserved legacy semantics").
 */
@Repository
@Profile("mongodb")
public class MongoCarDaoImpl implements CarDao {

    private final MongoCollection<Document> cars;
    private final MongoCarSequence sequence;

    @Autowired
    public MongoCarDaoImpl(MongoDatabase database) {
        this.cars = database.getCollection(CarDocuments.COLLECTION);
        this.sequence = new MongoCarSequence(database);
    }

    @Override
    public Car findById(int id) {
        Document doc = cars.find(eq("_id", id)).first();
        return doc == null ? null : CarDocuments.toCar(doc);
    }

    /** Legacy: deleting an unknown ID succeeds and burns one sequence value (JPA merge-then-remove). */
    @Override
    public void remove(int id) {
        if (cars.deleteOne(eq("_id", id)).getDeletedCount() == 0) {
            sequence.next();
        }
    }

    /** Legacy: any client-supplied carId is ignored; the ID is allocated before validation. */
    @Override
    public void add(Car car) {
        insertWithNewId(car);
    }

    /** Legacy: PUT replaces every field; PUT on an unknown ID inserts a new row under a new ID. */
    @Override
    public void update(int id, Car car) {
        if (cars.find(eq("_id", id)).first() == null) {
            insertWithNewId(car);
            return;
        }
        Car replacement = CarFieldRules.normalized(car);
        replacement.setCarId(id);
        CarFieldRules.validate(replacement);
        cars.replaceOne(eq("_id", id), CarDocuments.toDocument(replacement));
    }

    @Override
    public List<Map<String, Object>> findAll() {
        List<Car> result = new ArrayList<>();
        for (Document doc : cars.find().sort(Sorts.ascending("_id"))) {
            result.add(CarDocuments.toCar(doc));
        }
        return CarListView.keyedById(result);
    }

    private void insertWithNewId(Car car) {
        Car toInsert = CarFieldRules.normalized(car);
        toInsert.setCarId(sequence.next());
        CarFieldRules.validate(toInsert);
        cars.insertOne(CarDocuments.toDocument(toInsert));
    }
}
