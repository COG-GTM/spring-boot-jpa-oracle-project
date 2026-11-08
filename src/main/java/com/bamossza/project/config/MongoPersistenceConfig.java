package com.bamossza.project.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

@Configuration
@Profile("mongodb")
public class MongoPersistenceConfig {

    @Bean
    public MongoDatabase carDatabase(MongoClient mongoClient, MongoProperties properties) {
        return mongoClient.getDatabase(properties.getMongoClientDatabase());
    }

    /**
     * CarService keeps its @Transactional boundaries for the Oracle profile. On MongoDB every write
     * touches a single document (plus the atomic counter), so those boundaries are no-ops here.
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new SingleDocumentTransactionManager();
    }

    static class SingleDocumentTransactionManager extends AbstractPlatformTransactionManager {

        private static final long serialVersionUID = 1L;

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
