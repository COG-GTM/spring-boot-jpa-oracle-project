package com.bamossza.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceTest {

    @Mock
    private CarDao carDao;

    @InjectMocks
    private CarService carService;

    @Test
    public void findByIdDelegatesToDao() {
        Car car = new Car("Mazda", "MX-5", "181", "2.0L");
        when(carDao.findById(3)).thenReturn(car);

        assertThat(carService.findById(3)).isSameAs(car);
        verify(carDao).findById(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdRejectsZero() {
        carService.findById(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdRejectsNegativeId() {
        carService.findById(-5);
    }

    @Test
    public void removeDelegatesToDao() {
        carService.remove(9);

        verify(carDao).remove(9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeRejectsNonPositiveId() {
        carService.remove(0);
    }

    @Test
    public void findAllReturnsDaoResultWhenNotEmpty() {
        Map<String, Object> entry = new HashMap<String, Object>();
        entry.put("1", new Car());
        List<Map<String, Object>> daoResult = new ArrayList<Map<String, Object>>();
        daoResult.add(entry);
        when(carDao.findAll()).thenReturn(daoResult);

        assertThat(carService.findAll()).isSameAs(daoResult);
    }

    @Test
    public void findAllReturnsNullWhenDaoResultIsEmpty() {
        when(carDao.findAll()).thenReturn(Collections.<Map<String, Object>> emptyList());

        assertThat(carService.findAll()).isNull();
    }

    @Test(expected = NullPointerException.class)
    public void findAllPropagatesNullFromDao() {
        when(carDao.findAll()).thenReturn(null);

        carService.findAll();
    }

    @Test
    public void addDelegatesToDao() {
        Car car = new Car("Kia", "Stinger", "365", "3.3L");

        carService.add(car);

        verify(carDao).add(car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRejectsNullCar() {
        carService.add(null);
    }

    @Test
    public void updateDelegatesToDao() {
        Car car = new Car("Ford", "Focus", "160", "1.5L");

        carService.update(4, car);

        verify(carDao).update(4, car);
    }

    @Test
    public void updateWithNonPositiveIdAndNonNullCarStillDelegates() {
        Car car = new Car("Ford", "Fiesta", "100", "1.0L");

        carService.update(0, car);

        verify(carDao).update(0, car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRejectsNonPositiveIdWithNullCar() {
        carService.update(0, null);
    }

    @Test
    public void updateWithPositiveIdAndNullCarDelegates() {
        carService.update(2, null);

        verify(carDao).update(2, null);
        verify(carDao, never()).add(null);
    }
}
