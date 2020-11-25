package at.beeroftheworld.model;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONProperty;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class BeerType extends BaseDocument {
	
	private String name;
	private int alcoholLevel;
	
	@JSONProperty("name")
	public String getName() {
		return name;
	}
	
	@JSONProperty("alcoholLevel")
	public int getAlcoholLevel() {
		return alcoholLevel;
	}
	
	public void setName(String name) {
		assertThat(name, notNullValue());
		this.name = name;
	}
	
	public void setAlcoholLevel(int level) {
		assertThat(level, greaterThanOrEqualTo(0));
		alcoholLevel = level;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeerType other = (BeerType) obj;
		if (alcoholLevel != other.alcoholLevel)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
