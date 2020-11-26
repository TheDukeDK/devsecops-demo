package at.beeroftheworld.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONProperty;
import org.svenson.JSONTypeHint;

public class BeerBrand extends BaseDocument implements Cloneable {

	private String name;
	private int yearOfFounding;
	private String countryCode;
	private String region;
	private List<BeerType> beerTypes;
	
	@JSONProperty("name")
	public String getName() {
		return name;
	}
	
	@JSONProperty("yearOfFounding")
	public int getYearOfFounding() {
		return yearOfFounding;
	}
	
	@JSONProperty("countryCode")
	public String getCountryCode() {
		return countryCode;
	}
	
	@JSONProperty("region")
	public String getRegion() {
		return region;
	}
	
	public List<BeerType> getBeerTypes() {
		return beerTypes;
	}
	
	public void setName(String name) {
		assertThat(name, notNullValue());
		this.name = name;
	}
	
	public void setYearOfFounding(int yof) {
		assertThat(yof, greaterThanOrEqualTo(1040)); // oldest beer brewery was founded in 1040
		this.yearOfFounding = yof;
	}
	
	public void setCountryCode(String cc) {
		assertThat(cc, notNullValue());
		assertThat(cc.length(), equalTo(2));
		this.countryCode = cc.toLowerCase();
	}
	
	public void setRegion(String region) {
		assertThat(region, notNullValue());
		this.region = region;
	}
	
	@JSONTypeHint(BeerType.class)
	@JSONProperty("beerTypes")
	public void setBeerTypes(List<BeerType> beerTypes) {
		this.beerTypes = beerTypes;
	}
	
	public @Override boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		BeerBrand other = (BeerBrand) obj;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (yearOfFounding != other.yearOfFounding)
			return false;
		return true;
	}	
	
	public @Override Object clone() {
		BeerBrand brand = new BeerBrand();
		brand.setId(getId());
		brand.setRevision(getRevision());
		brand.setAttachments(getAttachments());
		brand.setName(getName());
		brand.setYearOfFounding(getYearOfFounding());
		brand.setCountryCode(getCountryCode());
		brand.setRegion(getRegion());
		brand.setBeerTypes(getBeerTypes());
		return brand;
	}
}
