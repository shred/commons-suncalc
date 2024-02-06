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

import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.GenericParameter;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.util.BaseBuilder;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.Moon;
import org.shredzone.commons.suncalc.util.Sun;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the illumination of the moon.
 * <p>
 * Starting with v3.9, a geolocation can be set optionally. If set, the results will be
 * topocentric, relative to the given location. If not set, the result is geocentric,
 * which was the standard behavior before v3.9.
 */
public class MoonIllumination {

    private final double fraction;
    private final double phase;
    private final double angle;
    private final double elongation;
    private final double radius;
    private final double crescentWidth;

    private MoonIllumination(double fraction, double phase, double angle,
                             double elongation, double radius, double crescentWidth) {
        this.fraction = fraction;
        this.phase = phase;
        this.angle = angle;
        this.elongation = elongation;
        this.radius = radius;
        this.crescentWidth = crescentWidth;
    }

    /**
     * Starts the computation of {@link MoonIllumination}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new MoonIlluminationBuilder();
    }

    /**
     * Collects all parameters for {@link MoonIllumination}.
     */
    public interface Parameters extends
            GenericParameter<Parameters>,
            TimeParameter<Parameters>,
            LocationParameter<Parameters>,
            Builder<MoonIllumination> {
    }

    /**
     * Builder for {@link MoonIllumination}. Performs the computations based on the
     * parameters, and creates a {@link MoonIllumination} object that holds the result.
     */
    private static class MoonIlluminationBuilder extends BaseBuilder<Parameters> implements Parameters {
        @Override
        public MoonIllumination execute() {
            JulianDate t = getJulianDate();
            Vector s, m;
            if (hasLocation()) {
                s = Sun.positionTopocentric(t, getLatitudeRad(), getLongitudeRad(), getElevation());
                m = Moon.positionTopocentric(t, getLatitudeRad(), getLongitudeRad(), getElevation());
            } else {
                s = Sun.position(t);
                m = Moon.position(t);
            }

            double phi = PI - acos(m.dot(s) / (m.getR() * s.getR()));
            Vector sunMoon = m.cross(s);
            double angle = atan2(
                    cos(s.getTheta()) * sin(s.getPhi() - m.getPhi()),
                    sin(s.getTheta()) * cos(m.getTheta()) - cos(s.getTheta()) * sin(m.getTheta()) * cos(s.getPhi() - m.getPhi())
            );

            double r = m.subtract(s).norm();
            double re = s.norm();
            double d = m.norm();
            double elongation = acos((d*d + re*re - r*r) / (2.0*d*re));

            double moonRadius = Moon.angularRadius(m.getR());
            double crescentWidth = moonRadius * (1 - cos(elongation));

            return new MoonIllumination(
                            (1 + cos(phi)) / 2,
                            toDegrees(phi * signum(sunMoon.getTheta())),
                            toDegrees(angle),
                            toDegrees(elongation),
                            toDegrees(moonRadius),
                            toDegrees(crescentWidth));
        }
    }

    /**
     * Illuminated fraction. {@code 0.0} indicates new moon, {@code 1.0} indicates full
     * moon.
     */
    public double getFraction() {
        return fraction;
    }

    /**
     * Moon phase. Starts at {@code -180.0} (new moon, waxing), passes {@code 0.0} (full
     * moon) and moves toward {@code 180.0} (waning, new moon).
     * <p>
     * Note that for historical reasons, the range of this phase is different to the
     * moon phase angle used in {@link MoonPhase}.
     */
    public double getPhase() {
        return phase;
    }

    /**
     * The angle of the moon illumination relative to earth. The moon is waxing if the
     * angle is negative, and waning if positive.
     * <p>
     * By subtracting {@link MoonPosition#getParallacticAngle()} from {@link #getAngle()},
     * one can get the zenith angle of the moons bright limb (anticlockwise). The zenith
     * angle can be used do draw the moon shape from the observer's perspective (e.g. the
     * moon lying on its back).
     */
    public double getAngle() {
        return angle;
    }

    /**
     * The closest {@link MoonPhase.Phase} that is matching the moon's angle.
     *
     * @return Closest {@link MoonPhase.Phase}
     * @since 3.5
     */
    public MoonPhase.Phase getClosestPhase() {
        return MoonPhase.Phase.toPhase(phase + 180.0);
    }

    /**
     * The elongation, which is the angular distance between the moon and the sun as
     * observed from a specific location on earth.
     *
     * @return Elongation between moon and sun, in degrees.
     * @since 3.9
     */
    public double getElongation() {
        return elongation;
    }

    /**
     * The radius of the moon disk, as observed from a specific location on earth.
     *
     * @return Moon radius, in degrees.
     * @since 3.9
     */
    public double getRadius() {
        return radius;
    }

    /**
     * The width of the moon crescent, as observed from a specific location on earth.
     *
     * @return Crescent width, in degrees.
     * @since 3.9
     */
    public double getCrescentWidth() {
        return crescentWidth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonIllumination[fraction=").append(fraction);
        sb.append(", phase=").append(phase);
        sb.append("°, angle=").append(angle);
        sb.append("°, elongation=").append(elongation);
        sb.append("°, radius=").append(radius);
        sb.append("°, crescentWidth=").append(crescentWidth);
        sb.append("°]");
        return sb.toString();
    }

}
