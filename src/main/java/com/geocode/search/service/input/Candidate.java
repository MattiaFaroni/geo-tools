package com.geocode.search.service.input;

public class Candidate {

	private double coordinateX;
	private double coordinateY;
	private int coordinateType;

	public double getCoordinateX() {
		return coordinateX;
	}

	public void setCoordinateX(double coordinateX) {
		this.coordinateX = coordinateX;
	}

	public double getCoordinateY() {
		return coordinateY;
	}

	public void setCoordinateY(double coordinateY) {
		this.coordinateY = coordinateY;
	}

	public int getCoordinateType() {
		return coordinateType;
	}

	public void setCoordinateType(int coordinateType) {
		this.coordinateType = coordinateType;
	}

	public Candidate() {}

	/**
	 * Constructor
	 * @param coordinateX coordinate X
	 * @param coordinateY coordinate Y
	 */
	public Candidate(double coordinateX, double coordinateY) {
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.coordinateType = 4326;
	}

	/**
	 * Constructor
	 * @param coordinateX coordinate X
	 * @param coordinateY coordinate Y
	 * @param coordinateType coordinate type
	 */
	public Candidate(double coordinateX, double coordinateY, int coordinateType) {
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.coordinateType = coordinateType;
	}
}
