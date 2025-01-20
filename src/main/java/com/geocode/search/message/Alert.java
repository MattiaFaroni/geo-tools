package com.geocode.search.message;

public enum Alert {
    ERROR_READING_INPUT_HEADER("Error reading input file header."),
    ERROR_CREATE_OUTPUT_HEADER("Error while creating the output header."),
    ERROR_WAIT_ALL_THREAD("Error while waiting for all threads to finish."),
    ERROR_READ_CONFIG("Error while reading the configuration file."),
    ERROR_CLOSING_CONNECTIONS("Error while closing all the connections."),
    DATABASE_URL_INVALID("Invalid database_url parameter."),
    ERROR_CONNECTION_TO_DATABASE("Error while connecting to the database."),
    ERROR_CLOSE_DATABASE_CONNECTION("Error while closing the database connection."),
    ERROR_UPLOAD_SHAPEFILE("Error while uploading the shapefile."),
    ERROR_INTERSECT_SHAPEFILE("Error while intersecting the shapefile."),
    ERROR_EXTRACT_DATA_SHAPEFILE("Error while extracting data from shapefile."),
    ERROR_INTERSECT_DATABASE("Error while intersecting a point on the database."),
    ERROR_READING_CSV("Error while reading the CSV file."),
    INVALID_COORDINATE_X_POSITION("Invalid coordinate X position."),
    INVALID_COORDINATE_Y_POSITION("Invalid coordinate Y position."),
    ERROR_EXTRACT_DATA_DATABASE("Error while extracting data from database."),
    ERROR_WRITE_FILE_OUTPUT("Error while writing the output file."),
    INPUT_PATH_INVALID("Invalid input_file parameter."),
    OUTPUT_FILE_INVALID("Invalid output file."),
    DELIMITER_INVALID("Invalid delimiter."),
    HEADER_INVALID("Invalid header parameter (S/N)."),
    INTERSECT_TYPE_INVALID("Invalid intersect_type parameter."),
    INTERSECT_DATA_INVALID("Invalid intersect_data parameter."),
    SHAPEFILE_PATH_INVALID("Invalid shapefile_path parameter."),
    DATABASE_CONNECTION_INVALID("Invalid database connection parameters");

    public final String description;

    Alert(String description) {
        this.description = description;
    }
}
