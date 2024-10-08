package com.geocode.search.service.intersect.shapefile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShapeData {

	private Filter filter;
	private Query query;
	private FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
	private FeatureIterator<SimpleFeature> feature;
}
