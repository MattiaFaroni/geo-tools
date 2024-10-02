package com.geocode.search.yaml.settings;

import static com.geocode.search.message.Alert.*;

import com.geocode.search.yaml.YamlStructure;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileSettings {

	private BufferedReader inputFile;
	private String delimiter;
	private String header;
	private int columnX;
	private int columnY;
	private int coordinateType;
	private FileWriter outputFile;

	/**
	 * Method used to read yaml configuration parameters
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	public boolean readConfigFromYaml(YamlStructure yaml) {
		inputFile = getInputFile(yaml);
		outputFile = getOutputFile(yaml);

		boolean correctSettings = false;

		if (inputFile != null && outputFile != null) {
			if (checkDelimiter(yaml) && checkHeader(yaml)) {
				getCoordinatesInfo(yaml);
				correctSettings = true;
			}
		}

		return correctSettings;
	}

	/**
	 * Method used to access the input file
	 * @param yaml configuration properties
	 * @return file to read
	 */
	private BufferedReader getInputFile(YamlStructure yaml) {
		try {
			return new BufferedReader(new FileReader(yaml.getInputFile(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			System.err.println(INPUT_PATH_INVALID.description);
			return null;
		}
	}

	/**
	 * Method used to access the output file
	 * @param yaml configuration properties
	 * @return file to write to
	 */
	private FileWriter getOutputFile(YamlStructure yaml) {
		try {
			return new FileWriter(yaml.getOutputFile(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.err.println(OUTPUT_FILE_INVALID.description);
			return null;
		}
	}

	/**
	 * Method used to check if the delimiter is valid
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	private boolean checkDelimiter(YamlStructure yaml) {
		if (yaml.getDelimiter() != null && !yaml.getDelimiter().isEmpty()) {
			delimiter = yaml.getDelimiter();
			return true;
		} else {
			System.err.println(DELIMITER_INVALID.description);
			return false;
		}
	}

	/**
	 * Method used to check if the header is valid
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	// spotless:off
	private boolean checkHeader(YamlStructure yaml) {
		if (yaml.getHeader() != null && !yaml.getHeader().isEmpty()
				&& (yaml.getHeader().equalsIgnoreCase("S")
				|| yaml.getHeader().equalsIgnoreCase("N"))) {

			header = yaml.getHeader();
			return true;
		} else {
			System.err.println(HEADER_INVALID.description);
			return false;
		}
	}
	// spotless:on

	/**
	 * Method used to get the information about coordinates
	 * @param yaml configuration properties
	 */
	private void getCoordinatesInfo(YamlStructure yaml) {
		columnX = yaml.getColumnX();
		columnY = yaml.getColumnY();
		coordinateType = yaml.getCoordinateType();
	}
}
