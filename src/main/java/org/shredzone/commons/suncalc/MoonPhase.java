/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2018 Richard "Shred" Körber
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

import static java.lang.Math.PI;
import static java.lang.Math.toRadians;
import static org.shredzone.commons.suncalc.util.ExtendedMath.PI2;

import java.util.Date;

import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.GenericParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.param.TimeResultParameter;
import org.shredzone.commons.suncalc.util.BaseBuilder;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.Moon;
import org.shredzone.commons.suncalc.util.Pegasus;
import org.shredzone.commons.suncalc.util.Sun;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the date and time when the moon reaches the desired phase.
 * <p>
 * Note: Due to the simplified formulas used in suncalc, the returned time can have an
 * error of several minutes.
 *
 * @since 2.3
 */
public class MoonPhase {

    private final Date time;
    private final double distance;

    private MoonPhase(Date time, double distance) {
        this.time = time;
        this.distance = distance;
    }

    /**
     * Starts the computation of {@link MoonPhase}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new MoonPhaseBuilder();
    }

    /**
     * Collects all parameters for {@link MoonPhase}.
     *
     * @since 2.3
     */
    public interface Parameters extends
            GenericParameter<Parameters>,
            TimeParameter<Parameters>,
            TimeResultParameter<Parameters>,
            Builder<MoonPhase> {

        /**
         * Sets the desired {@link Phase}.
         * <p>
         * Defaults to {@link Phase#NEW_MOON}.
         *
         * @param phase
         *            {@link Phase} to be used.
         * @return itself
         */
        Parameters phase(Phase phase);

        /**
         * Sets a free phase to be used.
         *
         * @param phase
         *            Desired phase, in degrees. 0 = new moon, 90 = first quarter, 180 =
         *            full moon, 270 = third quarter.
         * @return itself
         */
        Parameters phase(double phase);
    }

    /**
     * Enumeration of moon phases.
     *
     * @since 2.3
     */
    public enum Phase {

        /**
         * New moon.
         */
        NEW_MOON(0.0),

        /**
         * Waxing crescent moon.
         *
         * @since 2.12
         */
        WAXING_CRESCENT(45.0),

        /**
         * Waxing half moon.
         */
        FIRST_QUARTER(90.0),

        /**
         * Waxing gibbous moon.
         *
         * @since 2.12
         */
        WAXING_GIBBOUS(135.0),

        /**
         * Full moon.
         */
        FULL_MOON(180.0),

        /**
         * Waning gibbous moon.
         *
         * @since 2.12
         */
        WANING_GIBBOUS(225.0),

        /**
         * Waning half moon.
         */
        LAST_QUARTER(270.0),

        /**
         * Waning crescent moon.
         *
         * @since 2.12
         */
        WANING_CRESCENT(315.0);

        /**
         * Converts an angle to the closest matching moon phase.
         *
         * @param angle
         *         Moon phase angle, in degrees. 0 = New Moon, 180 = Full Moon. Angles
         *         outside the [0,360) range are normalized into that range.
         * @return Closest Phase that is matching that angle.
         * @since 2.12
         */
        public static Phase toPhase(double angle) {
            // bring into range 0.0 .. 360.0
            double normalized = angle % 360.0;
            if (normalized < 0.0) {
                normalized += 360.0;
            }

            if (normalized < 22.5) {
                return NEW_MOON;
            }
            if (normalized < 67.5) {
                return WAXING_CRESCENT;
            }
            if (normalized < 112.5) {
                return FIRST_QUARTER;
            }
            if (normalized < 157.5) {
                return WAXING_GIBBOUS;
            }
            if (normalized < 202.5) {
                return FULL_MOON;
            }
            if (normalized < 247.5) {
                return WANING_GIBBOUS;
            }
            if (normalized < 292.5) {
                return LAST_QUARTER;
            }
            if (normalized < 337.5) {
                return WANING_CRESCENT;
            }
            return NEW_MOON;
        }

        private final double angle;
        private final double angleRad;

        Phase(double angle) {
            this.angle = angle;
            this.angleRad = toRadians(angle);
        }

        /**
         * Returns the moons's angle in reference to the sun, in degrees.
         */
        public double getAngle() {
            return angle;
        }

        /**
         * Returns the moons's angle in reference to the sun, in radians.
         */
        public double getAngleRad() {
            return angleRad;
        }
    }

    /**
     * Builder for {@link MoonPhase}. Performs the computations based on the parameters,
     * and creates a {@link MoonPhase} object that holds the result.
     */
    private static class MoonPhaseBuilder extends BaseBuilder<Parameters> implements Parameters {
        private static final double SUN_LIGHT_TIME_TAU = 8.32 / (1440.0 * 36525.0);

        private double phase = Phase.NEW_MOON.getAngleRad();

        @Override
        public Parameters phase(Phase phase) {
            this.phase = phase.getAngleRad();
            return this;
        }

        @Override
        public Parameters phase(double phase) {
            this.phase = toRadians(phase);
            return this;
        }

        @Override
        public MoonPhase execute() {
            final JulianDate jd = getJulianDate();

            double dT = 7.0 / 36525.0;                      // step rate: 1 week
            double accuracy = (0.5 / 1440.0) / 36525.0;     // accuracy: 30 seconds

            double t0 = jd.getJulianCentury();
            double t1 = t0 + dT;

            double d0 = moonphase(jd, t0);
            double d1 = moonphase(jd, t1);

            while (d0 * d1 > 0.0 || d1 < d0) {
                t0 = t1;
                d0 = d1;
                t1 += dT;
                d1 = moonphase(jd, t1);
            }

            double tphase = Pegasus.calculate(t0, t1, accuracy, new Pegasus.Function() {
                @Override
                public double apply(double x) {
                    return moonphase(jd, x);
                }
            });

            JulianDate tjd = jd.atJulianCentury(tphase);
            return new MoonPhase(tjd.getDateTruncated(getTruncatedTo()), Moon.positionEquatorial(tjd).getR());
        }

        /**
         * Calculates the position of the moon at the given phase.
         *
         * @param jd
         *            Base Julian date
         * @param t
         *            Ephemeris time
         * @return difference angle of the sun's and moon's position
         */
        private double moonphase(JulianDate jd, double t) {
            Vector sun = Sun.positionEquatorial(jd.atJulianCentury(t - SUN_LIGHT_TIME_TAU));
            Vector moon = Moon.positionEquatorial(jd.atJulianCentury(t));
            double diff = moon.getPhi() - sun.getPhi() - phase; //NOSONAR: false positive
            while (diff < 0.0) {
                diff += PI2;
            }
            return ((diff + PI) % PI2) - PI;
        }

    }

    /**
     * Date and time of the desired moon phase. The time is rounded to full minutes.
     */
    public Date getTime() {
        return new Date(time.getTime());
    }

    /**
     * Geocentric distance of the moon at the given phase, in kilometers.
     *
     * @since 2.11
     */
    public double getDistance() { return distance; }

    /**
     * Checks if the moon is in a SuperMoon position.
     * <p>
     * Note that there is no official definition of supermoon. Suncalc will assume a
     * supermoon if the center of the moon is closer than 360,000 km to the center of
     * Earth. Usually only full moons or new moons are candidates for supermoons.
     *
     * @since 2.11
     */
    public boolean isSuperMoon() {
        return distance < 360000.0;
    }

    /**
     * Checks if the moon is in a MicroMoon position.
     * <p>
     * Note that there is no official definition of micromoon. Suncalc will assume a
     * micromoon if the center of the moon is farther than 405,000 km from the center of
     * Earth. Usually only full moons or new moons are candidates for micromoons.
     *
     * @since 2.11
     */
    public boolean isMicroMoon() {
        return distance > 405000.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonPhase[time=").append(time);
        sb.append(", distance=").append(distance);
        sb.append(" km]");
        return sb.toString();
    }

}
