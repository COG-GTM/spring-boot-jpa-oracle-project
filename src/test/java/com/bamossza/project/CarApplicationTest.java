package com.bamossza.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class CarApplicationTest {

    @Test
    public void isAnnotatedAsSpringBootApplication() {
        assertThat(CarApplication.class.getAnnotation(SpringBootApplication.class)).isNotNull();
    }

    @Test
    public void exposesMainEntryPoint() throws Exception {
        assertThat(CarApplication.class.getMethod("main", String[].class)).isNotNull();
    }
}
