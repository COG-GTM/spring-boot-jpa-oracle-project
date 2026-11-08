package com.bamossza.project.migration;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bamossza.project.entities.Car;

/** Plain JDBC access to the CAR table and CAR_SEQ sequence created by the Oracle/JPA application. */
public class OracleCarStore {

    private static final String MERGE = "MERGE INTO CAR t USING (SELECT ? CAR_ID, ? CAR_BRAND, ? CAR_MODEL, ? HORSEPOWER, ? CAR_ENGINE FROM DUAL) s"
            + " ON (t.CAR_ID = s.CAR_ID)"
            + " WHEN MATCHED THEN UPDATE SET t.CAR_BRAND = s.CAR_BRAND, t.CAR_MODEL = s.CAR_MODEL, t.HORSEPOWER = s.HORSEPOWER, t.CAR_ENGINE = s.CAR_ENGINE"
            + " WHEN NOT MATCHED THEN INSERT (CAR_ID, CAR_BRAND, CAR_MODEL, HORSEPOWER, CAR_ENGINE)"
            + " VALUES (s.CAR_ID, s.CAR_BRAND, s.CAR_MODEL, s.HORSEPOWER, s.CAR_ENGINE)";

    private final JdbcTemplate jdbc;

    public OracleCarStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SortedMap<Integer, Car> readAll() {
        SortedMap<Integer, Car> rows = new TreeMap<>();
        jdbc.query("SELECT CAR_ID, CAR_BRAND, CAR_MODEL, HORSEPOWER, CAR_ENGINE FROM CAR ORDER BY CAR_ID", rs -> {
            Car car = new Car(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            car.setCarId(rs.getInt(1));
            rows.put(car.getCarId(), car);
        });
        return rows;
    }

    public void upsertAll(Collection<Car> cars) {
        for (Car c : cars) {
            jdbc.update(MERGE, c.getCarId(), c.getCarBrand(), c.getCarModel(), c.getHorsepower(), c.getCarEngine());
        }
    }

    public void deleteAll(Collection<Integer> ids) {
        for (Integer id : ids) {
            jdbc.update("DELETE FROM CAR WHERE CAR_ID = ?", id);
        }
    }

    /** Draws one value from CAR_SEQ. The value is consumed, exactly as an application insert would. */
    public long nextSequenceValue() {
        return jdbc.queryForObject("SELECT CAR_SEQ.NEXTVAL FROM DUAL", Long.class);
    }

    /** Ensures the next NEXTVAL is strictly greater than {@code lastIssued} (Oracle 11g has no RESTART). */
    public void advanceSequencePast(long lastIssued) {
        long next = nextSequenceValue();
        long gap = lastIssued - next;
        if (gap > 0) {
            jdbc.execute("ALTER SEQUENCE CAR_SEQ INCREMENT BY " + gap);
            try {
                nextSequenceValue();
            } finally {
                jdbc.execute("ALTER SEQUENCE CAR_SEQ INCREMENT BY 1");
            }
        }
    }
}
