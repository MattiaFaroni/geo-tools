package com.geocode.search;

import com.geocode.search.cli.Parameters;
import com.geocode.search.service.Process;
import com.geocode.search.service.intersect.GeoTool;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.cli.*;

public class Intersect {

	public static void main(String[] args) {

		Options options = setOptions();
		CommandLineParser parser = new DefaultParser();
		HelpFormatter helper = new HelpFormatter();

		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("version")) {
				System.out.println("Version: " + getProjectVersion());
			} else if (cmd.hasOption("config")) {
				if (cmd.hasOption("help")) {
					printHelper(options, helper);
				} else {
					Parameters parameters = new Parameters();
					GeoTool geoTool = readConfiguration(parameters, cmd);
					startProcessing(parameters, geoTool);
					parameters.closeAllConnection();
				}
			} else if (cmd.hasOption("help")) {
				printHelper(options, helper);
			} else {
				System.out.println("Define the path with the configuration file.");
				printHelper(options, helper);
			}
		} catch (ParseException e) {
			System.err.println("Error: " + e.getMessage());
			printHelper(options, helper);
		}
	}

	/**
	 * Method used to set the options available
	 * @return options set
	 */
	private static Options setOptions() {
		Options options = new Options();
		options.addOption("v", "version", false, "Show the version of the project");
		options.addOption("c", "config", true, "Indicates the path to the configuration file");
		options.addOption("t", "thread", true, "Specify number of thread (default: 1)");
		options.addOption("h", "help", false, "Show arguments");
		return options;
	}

	/**
	 * Method used to retrieve the project version
	 * @return project version
	 */
	private static String getProjectVersion() {
		try {
			Properties properties = new Properties();
			properties.load(Intersect.class.getResourceAsStream("/build.properties"));
			return properties.getProperty("version");
		} catch (Exception e) {
			return "Unable to retrieve project version";
		}
	}

	/**
	 * Method used to print the help command on the screen
	 * @param options list of available parameters
	 * @param helper helper formatter
	 */
	private static void printHelper(Options options, HelpFormatter helper) {
		helper.printHelp("[-v] [-c config] [-t thread]", options);
		System.exit(0);
	}

	/**
	 * Method used to read configuration parameters
	 * @param parameters configuration parameters
	 * @param cmd command line
	 * @return geo tools elements
	 */
	private static GeoTool readConfiguration(Parameters parameters, CommandLine cmd) {
		GeoTool geoTool = new GeoTool();
		if (parameters.readParams(cmd)) {
			if (parameters.getIntersectModel().getIntersectType().equals("shapefile")) {
				geoTool = new GeoTool(
						parameters.getIntersectModel().getShapefilePath().toString(), "N");
			}
		} else {
			System.exit(0);
		}
		return geoTool;
	}

	/**
	 * Method used to start the process
	 * @param parameters configuration parameters
	 * @param geoTool geo tools elements
	 */
	private static void startProcessing(Parameters parameters, GeoTool geoTool) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("----------------------------------");
		System.out.println("Start process: " + formatter.format(now));
		System.out.println("----------------------------------");

		if (parameters.getFileConfiguration().getHeader().equalsIgnoreCase("S")) {
			addHeader(parameters);
		}
		ArrayList<Thread> threads = new ArrayList<>();
		Process process = new Process(parameters, geoTool);
		for (int i = 0; i < parameters.getThreads(); i++) {
			Thread thread = new Thread(process);
			threads.add(thread);
			thread.start();
		}
		waitThreads(threads);

		now = LocalDateTime.now();
		System.out.println("----------------------------------");
		System.out.println("End process:   " + formatter.format(now));
		System.out.println("----------------------------------");
	}

	/**
	 * Method used to add header to output file
	 * @param parameters configuration parameters
	 */
	private static void addHeader(Parameters parameters) {
		try {
			String inputHeader =
					parameters.getFileConfiguration().getInputFile().readLine();
			inputHeader += parameters.getFileConfiguration().getDelimiter();

			String outputHeader = "";
			for (String column : parameters.getIntersectModel().getIntersectData()) {
				outputHeader += column + parameters.getFileConfiguration().getDelimiter();
			}
			outputHeader = outputHeader.substring(0, outputHeader.length() - 1);

			parameters.getFileConfiguration().getOutputFile().write(inputHeader + outputHeader + "\n");
			parameters.getFileConfiguration().getOutputFile().flush();

		} catch (Exception e) {
			System.out.println("Error when adding header. Description: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Method used to wait for all threads to complete processing
	 * @param threads active threads list
	 */
	private static void waitThreads(ArrayList<Thread> threads) {
		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			System.out.println("Error: waiting for completion of all threads failed. Description: " + e.getMessage());
			System.exit(1);
		}
	}
}
