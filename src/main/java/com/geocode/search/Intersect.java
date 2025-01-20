package com.geocode.search;

import static com.geocode.search.message.Alert.*;

import com.geocode.search.cli.Parameters;
import com.geocode.search.logging.Logger;
import com.geocode.search.service.Process;
import com.geocode.search.service.intersect.GeoTool;
import io.sentry.Sentry;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.cli.*;

public class Intersect extends Logger {

    private static String SENTRY_DSN;

    public static void main(String[] args) {

        Options options = setOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();
        SentryInitialized();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("version")) {
                printInfo("Version: " + getProjectVersion());

            } else if (cmd.hasOption("config")) {
                readInput(cmd, options, helper);

            } else if (cmd.hasOption("help")) {
                printHelper(options, helper);

            } else {
                printInfo("Define the path with the configuration file.");
                printHelper(options, helper);
            }

        } catch (ParseException e) {
            printError(e.getMessage());
            Sentry.captureException(e);
            printHelper(options, helper);
        }
    }

    /**
     * Method used to initialize Sentry
     */
    private static void SentryInitialized() {
        if (System.getenv("dsn") != null && !System.getenv("dsn").isEmpty()) {
            SENTRY_DSN = System.getenv("dsn");
            Sentry.init(options -> {
                options.setDsn(SENTRY_DSN);
                options.setTracesSampleRate(1.0);
            });
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
            launchProcessing(parameters, geoTool);
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
            Sentry.captureException(e);
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
		if (parameters.readInputParameters(cmd)) {
			if (parameters.getIntersectSettings().getIntersectType().equals("shapefile")) {
				geoTool = new GeoTool(parameters.getIntersectSettings().getShapefilePath().toString(), "N");
			}
		} else {
			System.exit(0);
		}
		return geoTool;
	}
	// spotless:on

    /**
     * Method used to launch the process
     * @param parameters configuration parameters
     * @param geoTool geo tools elements
     */
    private static void launchProcessing(Parameters parameters, GeoTool geoTool) {
        printInfo("Start process");
        if (parameters.getFileSettings().getHeader().equalsIgnoreCase("S")) {
            addHeader(parameters);
        }
        ArrayList<Thread> threads = launchThreads(parameters, geoTool);
        waitThreads(threads);
        printInfo("End process");
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
    private static void addHeader(Parameters parameters) {
        try {
            String inputHeader = parameters.getFileSettings().getInputFile().readLine();
            inputHeader += parameters.getFileSettings().getDelimiter();
            createOutputHeader(inputHeader, parameters);

        } catch (Exception e) {
            printError(ERROR_READING_INPUT_HEADER.description, e.getMessage());
            Sentry.captureException(e);
        }
    }

    /**
     * Method used for creating the output header
     * @param inputHeader input header
     * @param parameters configuration parameters
     */
    private static void createOutputHeader(String inputHeader, Parameters parameters) {
        try {
            String outputHeader = "";
            for (String column : parameters.getIntersectSettings().getIntersectData()) {
                outputHeader += column + parameters.getFileSettings().getDelimiter();
            }
            outputHeader = outputHeader.substring(0, outputHeader.length() - 1);

            parameters.getFileSettings().getOutputFile().write(inputHeader + outputHeader + "\n");
            parameters.getFileSettings().getOutputFile().flush();

        } catch (Exception e) {
            printError(ERROR_CREATE_OUTPUT_HEADER.description, e.getMessage());
            Sentry.captureException(e);
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
            printError(ERROR_WAIT_ALL_THREAD.description, e.getMessage());
            Sentry.captureException(e);
            System.exit(1);
        }
    }
}
