package com.bamossza.project.persistence;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;

/** Atomic counter that replaces the Oracle CAR_SEQ sequence (increment by 1, starts at 1). */
public class MongoCarSequence {

    private final MongoCollection<Document> counters;

    public MongoCarSequence(MongoDatabase database) {
        this.counters = database.getCollection(CarDocuments.COUNTERS);
    }

    public int next() {
        Document doc = counters.findOneAndUpdate(eq("_id", CarDocuments.SEQUENCE_ID),
                new Document("$inc", new Document("seq", 1L)),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        return Math.toIntExact(((Number) doc.get("seq")).longValue());
    }

    /** Last value handed out, or 0 when the counter does not exist yet. */
    public long current() {
        Document doc = counters.find(eq("_id", CarDocuments.SEQUENCE_ID)).first();
        return doc == null ? 0L : ((Number) doc.get("seq")).longValue();
    }

    /** Raises the counter to at least {@code value}; never lowers it, so reruns cannot reissue IDs. */
    public void raiseTo(long value) {
        counters.updateOne(eq("_id", CarDocuments.SEQUENCE_ID),
                new Document("$max", new Document("seq", value)), new UpdateOptions().upsert(true));
    }
}
