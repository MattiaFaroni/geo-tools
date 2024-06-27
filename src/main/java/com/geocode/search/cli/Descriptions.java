package com.geocode.search.cli;

public enum Descriptions {
    FILE_CONFIG_INVALID("Error: The path to the config file is invalid"),
    ERROR_READ_CONFIG("Error while reading the configuration file"),
    INPUT_PATH_INVALID("Error: Invalid input_file parameter"),
    HEADER_INVALID("Error: Invalid header parameter (S/N)"),
    COLUMN_X_INVALID("Error: Invalid x_column parameter"),
    COLUMN_Y_INVALID("Error: Invalid y_column parameter"),
    COORDINATE_TYPE_INVALID("Error: Invalid coordinate_type parameter"),
    OUTPUT_FILE_INVALID("Error: Invalid output file"),
    INTERSECT_TYPE_INVALID("Error: Invalid intersect_type parameter"),
    INTERSECT_DATA_INVALID("Error: Invalid intersect_data parameter"),
    SHAPEFILE_PATH_INVALID("Error: Invalid shapefile_path parameter"),
    DATABASE_CONNECTION_INVALID("Error: Inavlid database_connection parameter"),
    USERNAME_INVALID("Error: Inavlid database_username parameter"),
    PASSWORD_INVALID("Error: Inavlid database_password parameter"),
    RADIUS_INVALID("Error: Invalid shapefile_radius parameter"),
    INCREASE_INVALID("Error: Invalid shapefile_increase parameter"),
    ATTEMPTS_INVALID("Error: Invalid shapefile_attempts parameter"),
    CANDIDATES_INVALID("Error: Invalid shapefile_candidates parameter"),
    MAXDISTANCE_INVALID("Error: Invalid shapefile_maxDistance parameter");

    public final String description;

    Descriptions(String description) {
        this.description = description;
    }
}
