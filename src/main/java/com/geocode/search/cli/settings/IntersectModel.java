package com.geocode.search.cli.settings;

import static com.geocode.search.cli.Descriptions.*;

import com.geocode.search.connection.Database;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntersectModel {

	private String intersectType;
	private ArrayList<String> intersectData = new ArrayList<>();
	private File shapefilePath;
	private Database databaseConnection;

	/**
	 * Method used to read intersect parameters from the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	public boolean readConfigFromProperties(Properties properties) {
		if (readIntersectType(properties)) {
			if (readIntersectData(properties)) {

				if (intersectType.equalsIgnoreCase("shapefile")) {
					return readShapefileConfiguration(properties);
				} else if (intersectType.equalsIgnoreCase("database")) {
					return readDatabaseConfiguration(properties);
				}
			}
		}
		return false;
	}

	/**
	 * Method used to read the intersect type from the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	// spotless:off
	private boolean readIntersectType(Properties properties) {
		if (properties.getProperty("geotools.intersect_type") != null) {
			if (properties.getProperty("geotools.intersect_type").equalsIgnoreCase("shapefile")  || properties.getProperty("geotools.intersect_type").equalsIgnoreCase("database")) {
				intersectType = properties.getProperty("geotools.intersect_type");
				return true;
			} else {
				System.out.println(INTERSECT_TYPE_INVALID.description);
				return false;
			}

		} else {
			System.out.println(INTERSECT_TYPE_INVALID.description);
			return false;
		}
	}
	// spotless:on

	/**
	 * Method used to read the intersect data from the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	private boolean readIntersectData(Properties properties) {
		if (properties.getProperty("geotools.intersect_data") != null) {
			String data = properties.getProperty("geotools.intersect_data");

			if (data.isEmpty()) {
				System.out.println(INTERSECT_DATA_INVALID.description);
				return false;
			} else {
				Collections.addAll(intersectData, data.split(","));
				return true;
			}

		} else {
			System.out.println(INTERSECT_DATA_INVALID.description);
			return false;
		}
	}

	/**
	 * Method used to read the shapefile path from the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	// spotless:off
	private boolean readShapefileConfiguration(Properties properties) {
		if (properties.getProperty("geotools.shapefile_path") != null && !properties.getProperty("geotools.shapefile_path").isEmpty()) {
			shapefilePath = new File(properties.getProperty("geotools.shapefile_path"));

			if (shapefilePath.exists() && !shapefilePath.isDirectory()) {
				return true;
			} else {
				System.out.println(SHAPEFILE_PATH_INVALID.description);
				return false;
			}

		} else {
			System.out.println(SHAPEFILE_PATH_INVALID.description);
			return false;
		}
	}
	// spotless:on

	/**
	 * Method used to read database connection settings from the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	// spotless:off
	private boolean readDatabaseConfiguration(Properties properties) {
		if (properties.getProperty("geotools.database_connection") != null && !properties.getProperty("geotools.database_connection").isEmpty()) {

			if (properties.getProperty("geotools.database_username") == null && !properties.getProperty("geotools.database_username").isEmpty()) {
				System.out.println(USERNAME_INVALID.description);
				return false;
			}

			if (properties.getProperty("geotools.database_password") == null && !properties.getProperty("geotools.database_password").isEmpty()) {
				System.out.println(PASSWORD_INVALID.description);
				return false;
			}

			String connection = properties.getProperty("geotools.database_connection");
			String username = properties.getProperty("geotools.database_username");
			String password = properties.getProperty("geotools.database_password");
			databaseConnection = new Database(connection, username, password);
			databaseConnection.connect();
			return true;

		} else {
			System.out.println(DATABASE_CONNECTION_INVALID.description);
			return false;
		}
	}
	// spotless:on
}
