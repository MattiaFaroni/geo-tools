package com.geocode.search.cli;

import static com.geocode.search.cli.Descriptions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.geocode.search.cli.yaml.YamlStructure;
import com.geocode.search.cli.yaml.settings.FileSettings;
import com.geocode.search.cli.yaml.settings.IntersectParameters;
import com.geocode.search.cli.yaml.settings.IntersectSettings;
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
	private IntersectParameters intersectParameters = new IntersectParameters();
	private int threads = 1;

	/**
	 * Method used to read parameters from the configuration file
	 * @param cmd command line
	 * @return boolean value indicating whether the parameter is correct
	 */
	// spotless:off
	public boolean readParams(CommandLine cmd) {
		if (cmd.getOptionValue("thread") != null) {
			threads = Integer.parseInt(cmd.getOptionValue("thread"));
		}

		String configFilePath = cmd.getOptionValue("config");
		YamlStructure yamlStructure = readYaml(configFilePath);

		boolean correctSettings = false;

		if (yamlStructure != null) {
			if (fileSettings.readConfigFromYaml(yamlStructure) && intersectSettings.readConfigFromYaml(yamlStructure)) {
				intersectParameters.readConfigFromYaml(yamlStructure);
				correctSettings = true;
			}
		}
		return correctSettings;
	}
	// spotless:on

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
			System.out.println(ERROR_READ_CONFIG.description);
			return null;
		}
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
			System.out.println("Error while closing all connections. Description: " + e.getMessage());
		}
	}
}
