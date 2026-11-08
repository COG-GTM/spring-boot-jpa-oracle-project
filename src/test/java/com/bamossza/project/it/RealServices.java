package com.bamossza.project.it;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/** Connection settings for the *IT tests, which always run against real Oracle XE and MongoDB. */
public final class RealServices {

    private RealServices() {
    }

    static String setting(String key, String env, String fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(env);
        }
        return value == null || value.isEmpty() ? fallback : value;
    }

    public static MongoClientURI mongoUri() {
        return new MongoClientURI(setting("it.mongodb.uri", "IT_MONGODB_URI", "mongodb://localhost:27017/carsystem_it"));
    }

    public static MongoClient mongoClient() {
        return new MongoClient(mongoUri());
    }

    /** One reused connection: Oracle XE has a small process limit (ORA-12519 when exhausted). */
    public static SingleConnectionDataSource oracleDataSource() {
        System.setProperty("oracle.jdbc.timezoneAsRegion", "false");
        SingleConnectionDataSource ds = new SingleConnectionDataSource(
                setting("it.oracle.url", "IT_ORACLE_URL", "jdbc:oracle:thin:@localhost:1521:xe"),
                setting("it.oracle.username", "IT_ORACLE_USERNAME", "carsystem"),
                setting("it.oracle.password", "IT_ORACLE_PASSWORD", "carsystem"), true);
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        return ds;
    }

    /** Recreates CAR and CAR_SEQ exactly as Hibernate generates them for the Oracle profile. */
    public static void resetOracleSchema(JdbcTemplate jdbc) {
        jdbc.execute("BEGIN EXECUTE IMMEDIATE 'DROP TABLE CAR'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -942 THEN RAISE; END IF; END;");
        jdbc.execute("BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE CAR_SEQ'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -2289 THEN RAISE; END IF; END;");
        jdbc.execute("CREATE SEQUENCE CAR_SEQ START WITH 1 INCREMENT BY 1");
        jdbc.execute("CREATE TABLE CAR (CAR_ID NUMBER(10,0) NOT NULL, CAR_BRAND VARCHAR2(50 CHAR), CAR_MODEL VARCHAR2(50 CHAR), "
                + "HORSEPOWER VARCHAR2(6 CHAR), CAR_ENGINE VARCHAR2(6 CHAR), PRIMARY KEY (CAR_ID))");
    }
}
