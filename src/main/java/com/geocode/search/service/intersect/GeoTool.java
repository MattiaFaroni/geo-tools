package com.geocode.search.service.intersect;

import com.geocode.search.cli.settings.IntersectParameters;
import com.geocode.search.connection.Database;
import com.geocode.search.service.input.Candidate;
import com.geocode.search.service.intersect.shapefile.ShapeData;
import com.geocode.search.service.output.IntersectResult;
import java.io.File;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

@Getter
@Setter
@NoArgsConstructor
public class GeoTool {

	private static SimpleFeatureSource source;
	private static SimpleFeatureType schema;
	private static final Vector<SimpleFeatureSource> cachedSourceVect = new Vector<>();
	private static final Vector<SimpleFeatureType> schemaVect = new Vector<>();

	/**
	 * Constructor
	 * @param fileName shapefile name
	 * @param activeCache cache enable flag
	 */
	// spotless:off
	public GeoTool(String fileName, String activeCache) {

		File file = new File(fileName);
		try {
			System.out.println("- Start upload shapefile " + file.getName() + ": " + Calendar.getInstance().getTime());
			FileDataStore myData = FileDataStoreFinder.getDataStore(file);
			source = myData.getFeatureSource();
			schema = source.getSchema();

			if (activeCache.equalsIgnoreCase("S") || activeCache.equalsIgnoreCase("Y")) {
				cachedSourceVect.add(DataUtilities.source(new SpatialIndexFeatureCollection(source.getFeatures())));
				schemaVect.add(schema);
			} else {
				cachedSourceVect.add(source);
				schemaVect.add(schema);
			}
			System.out.println("- End upload shapefile " + file.getName() + ": " + Calendar.getInstance().getTime());

		} catch (Exception e) {
			System.out.println("- Error during upload shapefile: " + e.getMessage());
			cachedSourceVect.add(source);
			schemaVect.add(schema);
		}
	}
	// spotless:on

	/**
	 * Method used to project coordinates onto the shapefile
	 * @param candidate candidate to be projected on the shapefile
	 * @param intersectResult area to be enhanced
	 * @return candidate extracted from the shapefile
	 */
	// spotless:off
	public IntersectResult intersectShapefile(Candidate candidate, IntersectResult intersectResult) {

		ShapeData shapeData = new ShapeData();
		try {
			shapeData.setFilter(CQL.toFilter("intersects(the_geom, POINT(" + candidate.getCoordinateX() + " " + candidate.getCoordinateY() + "))"));

			for (int i = 0; i < cachedSourceVect.size(); i++) {
				synchronized (cachedSourceVect) {
					shapeData.setQuery(new Query(schemaVect.get(i).getTypeName()));
					shapeData.getQuery().setMaxFeatures(1);
					shapeData.getQuery().setFilter(shapeData.getFilter());
					shapeData.setCollection(cachedSourceVect.get(i).getFeatures(shapeData.getQuery()));
				}
			}

			try (FeatureIterator<SimpleFeature> features =
					shapeData.getCollection().features()) {
				while (features.hasNext()) {
					intersectResult.getShapeElements().add(features.next());
				}
			}

		} catch (Exception e) {
			System.out.println("Error during intersect shapefile: " + e.getMessage());
			System.exit(1);
		}
		return intersectResult;
	}
	// spotless:on

	/**
	 * Method used to project coordinates onto the shapefile and extract n candidates
	 * @param candidate candidate to be projected on the shapefile
	 * @param intersectParameters settings used for research
	 * @return candidates extracted from the shapefile
	 */
	// spotless:off
	public IntersectResult extractDataFromShapefile(Candidate candidate, IntersectParameters intersectParameters) {

		IntersectResult intersectResult = new IntersectResult();
		try {
			double lap = 1;
			double distance = 0;
			double increase = 0.00001 * intersectParameters.getIncrease();
			intersectResult = intersectShapefile(candidate, intersectResult);

			for (int i = 1; i < intersectParameters.getAttempts(); i++) {
				if (intersectResult.getShapeElements().size() == intersectParameters.getCandidates()) break;
				if (distance >= intersectParameters.getMaxDistance()) break;

				for (double x = candidate.getCoordinateX() - increase;	x <= candidate.getCoordinateX() + increase;	x += increase / lap) {

					for (double y = candidate.getCoordinateY() - increase; y <= candidate.getCoordinateY() + increase; y += increase / lap) {

						Candidate point = new Candidate(x, y);
						intersectResult = intersectShapefile(point, intersectResult);
						if (intersectResult.getShapeElements().size() == intersectParameters.getCandidates()) break;
					}

					if (intersectResult.getShapeElements().size() == intersectParameters.getCandidates()) break;
				}

				distance += intersectParameters.getIncrease();
				increase += 0.00001 * intersectParameters.getIncrease();
				lap++;
			}
		} catch (Exception e) {
			System.out.println("Error when extracting data from shapefile: " + e.getMessage());
			System.exit(1);
		}
		return intersectResult;
	}
	// spotless:on

	/**
	 * Method used to project the coordinates onto the database and extract the specified columns
	 * @param candidate candidate to be projected on the database
	 * @param database database connection
	 * @param columns database columns
	 * @return list of candidates close to the given point
	 */
	// spotless:off
	public IntersectResult extractDataFromDatabase(Candidate candidate, Database database, double limit, ArrayList<String> columns) {
		String query = "SELECT DISTINCT ";
		for (String column : columns) {
			query += column + ",";
		}
		query += "ST_Distance(geom,ST_SetSRID(ST_MakePoint("
				+ candidate.getCoordinateX()
				+ ","
				+ candidate.getCoordinateY()
				+ "),"
				+ candidate.getCoordinateType()
				+ ")) FROM "
				+ database.getSchema()
				+ ".\""
				+ database.getTable()
				+ "\" ORDER BY ST_Distance LIMIT "
				+ limit
				+ ";";

		return executeIntersect(database, query);
	}
	// spotless:on

	/**
	 * Method used to project coordinates onto the database and extract n candidates given in the limit parameter
	 * @param candidate candidate to be projected on the database
	 * @param database database connection
	 * @param limit maximum number of candidates to be drawn
	 * @return list of candidates close to the given point
	 */
	public IntersectResult extractDataFromDatabase(Candidate candidate, Database database, double limit) {
		String query = "SELECT DISTINCT *,ST_Distance(geom,ST_SetSRID(ST_MakePoint("
				+ candidate.getCoordinateX()
				+ ","
				+ candidate.getCoordinateY()
				+ "),"
				+ candidate.getCoordinateType()
				+ ")) FROM "
				+ database.getSchema()
				+ ".\""
				+ database.getTable()
				+ "\" ORDER BY ST_Distance LIMIT "
				+ limit
				+ ";";

		return executeIntersect(database, query);
	}

	// TODO add a new method that allows you to set a maximum search radius on the database

	/**
	 * Method used to extract data from the database
	 * @param database database connection
	 * @param query database query
	 * @return list of candidates close to the given point
	 */
	private IntersectResult executeIntersect(Database database, String query) {
		IntersectResult intersectResult = new IntersectResult();
		try {
			database.connect();
			if (database.getConnection() != null) {
				Statement stmt = database.getConnection().createStatement();
				intersectResult.setDbElements(stmt.executeQuery(query));
			} else {
				System.out.println("Error during database connection.");
			}
		} catch (Exception e) {
			System.out.println("Error during intersect database: " + e.getMessage());
		}
		return intersectResult;
	}
}
