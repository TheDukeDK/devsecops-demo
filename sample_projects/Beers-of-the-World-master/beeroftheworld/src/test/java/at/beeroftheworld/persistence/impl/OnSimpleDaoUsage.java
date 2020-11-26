package at.beeroftheworld.persistence.impl;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;

import at.beeroftheworld.model.BeerBrand;
import at.beeroftheworld.persistence.BrandDao;
import at.beeroftheworld.persistence.BrandDaoTest;

public class OnSimpleDaoUsage extends BrandDaoTest {

	@Test
	public void testAllInOrder() {
		shouldCreateAndRetrieveExpectedBrand();
		shouldCorrectlyUpdateBrandAndFirstType();
		shouldDeleteExpectedDocument();
	}

	@Test
	public void shouldCreateAndRetrieveExpectedBrand() {
		// Arrange (see setup)
		BeerBrand expectedBrand = getExpectedBrand();
		BrandDao dao = getTestDao();

		// Act
		dao.insert(expectedBrand);
		BeerBrand actual = findActualToExpected();

		// Assert
		assertActualBrand(actual);
	}

	@Test
	public void shouldCorrectlyUpdateBrandAndFirstType() {
		// Arrange (see setup)
		BeerBrand expectedBrand = findActualToExpected(); // you need an ID to update, so expected won't work
		BrandDao dao = getTestDao();
		
		// Act
		expectedBrand.setRegion("berlin");
		expectedBrand.setCountryCode("de");
		expectedBrand.getBeerTypes().get(0).setAlcoholLevel(99);
		dao.update(expectedBrand);
		setExpectedBrand(expectedBrand); // because local brand was retrieved from database
		BeerBrand actual = findActualToExpected();
		
		// Assert
		assertActualBrand(actual);
	}

	@Test
	public void shouldDeleteExpectedDocument() {
		// Arrange (see setup)
		BeerBrand expectedBrand = findActualToExpected(); // you need an ID to update, so expected won't work
		BrandDao dao = getTestDao();

		// Act
		dao.delete(expectedBrand);
		BeerBrand actual = findActualToExpected();

		// Assert
		assertThat(actual, is(nullValue()));
	}

}
