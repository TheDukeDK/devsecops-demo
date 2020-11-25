package at.beeroftheworld.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;

import at.beeroftheworld.model.BeerBrand;
import at.beeroftheworld.model.BeerType;
import at.beeroftheworld.persistence.impl.BrandDaoImpl;

public abstract class BrandDaoTest {

	private BrandDao dao;
	private BeerBrand expectedBrand;

	@Before
	public void setup() {
		setupDao();
		setupExpectedBrand();
	}
	
	private void setupDao() {
		ConnectionManager conManager = ConnectionManager.getInstance();
		conManager.buildDefaultConnection("localhost", "cblue", 5984);
		dao = new BrandDaoImpl();
	}
	
	private void setupExpectedBrand() {
		expectedBrand = new BeerBrand();
		expectedBrand.setName("Ottakringer Brauerei");
		expectedBrand.setCountryCode("at");
		expectedBrand.setRegion("vienna");
		expectedBrand.setYearOfFounding(1837);
		
		BeerType[] types = new BeerType[2];
		types[0] = new BeerType();
		types[0].setName("Null Komma Josef");
		types[0].setAlcoholLevel(5);
		types[1] = new BeerType();
		types[1].setName("Helle Freude");
		types[1].setAlcoholLevel(1);
		expectedBrand.setBeerTypes(Arrays.asList(types));
	}
	
	protected BrandDao getTestDao() {
		return dao;
	}
	
	protected BeerBrand getExpectedBrand() {
		return expectedBrand;
	}
	
	protected void setExpectedBrand(BeerBrand brand) {
		expectedBrand = brand;
	}
	
	protected BeerBrand findActualToExpected() {
		List<BeerBrand> brands = dao.findAll();
		BeerBrand actual = null;
		for(BeerBrand brand : brands)
			if(brand.getName().equals(expectedBrand.getName()))
				actual = brand;
		return actual;
	}
	
	protected void assertActualBrand(BeerBrand actual) {
		assertThat(expectedBrand, is(equalTo(actual)));
		assertThat(expectedBrand.getBeerTypes(), is(equalTo(actual.getBeerTypes())));
	}

}
