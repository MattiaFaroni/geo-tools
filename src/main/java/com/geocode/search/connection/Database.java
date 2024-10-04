package com.geocode.search.connection;

import static com.geocode.search.message.Alert.*;

import com.geocode.search.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.postgresql.PGProperty;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Database extends Logger {

	private String url;
	private String username;
	private String password;
	private String host;
	private String port;
	private String database;
	private String schema;
	private String table;
	private Connection connection;

	/**
	 * Constructor
	 * @param url database connection url
	 * @param username database username
	 * @param password database password
	 */
	// spotless:off
	public Database(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		Properties props = org.postgresql.Driver.parseURL(url, null);
		host = Objects.requireNonNull(props).getProperty(PGProperty.PG_HOST.getName());
		port = props.getProperty(PGProperty.PG_PORT.getName());
		database = props.getProperty(PGProperty.PG_DBNAME.getName());

		if (props.getProperty(PGProperty.CURRENT_SCHEMA.getName()).contains(",")) {
			String[] elements = props.getProperty(PGProperty.CURRENT_SCHEMA.getName()).split(",");
			this.table = elements[0];
			this.schema = elements[1];

		} else {
			printError(DATABASE_URL_INVALID.description);
			System.exit(99);
		}
	}
	// spotless:on

	/**
	 * Method used to connect to postgresql database
	 */
	public void connect() {
		connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			printError(ERROR_CONNECTION_TO_DATABASE.description, e.getMessage());
		}
	}

	/**
	 * Method used to terminate the database connection
	 */
	public void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			printError(ERROR_CLOSE_DATABASE_CONNECTION.description, e.getMessage());
		}
	}
}
