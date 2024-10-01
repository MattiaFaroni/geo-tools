package com.geocode.search.cli.yaml.settings;

import static com.geocode.search.cli.Descriptions.*;

import com.geocode.search.cli.yaml.YamlStructure;
import com.geocode.search.connection.Database;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntersectSettings {

	private String intersectType;
	private ArrayList<String> intersectData = new ArrayList<>();
	private File shapefilePath;
	private Database databaseConnection;

	/**
	 * Method used to read yaml configuration parameters
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	public boolean readConfigFromYaml(YamlStructure yaml) {
		boolean correctSettings = false;

		if (checkIntersectType(yaml) && checkIntersectData(yaml)) {
			correctSettings = switch (intersectType) {
				case "shapefile" -> checkShapefilePath(yaml);
				case "database" -> checkDatabaseConnection(yaml);
				default -> false;};
		}
		return correctSettings;
	}

	/**
	 * Method used to check if the intersect type is valid
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	// spotless:off
	private boolean checkIntersectType(YamlStructure yaml) {
		if (yaml.getIntersect().getType().equals("shapefile") || yaml.getIntersect().getType().equals("database")) {
			intersectType = yaml.getIntersect().getType();
			return true;
		} else {
			System.out.println(INTERSECT_TYPE_INVALID.description);
			return false;
		}
	}
	// spotless:on

	/**
	 * Method used to check if the intersect data is valid
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	private boolean checkIntersectData(YamlStructure yaml) {
		YamlStructure.IntersectConf intersectConf = yaml.getIntersect();

		if (intersectConf.getData() != null && !intersectConf.getData().isEmpty()) {
			Collections.addAll(intersectData, intersectConf.getData().split(","));
			return true;
		} else {
			System.out.println(INTERSECT_DATA_INVALID.description);
			return false;
		}
	}

	/**
	 * Method used to check if the shapefile path is valid
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	private boolean checkShapefilePath(YamlStructure yaml) {
		YamlStructure.ShapefileConf shapefileConf = yaml.getIntersect().getShapefile();

		if (shapefileConf.getPath() != null && !shapefileConf.getPath().isEmpty()) {
			shapefilePath = new File(shapefileConf.getPath());

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

	/**
	 * Method used to check if the database connection is valid
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	// spotless:off
	private boolean checkDatabaseConnection(YamlStructure yaml) {
		YamlStructure.DatabaseConf databaseConf = yaml.getIntersect().getDatabase();

		if (databaseConf.getUrl() != null && !databaseConf.getUrl().isEmpty()
				&& databaseConf.getUsername() != null && !databaseConf.getUsername().isEmpty()
				&& databaseConf.getPassword() != null && !databaseConf.getPassword().isEmpty()) {

			databaseConnection = new Database(databaseConf.getUrl(), databaseConf.getUsername(), databaseConf.getPassword());
			return true;

		} else {
			System.out.println(DATABASE_CONNECTION_INVALID.description);
			return false;
		}
	}
	// spotless:on
}
