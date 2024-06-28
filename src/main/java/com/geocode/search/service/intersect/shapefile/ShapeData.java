package com.geocode.search.service.intersect.shapefile;

import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public class ShapeData {

	private Filter filter;
	private Query query;
	private FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
	private FeatureIterator<SimpleFeature> feature;

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> getCollection() {
		return collection;
	}

	public void setCollection(FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
		this.collection = collection;
	}

	public FeatureIterator<SimpleFeature> getFeature() {
		return feature;
	}

	public void setFeature(FeatureIterator<SimpleFeature> feature) {
		this.feature = feature;
	}
}
