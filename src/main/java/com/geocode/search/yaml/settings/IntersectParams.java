package com.geocode.search.yaml.settings;

import com.geocode.search.yaml.YamlStructure;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntersectParams {

	private double radius = 2;
	private double increase = 2;
	private double attempts = 100;
	private double candidates = 1;
	private double maxDistance = 50;

	/**
	 * Method used to read yaml configuration parameters
	 * @param yaml configuration properties
	 */
	public void readConfigFromYaml(YamlStructure yaml) {
		YamlStructure.ParametersConf parametersConf = yaml.getIntersect().getParameters();

		if (parametersConf.getRadius() != 0.0) {
			radius = parametersConf.getRadius();
		}
		if (parametersConf.getIncrease() != 0.0) {
			increase = parametersConf.getIncrease();
		}
		if (parametersConf.getAttempts() != 0.0) {
			attempts = parametersConf.getAttempts();
		}
		if (parametersConf.getCandidates() != 0.0) {
			candidates = parametersConf.getCandidates();
		}
		if (parametersConf.getMaxDistance() != 0.0) {
			maxDistance = parametersConf.getMaxDistance();
		}
	}
}
