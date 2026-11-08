package com.bamossza.project.persistence;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;

import org.springframework.dao.DataIntegrityViolationException;

import com.bamossza.project.entities.Car;

/**
 * Column rules the Oracle CAR table enforces implicitly, applied explicitly for stores that do not.
 * Oracle stores '' as NULL and VARCHAR2(n CHAR) limits count characters (code points), not UTF-16 units.
 * Limits are read from the JPA @Column metadata so the two stores cannot drift apart.
 */
public final class CarFieldRules {

    private static final Map<String, Integer> MAX_CHARS = readColumnLimits();

    private CarFieldRules() {
    }

    public static Car normalized(Car source) {
        Car car = new Car(emptyToNull(source.getCarBrand()), emptyToNull(source.getCarModel()),
                emptyToNull(source.getHorsepower()), emptyToNull(source.getCarEngine()));
        car.setCarId(source.getCarId());
        return car;
    }

    public static void validate(Car car) {
        check("carBrand", car.getCarBrand());
        check("carModel", car.getCarModel());
        check("horsepower", car.getHorsepower());
        check("carEngine", car.getCarEngine());
    }

    public static Map<String, Integer> limits() {
        return MAX_CHARS;
    }

    private static void check(String field, String value) {
        int max = MAX_CHARS.get(field);
        if (value != null && value.codePointCount(0, value.length()) > max) {
            throw new DataIntegrityViolationException(
                    "value too large for column " + field + " (maximum: " + max + " characters)");
        }
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static Map<String, Integer> readColumnLimits() {
        Map<String, Integer> limits = new LinkedHashMap<>();
        for (String name : new String[] {"carBrand", "carModel", "horsepower", "carEngine"}) {
            try {
                Field field = Car.class.getDeclaredField(name);
                limits.put(name, field.getAnnotation(Column.class).length());
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }
        }
        return limits;
    }
}
