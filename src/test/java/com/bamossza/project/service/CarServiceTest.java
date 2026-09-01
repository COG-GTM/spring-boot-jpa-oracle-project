package com.bamossza.project.service;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
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
    public void findByIdReturnsStubbedCarAndDelegates() {
        Car car = new Car("Toyota", "Corolla", "150", "2.0");
        when(carDao.findById(1)).thenReturn(car);

        assertSame(car, carService.findById(1));

        verify(carDao).findById(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdRejectsZero() {
        try {
            carService.findById(0);
        } finally {
            verifyZeroInteractions(carDao);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByIdRejectsNegativeId() {
        try {
            carService.findById(-1);
        } finally {
            verify(carDao, never()).findById(-1);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeRejectsZero() {
        try {
            carService.remove(0);
        } finally {
            verifyZeroInteractions(carDao);
        }
    }

    @Test
    public void removeDelegatesForPositiveId() {
        carService.remove(5);

        verify(carDao).remove(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addRejectsNull() {
        try {
            carService.add(null);
        } finally {
            verifyZeroInteractions(carDao);
        }
    }

    @Test
    public void addDelegatesCar() {
        Car car = new Car("Toyota", "Corolla", "150", "2.0");

        carService.add(car);

        verify(carDao).add(car);
    }

    @Test
    public void updateDelegatesIdAndCar() {
        Car car = new Car("Toyota", "Corolla", "150", "2.0");

        carService.update(1, car);

        verify(carDao).update(1, car);
    }

    @Test
    public void findAllReturnsNonEmptyDaoResult() {
        List<Map<String, Object>> result = Collections.<Map<String, Object>>singletonList(
                Collections.<String, Object>singletonMap("1", new Car()));
        when(carDao.findAll()).thenReturn(result);

        assertSame(result, carService.findAll());
    }

    @Test
    public void findAllReturnsNullForEmptyDaoResult() {
        when(carDao.findAll()).thenReturn(Collections.<Map<String, Object>>emptyList());

        org.junit.Assert.assertNull(carService.findAll());
    }

    @Test(expected = NullPointerException.class)
    public void findAllPropagatesNullDaoResultFailure() {
        when(carDao.findAll()).thenReturn(null);

        carService.findAll();
    }
}
