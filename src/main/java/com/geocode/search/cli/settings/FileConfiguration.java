package com.geocode.search.cli.settings;

import static com.geocode.search.cli.Descriptions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FileConfiguration {

	private BufferedReader inputFile;
	private String delimiter;
	private String header;
	private int columnX;
	private int columnY;
	private int coordinateType;
	private FileWriter outputFile;

	public BufferedReader getInputFile() {
		return inputFile;
	}

	public void setInputFile(BufferedReader inputFile) {
		this.inputFile = inputFile;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getColumnX() {
		return columnX;
	}

	public void setColumnX(int columnX) {
		this.columnX = columnX;
	}

	public int getColumnY() {
		return columnY;
	}

	public void setColumnY(int columnY) {
		this.columnY = columnY;
	}

	public int getCoordinateType() {
		return coordinateType;
	}

	public void setCoordinateType(int coordinateType) {
		this.coordinateType = coordinateType;
	}

	public FileWriter getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(FileWriter outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * Method used to read the settings on the input file from the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	public boolean readConfigFromProperties(Properties properties) {
		FileReader fileReader = getFileReader(properties);
		if (fileReader != null) {
			if (getCsvReader(fileReader, properties)) {
				if (readFileInputConfiguration(properties)) {
					return getOutputFile(properties);
				}
			}
		}
		return false;
	}

	/**
	 * Method used to access the input file
	 * @param properties configuration file
	 * @return file to read
	 */
	private FileReader getFileReader(Properties properties) {
		try {
			return new FileReader(properties.getProperty("geotools.input_file"), StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.out.println(INPUT_PATH_INVALID.description);
			return null;
		}
	}

	/**
	 * Method used to read the configuration of input file
	 * @param fileReader input file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	private boolean getCsvReader(FileReader fileReader, Properties properties) {
		if (properties.getProperty("geotools.header") != null) {
			header = properties.getProperty("geotools.header");

			if (!header.equalsIgnoreCase("S") && !header.equalsIgnoreCase("N")) {
				System.out.println(HEADER_INVALID.description);
				return false;
			} else {
				delimiter = properties.getProperty("geotools.delimiter");
				inputFile = new BufferedReader(fileReader);
				return true;
			}
		} else {
			System.out.println(HEADER_INVALID.description);
			return false;
		}
	}

	/**
	 * Method used to read input file properties from configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	private boolean readFileInputConfiguration(Properties properties) {
		if (properties.getProperty("geotools.column_x") != null) {
			columnX = Integer.parseInt(properties.getProperty("geotools.column_x"));
		} else {
			System.out.println(COLUMN_X_INVALID.description);
			return false;
		}
		if (properties.getProperty("geotools.column_y") != null) {
			columnY = Integer.parseInt(properties.getProperty("geotools.column_y"));
		} else {
			System.out.println(COLUMN_Y_INVALID.description);
			return false;
		}
		if (properties.getProperty("geotools.coordinate_type") != null) {
			try {
				coordinateType = Integer.parseInt(properties.getProperty("geotools.coordinate_type"));
			} catch (Exception e) {
				System.out.println(COORDINATE_TYPE_INVALID.description);
				return false;
			}
		} else {
			coordinateType = 4326;
		}
		return true;
	}

	/**
	 * Method used to create the output file specified in the configuration file
	 * @param properties configuration file
	 * @return boolean value that certifies whether the parameter values was correct
	 */
	private boolean getOutputFile(Properties properties) {
		try {
			outputFile = new FileWriter(properties.getProperty("geotools.output_file"), StandardCharsets.UTF_8);
			return true;
		} catch (Exception e) {
			System.out.println(OUTPUT_FILE_INVALID.description);
			return false;
		}
	}
}
