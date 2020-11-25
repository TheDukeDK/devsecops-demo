package at.beeroftheworld.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.ValueRow;
import org.jcouchdb.document.ViewResult;
import org.svenson.JSONParser;

import at.beeroftheworld.model.BeerBrand;
import at.beeroftheworld.persistence.BrandDao;
import at.beeroftheworld.persistence.ConnectionManager;
import at.beeroftheworld.persistence.OptimisticLockingException;
import at.beeroftheworld.persistence.PersistenceException;

public class BrandDaoImpl implements BrandDao {

	private static final String BRANDS_VIEW = "allBrands/allBrands";

	public List<BeerBrand> findAll() {
		try {
			ViewResult<BeerBrand> queryResult = queryView();
			return transformViewResultToList(queryResult);
		} catch (Exception e) {
			throw new PersistenceException(
					"Exception occurd during querying the brands view.", e);
		}
	}

	private ViewResult<BeerBrand> queryView() {
		Database connection = ConnectionManager.getInstance()
				.getDefaultConnection();
		JSONParser jsonParser = JSONParser.defaultJSONParser();
		return connection.queryView(BRANDS_VIEW, BeerBrand.class, null,
				jsonParser);
	}

	private List<BeerBrand> transformViewResultToList(
			ViewResult<BeerBrand> queryResult) {
		List<BeerBrand> beerBrands = new ArrayList<BeerBrand>(
				queryResult.getTotalRows());
		for (ValueRow<BeerBrand> row : queryResult.getRows())
			beerBrands.add(row.getValue());
		return beerBrands;
	}

	public void insert(BeerBrand brand) {
		try {
			Database connection = ConnectionManager.getInstance()
					.getDefaultConnection();
			connection.createDocument(brand);
		} catch (Exception e) {
			throw new PersistenceException(
					"Exception occurd during creating new brand.", e);
		}
	}

	public void delete(BeerBrand brand) {
		try {
			Database connection = ConnectionManager.getInstance()
					.getDefaultConnection();
			connection.delete(brand);
		} catch (Exception e) {
			throw new PersistenceException(
					"Exception occurd during delting a brand.", e);
		}
	}

	public BeerBrand update(BeerBrand brand) {
		BeerBrand newerBrand = findNewewBrand(brand);
		if (newerBrand == null) {
			try {
				Database connection = ConnectionManager.getInstance()
						.getDefaultConnection();
				connection.updateDocument(brand);
				return findById(brand.getId());
			} catch (Exception e) {
				throw new PersistenceException("Exception occurd during updateting brand.", e);
			}
		}
		else {
			throw new OptimisticLockingException(brand, newerBrand);
		}
	}

	private BeerBrand findNewewBrand(BeerBrand currentBrand) {
		BeerBrand newerBrand = findById(currentBrand.getId());
		if (newerBrand.getRevision().equals(currentBrand.getRevision()))
			return null;
		return newerBrand;
	}

	public BeerBrand findById(String documentId) {
		try {
			Database connection = ConnectionManager.getInstance()
					.getDefaultConnection();
			return connection.getDocument(BeerBrand.class, documentId);
		} catch (Exception e) {
			throw new PersistenceException(
					"Exception occurd during requesting a document by id.", e);
		}
	}

}
