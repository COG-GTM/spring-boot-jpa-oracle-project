package com.bamossza.project.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bamossza.project.entities.Car;

/** Legacy GET /api/cars shape: a list of single-entry maps keyed by the car ID as a string. */
public final class CarListView {

    private CarListView() {
    }

    public static List<Map<String, Object>> keyedById(List<Car> cars) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Car car : cars) {
            Map<String, Object> map = new HashMap<>();
            map.put(car.getCarId().toString(), car);
            list.add(map);
        }
        return list;
    }
}
