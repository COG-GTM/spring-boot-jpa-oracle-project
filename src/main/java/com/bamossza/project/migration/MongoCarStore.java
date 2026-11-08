package com.bamossza.project.migration;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bson.Document;

import com.bamossza.project.entities.Car;
import com.bamossza.project.persistence.CarDocuments;
import com.bamossza.project.persistence.MongoCarSequence;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

public class MongoCarStore {

    private static final int BATCH = 500;

    private final MongoCollection<Document> cars;
    private final MongoCarSequence sequence;

    public MongoCarStore(MongoDatabase database) {
        this.cars = database.getCollection(CarDocuments.COLLECTION);
        this.sequence = new MongoCarSequence(database);
    }

    public SortedMap<Integer, Car> readAll() {
        SortedMap<Integer, Car> rows = new TreeMap<>();
        for (Document doc : cars.find()) {
            Car car = CarDocuments.toCar(doc);
            rows.put(car.getCarId(), car);
        }
        return rows;
    }

    public void upsertAll(Collection<Car> source) {
        List<WriteModel<Document>> batch = new ArrayList<>();
        for (Car car : source) {
            batch.add(new ReplaceOneModel<>(eq("_id", car.getCarId()), CarDocuments.toDocument(car), new UpdateOptions().upsert(true)));
            if (batch.size() == BATCH) {
                cars.bulkWrite(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            cars.bulkWrite(batch);
        }
    }

    public void deleteAll(Collection<Integer> ids) {
        if (!ids.isEmpty()) {
            cars.deleteMany(in("_id", ids));
        }
    }

    public MongoCarSequence sequence() {
        return sequence;
    }
}
