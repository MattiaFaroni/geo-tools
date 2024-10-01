package com.geocode.search.cli;

public enum Descriptions {
	ERROR_READ_CONFIG("Error while reading the configuration file"),
	INPUT_PATH_INVALID("Error: Invalid input_file parameter"),
	HEADER_INVALID("Error: Invalid header parameter (S/N)"),
	OUTPUT_FILE_INVALID("Error: Invalid output file"),
	DELIMITER_INVALID("Error: Invalid delimiter"),
	INTERSECT_TYPE_INVALID("Error: Invalid intersect_type parameter"),
	INTERSECT_DATA_INVALID("Error: Invalid intersect_data parameter"),
	SHAPEFILE_PATH_INVALID("Error: Invalid shapefile_path parameter"),
	DATABASE_CONNECTION_INVALID("Error: Invalid database connection parameters");

	public final String description;

	Descriptions(String description) {
		this.description = description;
	}
}
