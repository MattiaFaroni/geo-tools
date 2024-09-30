package com.geocode.search.cli;

import static com.geocode.search.cli.Descriptions.*;

import com.geocode.search.cli.settings.FileConfiguration;
import com.geocode.search.cli.settings.IntersectModel;
import com.geocode.search.cli.settings.IntersectParameters;
import java.io.*;
import java.util.Properties;
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

	private FileConfiguration fileConfiguration = new FileConfiguration();
	private IntersectModel intersectModel = new IntersectModel();
	private IntersectParameters intersectParameters = new IntersectParameters();
	private int threads = 1;

	/**
	 * Method used to read parameters from the configuration file
	 * @param cmd command line
	 * @return boolean value that certifies whether the parameter reading was successful
	 */
	public boolean readParams(CommandLine cmd) {
		try {
			if (cmd.getOptionValue("thread") != null) {
				threads = Integer.parseInt(cmd.getOptionValue("thread"));
			}
			String configFilePath = cmd.getOptionValue("config");
			FileInputStream fileConfig = new FileInputStream(configFilePath);
			Properties properties = new Properties();
			properties.load(fileConfig);

			if (fileConfiguration.readConfigFromProperties(properties)) {
				if (intersectModel.readConfigFromProperties(properties)) {
					return intersectParameters.readSettingsFromProperties(properties);
				}
			}
			return false;

		} catch (FileNotFoundException e) {
			System.out.println(FILE_CONFIG_INVALID.description);
			return false;
		} catch (IOException e) {
			System.out.println(ERROR_READ_CONFIG.description);
			return false;
		}
	}

	/**
	 * Method used to close all open connections
	 */
	public void closeAllConnection() {
		try {
			fileConfiguration.getInputFile().close();
			fileConfiguration.getOutputFile().close();
			if (intersectModel.getDatabaseConnection() != null) {
				intersectModel.getDatabaseConnection().closeConnection();
			}
		} catch (Exception e) {
			System.out.println("Error while closing all connections. Description: " + e.getMessage());
		}
	}
}
