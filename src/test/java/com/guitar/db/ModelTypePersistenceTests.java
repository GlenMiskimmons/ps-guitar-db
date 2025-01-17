package com.guitar.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.guitar.db.repository.ModelTypeJpaRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.guitar.db.model.ModelType;
import com.guitar.db.repository.ModelTypeRepository;

import java.util.List;
import java.util.Optional;

@ContextConfiguration(locations={"classpath:com/guitar/db/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ModelTypePersistenceTests {
	@Autowired
	private ModelTypeRepository modelTypeRepository;

	@Autowired
	private ModelTypeJpaRepository modelTypeJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		ModelType mt = new ModelType();
		mt.setName("Test Model Type");
//		mt = modelTypeRepository.create(mt);
		mt = modelTypeJpaRepository.save(mt);
		
		// clear the persistence context so we don't return the previously cached location object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

//		ModelType otherModelType = modelTypeRepository.find(mt.getId());
		Optional<ModelType> modelType = modelTypeJpaRepository.findById(mt.getId());

		if(modelType.isPresent()) {
			ModelType otherModelType = modelType.get();
			assertEquals("Test Model Type", otherModelType.getName());

			modelTypeJpaRepository.delete(otherModelType);
		}
	}

	@Test
	public void testFind() throws Exception {
		Optional<ModelType> modelType = modelTypeJpaRepository.findById(1L);

		if(modelType.isPresent()) {
			ModelType mt = modelType.get();
			assertEquals("Dreadnought Acoustic", mt.getName());
		}
	}

	@Test
	public void testForNull() throws Exception {
		List<ModelType> mts = modelTypeJpaRepository.findByNameIsNull();
		assertNull(mts.get(0).getName());
	}
}
