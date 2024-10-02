package com.geocode.search.cli;

import static com.geocode.search.message.Alert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.geocode.search.yaml.YamlStructure;
import com.geocode.search.yaml.settings.FileSettings;
import com.geocode.search.yaml.settings.IntersectParams;
import com.geocode.search.yaml.settings.IntersectSettings;
import java.io.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Parameters {

	private FileSettings fileSettings = new FileSettings();
	private IntersectSettings intersectSettings = new IntersectSettings();
	private IntersectParams intersectParams = new IntersectParams();
	private int threads = 1;

	/**
	 * Method used to read parameters from the configuration file
	 * @param cmd command line
	 * @return boolean value indicating whether the parameter is correct
	 */
	public boolean readInputParameters(CommandLine cmd) {
		if (cmd.getOptionValue("thread") != null) {
			threads = Integer.parseInt(cmd.getOptionValue("thread"));
		}

		String configFilePath = cmd.getOptionValue("config");
		YamlStructure yamlStructure = readYaml(configFilePath);

		if (yamlStructure != null) {
			return readConfigFromYaml(yamlStructure);
		} else {
			return false;
		}
	}

	/**
	 * Method used to read the .yaml configuration file
	 * @param path configuration file path
	 * @return data contained in the configuration file
	 */
	private YamlStructure readYaml(String path) {
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			mapper.findAndRegisterModules();
			return mapper.readValue(new File(path), YamlStructure.class);
		} catch (Exception e) {
			System.err.println(ERROR_READ_CONFIG.description);
			return null;
		}
	}

	/**
	 * Method used to read yaml configuration parameters
	 * @param yaml configuration properties
	 * @return boolean value indicating whether the parameter is correct
	 */
	private boolean readConfigFromYaml(YamlStructure yaml) {
		boolean correctSettings = fileSettings.readConfigFromYaml(yaml);
		if (correctSettings) {
			correctSettings = intersectSettings.readConfigFromYaml(yaml);
		}
		if (correctSettings) {
			intersectParams.readConfigFromYaml(yaml);
		}
		return correctSettings;
	}

	/**
	 * Method used to close all open connections
	 */
	public void closeAllConnection() {
		try {
			fileSettings.getInputFile().close();
			fileSettings.getOutputFile().close();
			if (intersectSettings.getDatabaseConnection() != null) {
				intersectSettings.getDatabaseConnection().closeConnection();
			}
		} catch (Exception e) {
			System.err.println(ERROR_CLOSING_CONNECTIONS.description);
			System.err.println("Description: " + e.getMessage());
		}
	}
}
