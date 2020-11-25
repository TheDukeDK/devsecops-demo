package at.beeroftheworld.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.svenson.JSONParser;

public class JsonToModelParsing {
	
	private String brandAsJson;
	private BeerBrand expectedBrand;
	private ArrayList<BeerType> expectedBeerTypes;
	
	@Before
	public void setup() {
		// arrange test data
		String brandName = "My Brewery";
		int yof = 2000;
		String cc = "us";
		String region = "nyc";
		
		String darkName = "Dark";
		int darkAlcoholLevel = 4; 
		
		// setup JSON data
		StringBuilder jsonBuilder = new StringBuilder()
			.append("{")
				.append(transformToJsonAttribute("name", brandName) + ",")
				.append(String.format("\"yearOfFounding\":%d", yof) + ",")
				.append(transformToJsonAttribute("cc", cc) + ",")
				.append(transformToJsonAttribute("region", region) + ",")
				.append("\"beerTypes\":[")
					.append("{")
						.append(transformToJsonAttribute("name", darkName) + ",")
						.append(transformToJsonAttribute("alcoholLevel", darkAlcoholLevel))
					.append("}")
				.append("]")
			.append("}");
		
		brandAsJson = jsonBuilder.toString();
		
		// setup expected brand instance;
		expectedBrand = new BeerBrand();
		expectedBrand.setName(brandName);
		expectedBrand.setYearOfFounding(yof);
		expectedBrand.setCountryCode(cc);
		expectedBrand.setRegion(region);
		
		// setup expected type instance
		expectedBeerTypes = new ArrayList<BeerType>();
		
		BeerType dark = new BeerType();
		dark.setName(darkName);
		dark.setAlcoholLevel(darkAlcoholLevel);
		
		expectedBeerTypes.add(dark);
	}
	
	private String transformToJsonAttribute(String name, Object value) {
		return String.format("\"%s\":\"%s\"", name, value.toString());
	}
	
	@Test
	public void shouldParseJSONToBrandInstance() {
		// Arrange (see setup)
		JSONParser parser = JSONParser.defaultJSONParser();
		Class<BeerBrand> mappedType = BeerBrand.class;
		
		// Act
		BeerBrand actual = parser.parse(mappedType, brandAsJson);
		
		// Assert
		assertThat(actual, equalTo(expectedBrand));
	}
	
	@Test
	public void shouldParseNestedBeerType() {
		// Arrange (see setup)
		JSONParser parser = JSONParser.defaultJSONParser();
		Class<BeerBrand> mappedType = BeerBrand.class;
		
		// Act
		BeerBrand actual = parser.parse(mappedType, brandAsJson);
		List<BeerType> beerTypes = actual.getBeerTypes();
		
		assertThat((List<BeerType>)expectedBeerTypes, is(equalTo(beerTypes)));
	}
}
