/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2017 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.shredzone.commons.suncalc;

import static java.lang.Math.*;
import static org.shredzone.commons.suncalc.util.ExtendedMath.*;

import org.shredzone.commons.suncalc.param.AbstractBuilder;
import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.Moon;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the position of the moon.
 */
public class MoonPosition {

    private final double azimuth;
    private final double altitude;
    private final double distance;
    private final double parallacticAngle;

    private MoonPosition(double azimuth, double altitude, double distance, double parallacticAngle) {
        this.azimuth = azimuth;
        this.altitude = altitude;
        this.distance = distance;
        this.parallacticAngle = parallacticAngle;
    }

    /**
     * Starts the computation of {@link MoonPosition}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new MoonPositionBuilder();
    }

    /**
     * Collects all parameters for {@link MoonPosition}.
     */
    public static interface Parameters extends
            LocationParameter<Parameters>,
            TimeParameter<Parameters>,
            Builder<MoonPosition> {
    }

    /**
     * Builder for {@link MoonPosition}. Performs the computations based on the
     * parameters, and creates a {@link MoonPosition} object that holds the result.
     */
    private static class MoonPositionBuilder extends AbstractBuilder<Parameters> implements Parameters {
        @Override
        public MoonPosition execute() {
            JulianDate t = getJulianDate();

            double phi = getLatitudeRad();
            double lambda = getLongitudeRad();

            Vector mc = Moon.position(t);
            double h = t.getGreenwichMeanSiderealTime() + lambda - mc.getPhi();

            Vector horizontal = equatorialToHorizontal(h, mc.getTheta(), mc.getR(), phi);

            double hRef = refraction(horizontal.getTheta());

            double pa = atan2(sin(h), tan(phi) * cos(mc.getTheta())) - sin(mc.getTheta()) * cos(h);

            return new MoonPosition(horizontal.getPhi(), horizontal.getTheta() + hRef, mc.getR(), pa);
        }
    }

    /**
     * Moon altitude above the horizon, in radians.
     * <p>
     * {@code 0} means the moon's center is at the horizon, {@code PI / 2} at the zenith
     * (straight over your head).
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Moon azimuth in radians.
     * <p>
     * This is the direction along the horizon, measured from south to west. For example,
     * {@code 0} means south, {@code PI * 3 / 4} means northwest, {@code PI * 6 / 4} means
     * east.
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Distance to the moon in kilometers.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Parallactic angle of the moon in radians.
     */
    public double getParallacticAngle() {
        return parallacticAngle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonPosition[azimuth=").append(azimuth);
        sb.append(", altitude=").append(altitude);
        sb.append(", distance=").append(distance);
        sb.append(", parallacticAngle=").append(parallacticAngle);
        sb.append(']');
        return sb.toString();
    }

}
