package com.geocode.search.message;

public enum Alert {

	ERROR_READ_CONFIG("Error while reading the configuration file"),
	INPUT_PATH_INVALID("Error: Invalid input_file parameter"),
	HEADER_INVALID("Error: Invalid header parameter (S/N)"),
	OUTPUT_FILE_INVALID("Error: Invalid output file"),
	DELIMITER_INVALID("Error: Invalid delimiter"),
	INTERSECT_TYPE_INVALID("Error: Invalid intersect_type parameter"),
	INTERSECT_DATA_INVALID("Error: Invalid intersect_data parameter"),
	SHAPEFILE_PATH_INVALID("Error: Invalid shapefile_path parameter"),
	DATABASE_URL_INVALID("Error: Invalid database_url parameter"),
	DATABASE_CONNECTION_INVALID("Error: Invalid database connection parameters"),
	ERROR_CONNECTION_TO_DATABASE("Error while connecting to the database"),
	ERROR_CLOSE_DATABASE_CONNECTION("Error while closing the database connection"),
	INVALID_COORDINATE_X_POSITION("Error: Invalid coordinate X position"),
	INVALID_COORDINATE_Y_POSITION("Error: Invalid coordinate Y position"),
	ERROR_READING_INPUT_HEADER("Error while reading the input header"),
	ERROR_CREATE_OUTPUT_HEADER("Error while creating the output header"),
	ERROR_WAIT_ALL_THREAD("Error while waiting for all threads to finish"),
	ERROR_CLOSING_CONNECTIONS("Error while closing all the connection"),
	ERROR_UPLOAD_SHAPEFILE("Error while uploading the shape file"),
	ERROR_INTERSECT_SHAPEFILE("Error while intersecting the shapefile"),
	ERROR_EXTRACT_DATA_SHAPEFILE("Error while extracting data from shapefile"),
	ERROR_INTERSECT_DATABASE("Error while intersecting the database"),
	ERROR_READING_CSV("Error while reading the CSV file"),
	ERROR_EXTRACT_DATA_DATABASE("Error while extracting data from database"),
	ERROR_WRITE_FILE_OUTPUT("Error while writing the output file");

	public final String description;

	Alert(String description) {
		this.description = description;
	}
}
