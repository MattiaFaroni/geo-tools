package com.geocode.search.service;

import static com.geocode.search.message.Alert.*;

import com.geocode.search.cli.Parameters;
import com.geocode.search.logging.Logger;
import com.geocode.search.service.input.Candidate;
import com.geocode.search.service.intersect.GeoTool;
import com.geocode.search.service.output.IntersectResult;
import io.sentry.Sentry;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opengis.feature.simple.SimpleFeature;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Process extends Logger implements Runnable {

	private Parameters parameters;
	private GeoTool geoTool;
	private static int rowCount = 0;

	@Override
	public void run() {
		String line;
		try {
			while ((line = parameters.getFileSettings().getInputFile().readLine()) != null) {
				executeIntersect(line);

				rowCount++;
				synchronized (this) {
					if (rowCount % 1000 == 0) {
						printInfo("Processed records: " + rowCount);
					}
				}
			}
		} catch (IOException e) {
			printError(ERROR_READING_CSV.description, e.getMessage());
			Sentry.captureException(e);
		}
	}

	/**
	 * Method used to perform intersect on shapefile or database
	 * @param line record of the input file to be processed
	 */
	// spotless:off
	private void executeIntersect(String line) {
		Candidate candidate = readCoordinates(line);
		IntersectResult intersectResult;

		switch (parameters.getIntersectSettings().getIntersectType()) {
			case "shapefile":
				intersectResult = geoTool.extractDataFromShapefile(candidate, parameters.getIntersectParams());
				generateShapefileOutput(intersectResult, line);
				break;
			case "database":
				intersectResult = geoTool.extractDataFromDatabase(candidate, parameters.getIntersectSettings().getDatabaseConnection(), parameters.getIntersectParams().getCandidates(), parameters.getIntersectSettings().getIntersectData());
				addDatabaseResultToFile(intersectResult, line);
				break;
		}
	}
	// spotless:on

	/**
	 * Method used to read the coordinates and type
	 * @param line record of the input file to be processed
	 * @return extracted candidate
	 */
	// spotless:off
	private Candidate readCoordinates(String line) {
		Candidate candidate = new Candidate();
		String delimiter = parameters.getFileSettings().getDelimiter();
		if (delimiter.equals("|")) {
			delimiter = "\\|";
		}
		String[] elements = line.split(delimiter);

		try {
			candidate.setCoordinateX(Double.parseDouble(elements[parameters.getFileSettings().getColumnX()]));
		} catch (Exception e) {
			printError(INVALID_COORDINATE_X_POSITION.description);
			System.exit(1);
		}
		try {
			candidate.setCoordinateY(Double.parseDouble(elements[parameters.getFileSettings().getColumnY()]));
		} catch (Exception e) {
			printError(INVALID_COORDINATE_Y_POSITION.description);
			System.exit(1);
		}

		candidate.setCoordinateType(parameters.getFileSettings().getCoordinateType());
		return candidate;
	}
	// spotless:on

	/**
	 * Method used to generate the output of the shapefile
	 * @param intersectResult result of the reverse operation
	 * @param line input file string
	 */
	private void generateShapefileOutput(IntersectResult intersectResult, String line) {
		int candidateNumber = 0;
		String shapefileResult = "";
		if (!intersectResult.getShapeElements().isEmpty()) {
			for (SimpleFeature simpleFeature : intersectResult.getShapeElements()) {

				if (candidateNumber <= parameters.getIntersectParams().getCandidates()) {

					for (String data : parameters.getIntersectSettings().getIntersectData()) {
						shapefileResult += simpleFeature.getAttribute(data);
						shapefileResult += parameters.getFileSettings().getDelimiter();
					}
					candidateNumber++;

					shapefileResult = shapefileResult.substring(0, shapefileResult.length() - 1);
					String inputRecord = generateInputRecord(line);
					writeOutputToTheFile(inputRecord, shapefileResult);
				}
			}
		} else {
			String inputRecord = generateInputRecord(line);
			shapefileResult = generateOutputEmpty();
			writeOutputToTheFile(inputRecord, shapefileResult);
		}
	}

	/**
	 * Method used to generate the output of the database
	 * @param intersectResult result of the reverse operation
	 * @param line input file string
	 */
	private void addDatabaseResultToFile(IntersectResult intersectResult, String line) {
		try {
			while (intersectResult.getDbElements().next()) {

				String databaseResult = "";
				for (String data : parameters.getIntersectSettings().getIntersectData()) {
					databaseResult += intersectResult.getDbElements().getString(data);
					databaseResult += parameters.getFileSettings().getDelimiter();
				}

				databaseResult = databaseResult.substring(0, databaseResult.length() - 1);
				String inputRecord = generateInputRecord(line);
				writeOutputToTheFile(inputRecord, databaseResult);
			}
			intersectResult.getDbElements().close();

		} catch (Exception e) {
			printError(ERROR_EXTRACT_DATA_DATABASE.description, e.getMessage());
			Sentry.captureException(e);
			System.exit(1);
		}
	}

	/**
	 * Method used to generate the input record to be reported in the output file
	 * @param line input file string
	 * @return string to be reported in output
	 */
	private String generateInputRecord(String line) {
		return line + parameters.getFileSettings().getDelimiter();
	}

	/**
	 * Method used to generate output when nothing is found within the shapefile
	 * @return string to be reported in output
	 */
	// spotless:off
	private String generateOutputEmpty() {
		return String.valueOf(parameters.getFileSettings().getDelimiter()).repeat(parameters.getIntersectSettings().getIntersectData().size() - 1);
	}
	// spotless:on

	/**
	 * Method used to report the result to the output file
	 * @param inputRecord input record
	 * @param reverseElements reverse result or header
	 */
	private void writeOutputToTheFile(String inputRecord, String reverseElements) {
		try {
			parameters.getFileSettings().getOutputFile().write(inputRecord + reverseElements + "\n");
			parameters.getFileSettings().getOutputFile().flush();

		} catch (Exception e) {
			printError(ERROR_WRITE_FILE_OUTPUT.description, e.getMessage());
			Sentry.captureException(e);
			System.exit(1);
		}
	}
}
