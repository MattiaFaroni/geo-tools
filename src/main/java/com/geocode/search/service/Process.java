package com.geocode.search.service;

import com.geocode.search.cli.Parameters;
import com.geocode.search.service.input.Candidate;
import com.geocode.search.service.intersect.GeoTool;
import com.geocode.search.service.output.IntersectResult;
import java.io.IOException;
import org.opengis.feature.simple.SimpleFeature;

public class Process implements Runnable {

	private Parameters parameters;
	private GeoTool geoTool;
	private static int rowCount = 0;

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public GeoTool getGeoTool() {
		return geoTool;
	}

	public void setGeoTool(GeoTool geoTool) {
		this.geoTool = geoTool;
	}

	public static int getRowCount() {
		return rowCount;
	}

	public static void setRowCount(int rowCount) {
		Process.rowCount = rowCount;
	}

	/**
	 * Constructor
	 * @param parameters input params
	 * @param geoTool shapefile to use
	 */
	public Process(Parameters parameters, GeoTool geoTool) {
		this.parameters = parameters;
		this.geoTool = geoTool;
	}

	@Override
	public void run() {
		String line;
		try {
			while ((line = parameters.getFileConfiguration().getInputFile().readLine()) != null) {
				executeIntersect(line);

				rowCount++;
				synchronized (this) {
					if (rowCount % 1000 == 0) {
						System.out.println("Processed records: " + rowCount);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error when reading input csv file");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method used to perform intersect on shapefile or database
	 * @param line record of the input file to be processed
	 */
	private void executeIntersect(String line) {
		Candidate candidate = readCoordinates(line);
		if (parameters.getIntersectModel().getIntersectType().equalsIgnoreCase("shapefile")) {
			IntersectResult intersectResult =
					geoTool.extractDataFromShapefile(candidate, parameters.getIntersectParameters());
			generateShapefileOutput(intersectResult, line);
		} else if (parameters.getIntersectModel().getIntersectType().equalsIgnoreCase("database")) {
			IntersectResult intersectResult = geoTool.extractDataFromDatabase(
					candidate,
					parameters.getIntersectModel().getDatabaseConnection(),
					parameters.getIntersectParameters().getCandidates(),
					parameters.getIntersectModel().getIntersectData());
			addDatabaseResultToFile(intersectResult, line);
		}
	}

	/**
	 * Method used to read the coordinates and type
	 * @param line record of the input file to be processed
	 * @return extracted candidate
	 */
	private Candidate readCoordinates(String line) {
		Candidate candidate = new Candidate();
		String delimiter = parameters.getFileConfiguration().getDelimiter();
		if (delimiter.equals("|")) {
			delimiter = "\\|";
		}
		String[] elements = line.split(delimiter);
		try {
			candidate.setCoordinateX(Double.parseDouble(
					elements[parameters.getFileConfiguration().getColumnX()]));
		} catch (Exception e) {
			System.out.println("ERROR: invalid position or x coordinate");
			System.exit(1);
		}
		try {
			candidate.setCoordinateY(Double.parseDouble(
					elements[parameters.getFileConfiguration().getColumnY()]));
		} catch (Exception e) {
			System.out.println("ERROR: invalid position or y coordinate");
			System.exit(1);
		}
		candidate.setCoordinateType(parameters.getFileConfiguration().getCoordinateType());
		return candidate;
	}

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

				if (candidateNumber <= parameters.getIntersectParameters().getCandidates()) {

					for (String data : parameters.getIntersectModel().getIntersectData()) {
						shapefileResult += simpleFeature.getAttribute(data);
						shapefileResult += parameters.getFileConfiguration().getDelimiter();
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
				for (String data : parameters.getIntersectModel().getIntersectData()) {
					databaseResult += intersectResult.getDbElements().getString(data);
					databaseResult += parameters.getFileConfiguration().getDelimiter();
				}

				databaseResult = databaseResult.substring(0, databaseResult.length() - 1);
				String inputRecord = generateInputRecord(line);
				writeOutputToTheFile(inputRecord, databaseResult);
			}
			intersectResult.getDbElements().close();
		} catch (Exception e) {
			System.out.println("ERROR: extraction of data from the database failed. Description: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Method used to generate the input record to be reported in the output file
	 * @param line input file string
	 * @return string to be reported in output
	 */
	private String generateInputRecord(String line) {
		return line + parameters.getFileConfiguration().getDelimiter();
	}

	/**
	 * Method used to generate output when nothing is found within the shapefile
	 * @return string to be reported in output
	 */
	private String generateOutputEmpty() {
		return String.valueOf(parameters.getFileConfiguration().getDelimiter())
				.repeat(parameters.getIntersectModel().getIntersectData().size() - 1);
	}

	/**
	 * Method used to report the result to the output file
	 * @param inputRecord input record
	 * @param reverseElements reverse result or header
	 */
	private void writeOutputToTheFile(String inputRecord, String reverseElements) {
		try {
			parameters.getFileConfiguration().getOutputFile().write(inputRecord + reverseElements + "\n");
			parameters.getFileConfiguration().getOutputFile().flush();
		} catch (Exception e) {
			System.out.println("ERROR: write to output file failed. Description: " + e.getMessage());
			System.exit(1);
		}
	}
}
