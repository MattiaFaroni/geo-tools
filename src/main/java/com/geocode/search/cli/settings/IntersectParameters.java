package com.geocode.search.cli.settings;

import static com.geocode.search.cli.Descriptions.*;

import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntersectParameters {

	private double radius = 2;
	private double increase = 2;
	private double attempts = 100;
	private double candidates = 1;
	private double maxDistance = 50;

	/**
	 * Method used to read settings intersect from configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	// spotless:off
	public boolean readSettingsFromProperties(Properties properties) {
		if (properties.getProperty("geotools.intersect_radius") != null && !properties.getProperty("geotools.intersect_radius").isEmpty()) {
			try {
				radius = Integer.parseInt(properties.getProperty("geotools.intersect_radius"));
			} catch (Exception e) {
				System.out.println(RADIUS_INVALID.description);
				return false;
			}
		}
		if (properties.getProperty("geotools.intersect_increase") != null && !properties.getProperty("geotools.intersect_increase").isEmpty()) {
			try {
				increase = Integer.parseInt(properties.getProperty("geotools.intersect_increase"));
			} catch (Exception e) {
				System.out.println(INCREASE_INVALID.description);
				return false;
			}
		}
		if (properties.getProperty("geotools.intersect_attempts") != null && !properties.getProperty("geotools.intersect_attempts").isEmpty()) {
			try {
				attempts = Integer.parseInt(properties.getProperty("geotools.intersect_attempts"));
			} catch (Exception e) {
				System.out.println(ATTEMPTS_INVALID.description);
				return false;
			}
		}
		if (properties.getProperty("geotools.intersect_candidates") != null	&& !properties.getProperty("geotools.intersect_candidates").isEmpty()) {
			try {
				candidates = Integer.parseInt(properties.getProperty("geotools.intersect_candidates"));
				if (candidates == 0) {
					System.out.println(CANDIDATES_INVALID.description);
					return false;
				}
			} catch (Exception e) {
				System.out.println(CANDIDATES_INVALID.description);
				return false;
			}
		}
		if (properties.getProperty("geotools.intersect_maxDistance") != null && !properties.getProperty("geotools.intersect_maxDistance").isEmpty()) {
			try {
				maxDistance = Integer.parseInt(properties.getProperty("geotools.intersect_maxDistance"));
			} catch (Exception e) {
				System.out.println(MAXDISTANCE_INVALID.description);
				return false;
			}
		}
		return true;
	}
	// spotless:on
}
