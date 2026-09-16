package com.bamossza.project.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bamossza.project.entities.Car;
import com.bamossza.project.repository.CarRepository;

@RunWith(MockitoJUnitRunner.class)
public class CarDaoImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarDaoImpl carDao;

    private static Car car(int id, String brand) {
        Car car = new Car(brand, "model", "100", "1.0L");
        car.setCarId(id);
        return car;
    }

    @Test
    public void findByIdReturnsRepositoryResult() {
        Car car = car(1, "Audi");
        when(carRepository.findByCarId(1)).thenReturn(car);

        assertThat(carDao.findById(1)).isSameAs(car);
    }

    @Test
    public void findByIdReturnsNullWhenRepositoryThrows() {
        when(carRepository.findByCarId(2)).thenThrow(new RuntimeException("db down"));

        assertThat(carDao.findById(2)).isNull();
    }

    @Test
    public void removeDeletesCarWithGivenId() {
        carDao.remove(11);

        ArgumentCaptor<Car> captor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).delete(captor.capture());
        assertThat(captor.getValue().getCarId()).isEqualTo(11);
    }

    @Test
    public void removeSwallowsRepositoryException() {
        org.mockito.Mockito.doThrow(new RuntimeException("db down")).when(carRepository).delete(org.mockito.Matchers.any(Car.class));

        carDao.remove(12);
    }

    @Test
    public void addSavesCar() {
        Car car = car(3, "BMW");

        carDao.add(car);

        verify(carRepository).save(car);
    }

    @Test
    public void addSwallowsRepositoryException() {
        Car car = car(4, "BMW");
        when(carRepository.save(car)).thenThrow(new RuntimeException("db down"));

        carDao.add(car);
    }

    @Test
    public void updateSetsIdBeforeSaving() {
        Car car = new Car("Tesla", "Model 3", "283", "EV");

        carDao.update(42, car);

        assertThat(car.getCarId()).isEqualTo(42);
        verify(carRepository).save(car);
    }

    @Test
    public void updateSwallowsRepositoryException() {
        Car car = new Car("Tesla", "Model Y", "384", "EV");
        when(carRepository.save(car)).thenThrow(new RuntimeException("db down"));

        carDao.update(43, car);

        assertThat(car.getCarId()).isEqualTo(43);
    }

    @Test
    public void findAllMapsEachCarKeyedById() {
        when(carRepository.findAll()).thenReturn(Arrays.asList(car(1, "Audi"), car(2, "Seat")));

        List<Map<String, Object>> result = carDao.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsKey("1");
        assertThat(result.get(1)).containsKey("2");
        assertThat(((Car) result.get(1).get("2")).getCarBrand()).isEqualTo("Seat");
    }

    @Test
    public void findAllReturnsEmptyListWhenRepositoryIsEmpty() {
        when(carRepository.findAll()).thenReturn(new ArrayList<Car>());

        assertThat(carDao.findAll()).isEmpty();
    }

    @Test
    public void findAllReturnsNullWhenRepositoryThrows() {
        when(carRepository.findAll()).thenThrow(new RuntimeException("db down"));

        assertThat(carDao.findAll()).isNull();
    }

    @Test
    public void findAllReturnsNullWhenACarHasNoId() {
        when(carRepository.findAll()).thenReturn(Arrays.asList(new Car("Opel", "Corsa", "75", "1.2L")));

        assertThat(carDao.findAll()).isNull();
    }
}
