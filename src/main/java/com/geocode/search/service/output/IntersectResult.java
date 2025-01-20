package com.geocode.search.service.output;

import java.sql.ResultSet;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opengis.feature.simple.SimpleFeature;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntersectResult {

    private HashSet<SimpleFeature> shapeElements = new HashSet<>();
    private ResultSet dbElements;
}
