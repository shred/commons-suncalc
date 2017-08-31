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

import static java.lang.Math.toRadians;
import static org.shredzone.commons.suncalc.util.ExtendedMath.*;

import org.shredzone.commons.suncalc.param.AbstractBuilder;
import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.Sun;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the position of the sun.
 */
public class SunPosition {

    private final double azimuth;
    private final double altitude;

    private SunPosition(double azimuth, double altitude) {
        this.azimuth = azimuth;
        this.altitude = altitude;
    }

    /**
     * Starts the computation of {@link SunPosition}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new SunPositionBuilder();
    }

    /**
     * Collects all parameters for {@link SunPosition}.
     */
    public static interface Parameters extends
            LocationParameter<Parameters>,
            TimeParameter<Parameters>,
            Builder<SunPosition> {
    }

    /**
     * Builder for {@link SunPosition}. Performs the computations based on the parameters,
     * and creates a {@link SunPosition} object that holds the result.
     */
    private static class SunPositionBuilder extends AbstractBuilder<Parameters> implements Parameters {
        @Override
        public SunPosition execute() {
            JulianDate t = getJulianDate();

            double lw = toRadians(-getLongitude());
            double phi = toRadians(getLatitude());
            Vector c = Sun.position(t);
            double h = t.getGreenwichMeanSiderealTime() - lw - c.getPhi();
            Vector horizontal = equatorialToHorizontal(h, c.getTheta(), c.getR(), phi);
            double hRef = refraction(horizontal.getTheta());

            return new SunPosition(horizontal.getPhi(), horizontal.getTheta() + hRef);
        }
    }

    /**
     * Sun altitude above the horizon, in radians.
     * <p>
     * {@code 0} means the sun's center is at the horizon, {@code PI / 2} at the zenith
     * (straight over your head).
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Sun azimuth in radians.
     * <p>
     * This is the direction along the horizon, measured from south to west. For example,
     * {@code 0} means south, {@code PI * 3 / 4} means northwest, {@code PI * 6 / 4} means
     * east.
     */
    public double getAzimuth() {
        return azimuth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunPosition[azimuth=").append(azimuth);
        sb.append(", altitude=").append(altitude);
        sb.append(']');
        return sb.toString();
    }

}
