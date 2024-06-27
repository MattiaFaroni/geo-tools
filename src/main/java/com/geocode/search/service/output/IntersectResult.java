package com.geocode.search.service.output;

import java.sql.ResultSet;
import java.util.HashSet;
import org.opengis.feature.simple.SimpleFeature;

public class IntersectResult {

    private HashSet<SimpleFeature> shapeElements = new HashSet<>();
    private ResultSet dbElements;

    public HashSet<SimpleFeature> getShapeElements() {
        return shapeElements;
    }

    public void setShapeElements(HashSet<SimpleFeature> shapeElements) {
        this.shapeElements = shapeElements;
    }

    public ResultSet getDbElements() {
        return dbElements;
    }

    public void setDbElements(ResultSet dbElements) {
        this.dbElements = dbElements;
    }
}
