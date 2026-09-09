package com.bamossza.project.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.bamossza.project.entities.Car;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void savedCarGetsGeneratedIdAndIsReadableByCarId() {
		Car saved = carRepository.save(new Car("Toyota", "Supra", "340", "3.0L"));
		entityManager.flush();
		entityManager.clear();

		assertThat(saved.getCarId()).isNotNull().isPositive();

		Car found = carRepository.findByCarId(saved.getCarId());
		assertThat(found).isNotNull();
		assertThat(found.getCarBrand()).isEqualTo("Toyota");
		assertThat(found.getCarModel()).isEqualTo("Supra");
		assertThat(found.getHorsepower()).isEqualTo("340");
		assertThat(found.getCarEngine()).isEqualTo("3.0L");
	}

	@Test
	void findByCarIdReturnsNullWhenAbsent() {
		assertThat(carRepository.findByCarId(4242)).isNull();
	}

	@Test
	void findAllReturnsAllPersistedCars() {
		carRepository.save(new Car("Honda", "Civic", "180", "1.5L"));
		carRepository.save(new Car("Mazda", "MX-5", "181", "2.0L"));
		entityManager.flush();

		List<Car> cars = carRepository.findAll();

		assertThat(cars).hasSize(2).extracting(Car::getCarBrand).containsExactlyInAnyOrder("Honda", "Mazda");
	}

	@Test
	void deleteRemovesCar() {
		Car saved = carRepository.save(new Car("Ford", "Focus", "150", "1.5L"));
		entityManager.flush();

		carRepository.delete(saved);
		entityManager.flush();
		entityManager.clear();

		assertThat(carRepository.findAll()).isEmpty();
	}

	@Test
	void deleteRemovesCarWhenGivenDetachedIdOnlyInstance() {
		Car saved = carRepository.save(new Car("Nissan", "370Z", "332", "3.7L"));
		entityManager.flush();
		entityManager.clear();

		Car idOnly = new Car();
		idOnly.setCarId(saved.getCarId());
		carRepository.delete(idOnly);
		entityManager.flush();
		entityManager.clear();

		assertThat(carRepository.findByCarId(saved.getCarId())).isNull();
	}

	@Test
	void entityIsMappedToCarTableColumns() {
		Car saved = carRepository.save(new Car("BMW", "M3", "473", "3.0L"));
		entityManager.flush();

		Object brand = entityManager.getEntityManager()
				.createNativeQuery("select CAR_BRAND from CAR where CAR_ID = :id").setParameter("id", saved.getCarId())
				.getSingleResult();

		assertThat(brand).isEqualTo("BMW");
	}
}
