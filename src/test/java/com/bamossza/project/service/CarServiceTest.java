package com.bamossza.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

	@Mock
	private CarDao carDao;

	@InjectMocks
	private CarService carService;

	@Test
	void findByIdDelegatesToDao() {
		Car car = new Car("Honda", "Civic", "180", "1.5L");
		given(carDao.findById(3)).willReturn(car);

		assertThat(carService.findById(3)).isSameAs(car);
	}

	@Test
	void findByIdRejectsNonPositiveId() {
		assertThatThrownBy(() -> carService.findById(0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("ID cannot be 0 or < 0");
		assertThatThrownBy(() -> carService.findById(-1)).isInstanceOf(IllegalArgumentException.class);

		then(carDao).should(never()).findById(org.mockito.ArgumentMatchers.anyInt());
	}

	@Test
	void removeRejectsNonPositiveId() {
		assertThatThrownBy(() -> carService.remove(0)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("ID cannot be 0 or < 0 or this id do not exist");

		then(carDao).should(never()).remove(org.mockito.ArgumentMatchers.anyInt());
	}

	@Test
	void removeDelegatesToDao() {
		carService.remove(7);

		then(carDao).should().remove(7);
	}

	@Test
	void addRejectsNullCar() {
		assertThatThrownBy(() -> carService.add(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The passed object cannot be null.");

		then(carDao).should(never()).add(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void addDelegatesToDao() {
		Car car = new Car("Mazda", "MX-5", "181", "2.0L");

		carService.add(car);

		then(carDao).should().add(car);
	}

	@Test
	void updateRejectsNonPositiveIdWithNullCar() {
		assertThatThrownBy(() -> carService.update(0, null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The passed object cannot be null.");
	}

	@Test
	void updateAcceptsNonPositiveIdWhenCarIsNotNull() {
		Car car = new Car("Audi", "A4", "201", "2.0L");

		carService.update(0, car);

		then(carDao).should().update(0, car);
	}

	@Test
	void updateAcceptsNullCarWhenIdIsPositive() {
		carService.update(4, null);

		then(carDao).should().update(4, null);
	}

	@Test
	void updateDelegatesToDao() {
		Car car = new Car("Ford", "Focus", "150", "1.5L");

		carService.update(2, car);

		then(carDao).should().update(2, car);
	}

	@Test
	void findAllReturnsDaoResultWhenNotEmpty() {
		Map<String, Object> entry = new HashMap<>();
		entry.put("1", new Car("BMW", "M3", "473", "3.0L"));
		List<Map<String, Object>> result = new ArrayList<>(Collections.singletonList(entry));
		given(carDao.findAll()).willReturn(result);

		assertThat(carService.findAll()).isSameAs(result);
	}

	@Test
	void findAllReturnsNullWhenDaoResultIsEmpty() {
		given(carDao.findAll()).willReturn(new ArrayList<>());

		assertThat(carService.findAll()).isNull();
	}
}
