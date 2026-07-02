package com.bamossza.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.dto.CarDto;
import com.bamossza.project.entities.Car;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarDao carDao;

    @InjectMocks
    private CarService carService;

    @Test
    void findByIdRejectsNonPositiveId() {
        assertThatThrownBy(() -> carService.findById(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> carService.findById(-5))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(carDao);
    }

    @Test
    void findByIdReturnsCarFromDao() {
        Car car = new Car("MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        car.setCarId(1);
        when(carDao.findById(1)).thenReturn(Optional.of(car));

        Optional<Car> result = carService.findById(1);

        assertThat(result).contains(car);
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        when(carDao.findById(99)).thenReturn(Optional.empty());

        assertThat(carService.findById(99)).isEmpty();
    }

    @Test
    void findAllReturnsEmptyCollectionWhenNoCars() {
        when(carDao.findAll()).thenReturn(List.of());

        List<CarDto> result = carService.findAll();

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void findAllReturnsDtosFromDao() {
        CarDto dto = new CarDto(1, "MAZDA", "SKYACTIV-G 2.0", "165", "2000");
        when(carDao.findAll()).thenReturn(List.of(dto));

        assertThat(carService.findAll()).containsExactly(dto);
    }

    @Test
    void addRejectsNullCar() {
        assertThatThrownBy(() -> carService.add(null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(carDao);
    }

    @Test
    void addDelegatesToDao() {
        Car car = new Car("HONDA", "CIVIC", "158", "1500");

        carService.add(car);

        verify(carDao).add(car);
    }
}
