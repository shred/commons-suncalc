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
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.util.BaseBuilder;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.Moon;
import org.shredzone.commons.suncalc.util.Sun;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the illumination of the moon.
 */
public class MoonIllumination {

    private final double fraction;
    private final double phase;
    private final double angle;

    private MoonIllumination(double fraction, double phase, double angle) {
        this.fraction = fraction;
        this.phase = phase;
        this.angle = angle;
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
            Vector s = Sun.position(t);
            Vector m = Moon.position(t);
            double phi = PI - acos(m.dot(s) / (m.getR() * s.getR()));
            Vector sunMoon = m.cross(s);

            return new MoonIllumination(
                            (1 + cos(phi)) / 2,
                            toDegrees(phi * signum(sunMoon.getTheta())),
                            toDegrees(sunMoon.getTheta()));
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonIllumination[fraction=").append(fraction);
        sb.append(", phase=").append(phase);
        sb.append("°, angle=").append(angle);
        sb.append("°]");
        return sb.toString();
    }

}
