package com.geocode.search.yaml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YamlStructure {

	private String inputFile;
	private String outputFile;
	private String delimiter;
	private String header;
	private int columnX;
	private int columnY;
	private int coordinateType;
	private IntersectConf intersect;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IntersectConf {
		private String type;
		private String data;
		private ShapefileConf shapefile;
		private DatabaseConf database;
		private ParametersConf parameters;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ShapefileConf {
		private String path;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DatabaseConf {
		private String url;
		private String username;
		private String password;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ParametersConf {
		private double radius;
		private double increase;
		private double attempts;
		private double candidates;
		private double maxDistance;
	}
}
