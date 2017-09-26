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
import static org.shredzone.commons.suncalc.util.ExtendedMath.APPARENT_REFRACTION;

import java.util.Date;

import org.shredzone.commons.suncalc.param.BaseBuilder;
import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.QuadraticInterpolation;
import org.shredzone.commons.suncalc.util.Sun;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the rise and set times of the sun.
 */
public class SunTimes {

    private final Date rise;
    private final Date set;
    private final Date noon;
    private final Date nadir;

    private SunTimes(Date rise, Date set, Date noon, Date nadir) {
        this.rise = rise;
        this.set = set;
        this.noon = noon;
        this.nadir = nadir;
    }

    /**
     * Starts the computation of {@link SunTimes}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new SunTimesBuilder();
    }

    /**
     * Collects all parameters for {@link SunTimes}.
     */
    public static interface Parameters extends
            LocationParameter<Parameters>,
            TimeParameter<Parameters>,
            Builder<SunTimes> {

        /**
         * Sets the {@link Twilight} mode.
         * <p>
         * Defaults to {@value Twilight#VISUAL}.
         *
         * @param twilight
         *            {@link Twilight} mode to be used.
         * @return itself
         */
        Parameters twilight(Twilight twilight);

        /**
         * Sets the desired elevation angle of the sun. The sunrise and sunset times are
         * referring to the moment where the sun passes this angle.
         *
         * @param angle
         *            Geocentric elevation angle, in degrees.
         * @return itself
         */
        Parameters twilight(double angle);

        /**
         * Checks only the next 24 hours. Rise, set, noon or nadir times can be
         * {@code null} if the sun never reaches the point during one day (e.g. at
         * solstice).
         * <p>
         * This is the default.
         *
         * @return itself
         */
        Parameters oneDay();

        /**
         * Computes until rise, set, noon, and nadir times are found, even if the sun
         * needs more than a day for it. This can considerably increase computation time.
         *
         * @return itself
         */
        Parameters fullCycle();
    }

    /**
     * Enumeration of predefined twilights.
     * <p>
     * The twilight angles use a geocentric reference, by definition. However,
     * {@link #VISUAL} and {@link #VISUAL_LOWER} are topocentric, and take atmospheric
     * refraction into account.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Twilight">Wikipedia: Twilight</a>
     */
    public enum Twilight {

        /**
         * The moment when the visual upper edge of the sun crosses the horizon. This is
         * commonly referred to as "sunrise" and "sunset". Atmospheric refraction is taken
         * into account.
         * <p>
         * This is the default.
         */
        VISUAL(0.0, 1.0),

        /**
         * The moment when the visual lower edge of the sun crosses the horizon. This is
         * the ending of the sunrise and the starting of the sunset. Atmospheric
         * refraction is taken into account.
         */
        VISUAL_LOWER(0.0, -1.0),

        /**
         * The moment when the center of the sun crosses the horizon (0°).
         */
        HORIZON(0.0),

        /**
         * Civil twilight (-6°).
         */
        CIVIL(-6.0),

        /**
         * Nautical twilight (-12°).
         */
        NAUTICAL(-12.0),

        /**
         * Astronomical twilight (-18°).
         */
        ASTRONOMICAL(-18.0),

        /**
         * Golden hour (6°). The Golden hour is between {@link #GOLDEN_HOUR} and
         * {@link #BLUE_HOUR}. The Magic hour is between {@link #GOLDEN_HOUR} and
         * {@link #CIVIL}.
         *
         * @see <a href=
         *      "https://en.wikipedia.org/wiki/Golden_hour_(photography)">Wikipedia:
         *      Golden hour</a>
         */
        GOLDEN_HOUR(6.0),

        /**
         * Blue hour (-4°). The Blue hour is between {@link #CIVIL} and
         * {@link #BLUE_HOUR}.
         *
         * @see <a href="https://en.wikipedia.org/wiki/Blue_hour">Wikipedia: Blue hour</a>
         */
        BLUE_HOUR(-4.0);

        private final double angle;
        private final Double position;

        private Twilight(double angle) {
            this(angle, null);
        }

        private Twilight(double angle, Double position) {
            this.angle = angle;
            this.position = position;
        }

        /**
         * Returns the sun's angle at the twilight position, in degrees.
         */
        public double getAngle() {
            return angle;
        }

        /**
         * Returns {@code true} if this twilight position is topocentric. Then the
         * parallax and the atmospheric refraction is taken into account.
         */
        public boolean isTopocentric() {
            return position != null;
        }

        /**
         * Returns the angular position. {@code 0.0} means center of the sun. {@code 1.0}
         * means upper edge of the sun. {@code -1.0} means lower edge of the sun.
         */
        private Double getAngularPosition() {
            return position;
        }
    }

    /**
     * Builder for {@link SunTimes}. Performs the computations based on the parameters,
     * and creates a {@link SunTimes} object that holds the result.
     */
    private static class SunTimesBuilder extends BaseBuilder<Parameters> implements Parameters {
        private double angle = Twilight.VISUAL.getAngle();
        private Double position = Twilight.VISUAL.getAngularPosition();
        private boolean fullCycle = false;

        @Override
        public Parameters twilight(Twilight twilight) {
            this.angle = twilight.getAngle();
            this.position = twilight.getAngularPosition();
            return this;
        }

        @Override
        public Parameters twilight(double angle) {
            this.angle = angle;
            this.position = null;
            return this;
        }

        @Override
        public Parameters oneDay() {
            this.fullCycle = false;
            return this;
        }

        @Override
        public Parameters fullCycle() {
            this.fullCycle = true;
            return this;
        }

        @Override
        public SunTimes execute() {
            JulianDate jd = getJulianDate();
            double lat = getLatitudeRad();
            double lng = getLongitudeRad();

            Vector startPosition = Sun.positionHorizontal(jd, lat, lng);

            double hc = toRadians(angle);
            if (position != null) {
                hc += Sun.parallax(getHeight(), startPosition.getR());
                hc -= APPARENT_REFRACTION;
                hc -= position * Sun.angularRadius(startPosition.getR());
            }

            double y_minus = startPosition.getTheta() - hc;
            Double rise = null;
            Double set = null;
            Double noon = null;
            Double nadir = null;
            double ye;

            int maxHours = fullCycle ? 365 * 24 : 24;

            for (int hour = 1; hour <= maxHours; hour += 2) {
                JulianDate jdH0 = jd.atHour(hour);
                JulianDate jdH1 = jd.atHour(hour + 1.0);
                double y_0 = Sun.positionHorizontal(jdH0, lat, lng).getTheta() - hc;
                double y_plus = Sun.positionHorizontal(jdH1, lat, lng).getTheta() - hc;

                QuadraticInterpolation qi = new QuadraticInterpolation(y_minus, y_0, y_plus);
                ye = qi.getYe();

                if (qi.getNumberOfRoots() == 1) {
                    if (y_minus < 0.0) {
                        rise = qi.getRoot1() + hour;
                    } else {
                        set = qi.getRoot1() + hour;
                    }
                } else if (qi.getNumberOfRoots() == 2) {
                    rise = hour + (ye < 0.0 ? qi.getRoot2() : qi.getRoot1());
                    set = hour + (ye < 0.0 ? qi.getRoot1() : qi.getRoot2());
                }

                double xe = qi.getXe();
                if (xe > -1.01 && xe < 1.01) {
                    if (ye < 0.0) {
                        nadir = xe + hour;
                    } else {
                        noon = xe + hour;
                    }
                }

                if (rise != null && set != null && nadir != null && noon != null) {
                    break;
                }

                y_minus = y_plus;
            }

            return new SunTimes(
                    rise != null ? jd.atHour(rise).getDate() : null,
                    set != null ? jd.atHour(set).getDate() : null,
                    noon != null ? jd.atHour(noon).getDate() : null,
                    nadir != null ? jd.atHour(nadir).getDate() : null);
        }
    }

    /**
     * Sunrise time. {@code null} if the sun does not rise that day.
     */
    public Date getRise() {
        return rise != null ? new Date(rise.getTime()) : null;
    }

    /**
     * Sunset time. {@code null} if the sun does not set that day.
     */
    public Date getSet() {
        return set != null ? new Date(set.getTime()) : null;
    }

    /**
     * Gets the time when the sun reaches its highest point. {@code null} if the sun
     * never rises on that day.
     */
    public Date getNoon() {
        return noon;
    }

    /**
     * Gets the time when the sun reaches its lowest point. {@code null} if the sun
     * never sets on that day.
     */
    public Date getNadir() {
        return nadir;
    }

    /**
     * {@code true} if the sun never rises/sets, but is always above the twilight angle
     * that day.
     */
    public boolean isAlwaysUp() {
        return rise == null && set == null && noon != null;
    }

    /**
     * {@code true} if the sun never rises/sets, but is always below the twilight angle
     * that day.
     */
    public boolean isAlwaysDown() {
        return rise == null && set == null && nadir != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunTimes[rise=").append(rise);
        sb.append(", set=").append(set);
        sb.append(", noon=").append(noon);
        sb.append(", nadir=").append(nadir);
        sb.append(", alwaysUp=").append(isAlwaysUp());
        sb.append(", alwaysDown=").append(isAlwaysDown());
        sb.append(']');
        return sb.toString();
    }

}
