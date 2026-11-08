package com.bamossza.project.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.bamossza.project.entities.Car;

public class CarFieldRulesTest {

    private static String repeat(String s, int n) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n; i++) {
            b.append(s);
        }
        return b.toString();
    }

    @Test
    public void limitsComeFromJpaColumnMetadata() {
        assertEquals(Integer.valueOf(50), CarFieldRules.limits().get("carBrand"));
        assertEquals(Integer.valueOf(50), CarFieldRules.limits().get("carModel"));
        assertEquals(Integer.valueOf(6), CarFieldRules.limits().get("horsepower"));
        assertEquals(Integer.valueOf(6), CarFieldRules.limits().get("carEngine"));
    }

    @Test
    public void emptyStringsBecomeNullLikeOracle() {
        Car source = new Car("", "Model", "", null);
        source.setCarId(7);
        Car car = CarFieldRules.normalized(source);
        assertNull(car.getCarBrand());
        assertEquals("Model", car.getCarModel());
        assertNull(car.getHorsepower());
        assertNull(car.getCarEngine());
        assertEquals(Integer.valueOf(7), car.getCarId());
        assertEquals("", source.getCarBrand());
    }

    @Test
    public void valuesAtTheLimitAreAccepted() {
        CarFieldRules.validate(new Car(repeat("b", 50), repeat("m", 50), "123456", "1.6 TD"));
    }

    @Test
    public void supplementaryCharactersCountAsOneCharacter() {
        // 6 code points = 12 UTF-16 units; VARCHAR2(6 CHAR) accepts this in Oracle.
        CarFieldRules.validate(new Car("x", "y", repeat("\uD83D\uDD0B", 6), repeat("\u00e9", 6)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void overlengthBrandIsRejected() {
        CarFieldRules.validate(new Car(repeat("b", 51), null, null, null));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void overlengthEngineIsRejected() {
        CarFieldRules.validate(new Car(null, null, null, repeat("\uD83D\uDD0B", 7)));
    }
}
