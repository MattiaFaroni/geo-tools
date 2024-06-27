package com.geocode.search.cli.settings;

import static com.geocode.search.cli.Descriptions.*;

import java.util.Properties;

public class IntersectParameters {

    private double radius = 2;
    private double increase = 2;
    private double attempts = 100;
    private double candidates = 1;
    private double maxDistance = 50;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getIncrease() {
        return increase;
    }

    public void setIncrease(double increase) {
        this.increase = increase;
    }

    public double getAttempts() {
        return attempts;
    }

    public void setAttempts(double attempts) {
        this.attempts = attempts;
    }

    public double getCandidates() {
        return candidates;
    }

    public void setCandidates(double candidates) {
        this.candidates = candidates;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public IntersectParameters() {}

    /**
     * Constructor
     * @param radius radius in meters from the input coordinate
     * @param increase radius increase in meters
     * @param attempts number of search attempts
     * @param candidates maximum number of candidates drawn
     * @param maxDistance maximum search radius
     */
    public IntersectParameters(double radius, double increase, double attempts, double candidates, double maxDistance) {
        this.radius = radius;
        this.increase = increase;
        this.attempts = attempts;
        this.candidates = candidates;
        this.maxDistance = maxDistance;
    }

    /**
     * Method used to read settings intersect from configuration file
     * @param properties configuration file
     * @return boolean value that certifies whether the parameter values was correct
     */
    public boolean readSettingsFromProperties(Properties properties) {
        if (properties.getProperty("geotools.intersect_radius") != null
                && !properties.getProperty("geotools.intersect_radius").isEmpty()) {
            try {
                radius = Integer.parseInt(properties.getProperty("geotools.intersect_radius"));
            } catch (Exception e) {
                System.out.println(RADIUS_INVALID.description);
                return false;
            }
        }
        if (properties.getProperty("geotools.intersect_increase") != null
                && !properties.getProperty("geotools.intersect_increase").isEmpty()) {
            try {
                increase = Integer.parseInt(properties.getProperty("geotools.intersect_increase"));
            } catch (Exception e) {
                System.out.println(INCREASE_INVALID.description);
                return false;
            }
        }
        if (properties.getProperty("geotools.intersect_attempts") != null
                && !properties.getProperty("geotools.intersect_attempts").isEmpty()) {
            try {
                attempts = Integer.parseInt(properties.getProperty("geotools.intersect_attempts"));
            } catch (Exception e) {
                System.out.println(ATTEMPTS_INVALID.description);
                return false;
            }
        }
        if (properties.getProperty("geotools.intersect_candidates") != null
                && !properties.getProperty("geotools.intersect_candidates").isEmpty()) {
            try {
                candidates = Integer.parseInt(properties.getProperty("geotools.intersect_candidates"));
                if (candidates == 0) {
                    System.out.println(CANDIDATES_INVALID.description);
                    return false;
                }
            } catch (Exception e) {
                System.out.println(CANDIDATES_INVALID.description);
                return false;
            }
        }
        if (properties.getProperty("geotools.intersect_maxDistance") != null
                && !properties.getProperty("geotools.intersect_maxDistance").isEmpty()) {
            try {
                maxDistance = Integer.parseInt(properties.getProperty("geotools.intersect_maxDistance"));
            } catch (Exception e) {
                System.out.println(MAXDISTANCE_INVALID.description);
                return false;
            }
        }
        return true;
    }
}
