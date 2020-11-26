package at.beeroftheworld.persistence;

import java.util.List;

import at.beeroftheworld.model.BeerBrand;

public interface BrandDao {
	List<BeerBrand> findAll();
	BeerBrand findById(String documentId);
	void insert(BeerBrand brand);
	void delete(BeerBrand brand);
	BeerBrand update(BeerBrand brand);
}
