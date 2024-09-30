package com.geocode.search.service.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

	private double coordinateX;
	private double coordinateY;
	private int coordinateType;

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
}
