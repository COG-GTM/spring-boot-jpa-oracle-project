package com.bamossza.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bamossza.project.controller.CarController;
import com.bamossza.project.dao.CarDao;
import com.bamossza.project.entities.Car;
import com.bamossza.project.repository.CarRepository;
import com.bamossza.project.service.CarService;

@SpringBootTest
class CarApplicationTests {

	@Autowired
	private CarController carController;

	@Autowired
	private CarService carService;

	@Autowired
	private CarDao carDao;

	@Autowired
	private CarRepository carRepository;

	@Test
	void contextLoads() {
		assertThat(carController).isNotNull();
		assertThat(carService).isNotNull();
		assertThat(carDao).isNotNull();
		assertThat(carRepository).isNotNull();
	}

	@Test
	void savesAndReadsBackACar() {
		carDao.add(new Car("Toyota", "Corolla", "132", "1.8"));

		assertThat(carRepository.findAll()).hasSize(1);
		Integer id = carRepository.findAll().get(0).getCarId();
		assertThat(carService.findById(id).getCarBrand()).isEqualTo("Toyota");
		assertThat(carService.findAll()).hasSize(1);

		carService.remove(id);
		assertThat(carRepository.findAll()).isEmpty();
	}
}
