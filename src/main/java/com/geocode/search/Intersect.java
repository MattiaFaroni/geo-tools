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
				readInput(cmd, options, helper);

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
	 * Method used to read input parameters
	 * @param cmd command line
	 * @param options list of available parameters
	 * @param helper helper formatter
	 */
	private static void readInput(CommandLine cmd, Options options, HelpFormatter helper) {
		if (cmd.hasOption("help")) {
			printHelper(options, helper);
		} else {
			Parameters parameters = new Parameters();
			GeoTool geoTool = readConfiguration(parameters, cmd);
			startProcessing(parameters, geoTool);
			parameters.closeAllConnection();
		}
	}

	/**
	 * Method used to set the options available
	 * @return options set
	 */
	// spotless:off
	private static Options setOptions() {
		Options options = new Options();
		options.addOption("v", "version", false, "Show the version of the project");
		options.addOption("c", "config", true, "Indicates the path to the configuration file");
		options.addOption("t", "thread", true, "Specify number of thread (default: 1)");
		options.addOption("h", "help", false, "Show arguments");
		return options;
	}
	// spotless:on

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
	// spotless:off
	private static GeoTool readConfiguration(Parameters parameters, CommandLine cmd) {
		GeoTool geoTool = new GeoTool();
		if (parameters.readParams(cmd)) {
			if (parameters.getIntersectModel().getIntersectType().equals("shapefile")) {
				geoTool = new GeoTool(parameters.getIntersectModel().getShapefilePath().toString(), "N");
			}
		} else {
			System.exit(0);
		}
		return geoTool;
	}
	// spotless:on

	/**
	 * Method used to start the process
	 * @param parameters configuration parameters
	 * @param geoTool geo tools elements
	 */
	private static void startProcessing(Parameters parameters, GeoTool geoTool) {
		printStart();
		if (parameters.getFileConfiguration().getHeader().equalsIgnoreCase("S")) {
			addHeader(parameters);
		}
		ArrayList<Thread> threads = launchThreads(parameters, geoTool);
		waitThreads(threads);
		printEnd();
	}

	/**
	 * Method used to print the start of the process
	 */
	private static void printStart() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("----------------------------------");
		System.out.println("Start process: " + formatter.format(now));
		System.out.println("----------------------------------");
	}

	/**
	 * Method used to print the end of the process
	 */
	private static void printEnd() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("----------------------------------");
		System.out.println("End process:   " + formatter.format(now));
		System.out.println("----------------------------------");
	}

	/**
	 * Method used to launch threads
	 * @param parameters configuration parameters
	 * @param geoTool geo tools elements
	 * @return list of running threads
	 */
	private static ArrayList<Thread> launchThreads(Parameters parameters, GeoTool geoTool) {
		ArrayList<Thread> threads = new ArrayList<>();
		Process process = new Process(parameters, geoTool);
		for (int i = 0; i < parameters.getThreads(); i++) {
			Thread thread = new Thread(process);
			threads.add(thread);
			thread.start();
		}
		return threads;
	}

	/**
	 * Method used to add header to output file
	 * @param parameters configuration parameters
	 */
	// spotless:off
	private static void addHeader(Parameters parameters) {
		try {
			String inputHeader = parameters.getFileConfiguration().getInputFile().readLine();
			inputHeader += parameters.getFileConfiguration().getDelimiter();
			createOutputHeader(inputHeader, parameters);

		} catch (Exception e) {
			System.err.println("Error reading input header: " + e.getMessage());
		}
	}
	// spotless:on

	/**
	 * Method used for creating the output header
	 * @param inputHeader input header
	 * @param parameters configuration parameters
	 */
	private static void createOutputHeader(String inputHeader, Parameters parameters) {
		try {
			String outputHeader = "";
			for (String column : parameters.getIntersectModel().getIntersectData()) {
				outputHeader += column + parameters.getFileConfiguration().getDelimiter();
			}
			outputHeader = outputHeader.substring(0, outputHeader.length() - 1);

			parameters.getFileConfiguration().getOutputFile().write(inputHeader + outputHeader + "\n");
			parameters.getFileConfiguration().getOutputFile().flush();

		} catch (Exception e) {
			System.out.println("Error when create output header. Description: " + e.getMessage());
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
