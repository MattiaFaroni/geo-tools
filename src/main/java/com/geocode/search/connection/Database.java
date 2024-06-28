package com.geocode.search.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.Properties;
import org.postgresql.PGProperty;

public class Database {

	private String url;
	private String username;
	private String password;
	private String host;
	private String port;
	private String database;
	private String schema;
	private String table;
	private Connection connection;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Constructor
	 * @param url database connection url
	 * @param username database username
	 * @param password database password
	 */
	public Database(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		Properties props = org.postgresql.Driver.parseURL(url, null);
		this.host = Objects.requireNonNull(props).getProperty(PGProperty.PG_HOST.getName());
		this.port = props.getProperty(PGProperty.PG_PORT.getName());
		this.database = props.getProperty(PGProperty.PG_DBNAME.getName());

		if (props.getProperty(PGProperty.CURRENT_SCHEMA.getName()).contains(",")) {
			String[] elements =
					props.getProperty(PGProperty.CURRENT_SCHEMA.getName()).split(",");
			this.table = elements[0];
			this.schema = elements[1];

		} else {
			System.out.println("Database url not valid");
			System.out.println("Use this template: jdbc:postgresql://host:port/database?currentSchema=table,schema");
			System.exit(99);
		}
	}

	/**
	 * Method used to connect to postgresql database
	 */
	public void connect() {
		connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println("Error when connecting to the database. Description: " + e.getMessage());
		}
	}

	/**
	 * Method used to terminate the database connection
	 */
	public void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			System.out.println("Error while closing database connection: Description: " + e.getMessage());
		}
	}
}
