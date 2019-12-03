package com.guitar.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.guitar.db.repository.LocationJpaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.guitar.db.model.Location;

@ContextConfiguration(locations={"classpath:com/guitar/db/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LocationPersistenceTests {
//	@Autowired
//	private LocationRepository locationRepository;

	@Autowired
	private LocationJpaRepository locationJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		Location location = new Location();
		location.setCountry("Canada");
		location.setState("British Columbia");
//		location = locationRepository.create(location);
		location = locationJpaRepository.saveAndFlush(location);
		
		// clear the persistence context so we don't return the previously cached location object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

		Optional<Location> otherLocation = locationJpaRepository.findById(location.getId());

		if(otherLocation.isPresent()) {
			assertEquals("Canada", otherLocation.get().getCountry());
			assertEquals("British Columbia", otherLocation.get().getState());

			//delete BC location now
			locationJpaRepository.delete(otherLocation.get());
		}
	}

	@Test
	public void testFindWithLike() throws Exception {
//		List<Location> locs = locationRepository.getLocationByStateName("New");
		List<Location> locs = locationJpaRepository.findByStateLike("New%");
		assertEquals(4, locs.size());
	}

	@Test
	public void testFindWithNotLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLike("New%");
		assertEquals(46, locs.size());
	}

	public void testFindWithStartingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateStartingWith("New");
		assertEquals(4, locs.size());
	}

	@Test
	@Transactional  //note this is needed because we will get a lazy load exception unless we are in a tx
	public void testFindWithChildren() throws Exception {
//		Location arizona = locationRepository.find(3L);
//		assertEquals("United States", arizona.getCountry());
//		assertEquals("Arizona", arizona.getState());
//
//		assertEquals(1, arizona.getManufacturers().size());
//
//		assertEquals("Fender Musical Instruments Corporation", arizona.getManufacturers().get(0).getName());
		Optional<Location> arizona = locationJpaRepository.findById(3L);

		if(arizona.isPresent()) {
			assertEquals("United States", arizona.get().getCountry());
			assertEquals("Arizona", arizona.get().getState());

			assertEquals(1, arizona.get().getManufacturers().size());

			assertEquals("Fender Musical Instruments Corporation", arizona.get().getManufacturers().get(0).getName());
		}
	}

	@Test
	public void testJpaFind() {
		List<Location> locations = locationJpaRepository.findAll();
		assertNotNull(locations);
	}

	@Test
	public void testJpaAnd() {
		List<Location> locations = locationJpaRepository.findByStateAndCountry("Utah", "United States");
		assertNotNull(locations);

		assertEquals("Utah", locations.get(0).getState());
	}

	@Test
	public void testJpaOr() {
		List<Location> locations = locationJpaRepository.findByStateIsOrCountryEquals("Utah", "United States");
		assertNotNull(locations);

		assertEquals("Alabama", locations.get(0).getState());
	}

	@Test
	public void testJpaNot() {
		List<Location> locations = locationJpaRepository.findByStateNot("Utah");
		assertNotNull(locations);

		assertNotEquals("Utah", locations.get(0).getState());
	}
}
