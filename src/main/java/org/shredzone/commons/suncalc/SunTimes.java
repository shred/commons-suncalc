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

import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.param.TimeResultParameter;
import org.shredzone.commons.suncalc.util.BaseBuilder;
import org.shredzone.commons.suncalc.util.JulianDate;
import org.shredzone.commons.suncalc.util.QuadraticInterpolation;
import org.shredzone.commons.suncalc.util.Sun;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the rise and set times of the sun.
 */
@ParametersAreNonnullByDefault
@Immutable
public class SunTimes {

    private final Date rise;
    private final Date set;
    private final Date noon;
    private final Date nadir;
    private final boolean alwaysUp;
    private final boolean alwaysDown;

    private SunTimes(Date rise, Date set, Date noon, Date nadir, boolean alwaysUp,
            boolean alwaysDown) {
        this.rise = rise;
        this.set = set;
        this.noon = noon;
        this.nadir = nadir;
        this.alwaysUp = alwaysUp;
        this.alwaysDown = alwaysDown;
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
            TimeResultParameter<Parameters>,
            Builder<SunTimes> {

        /**
         * Sets the {@link Twilight} mode.
         * <p>
         * Defaults to {@link Twilight#VISUAL}.
         *
         * @param twilight
         *            {@link Twilight} mode to be used.
         * @return itself
         */
        Parameters twilight(Twilight twilight);

        /**
         * Sets the desired elevation angle of the sun. The sunrise and sunset times are
         * referring to the moment when the center of the sun passes this angle.
         *
         * @param angle
         *            Geocentric elevation angle, in degrees.
         * @return itself
         */
        Parameters twilight(double angle);

        /**
         * Checks only the next 24 hours. Rise or set time can be {@code null} if the sun
         * never reaches the point during one day (e.g. at solstice).
         * <p>
         * This is the default.
         *
         * @return itself
         */
        Parameters oneDay();

        /**
         * Computes until rise and set times are found, even if the sun needs more than a
         * day for it. This can considerably increase computation time.
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
        private final double angleRad;
        private final Double position;

        private Twilight(double angle) {
            this(angle, null);
        }

        private Twilight(double angle, Double position) {
            this.angle = angle;
            this.angleRad = toRadians(angle);
            this.position = position;
        }

        /**
         * Returns the sun's angle at the twilight position, in degrees.
         */
        public double getAngle() {
            return angle;
        }

        /**
         * Returns the sun's angle at the twilight position, in radians.
         */
        public double getAngleRad() {
            return angleRad;
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
         * {@code null} means the angular position is not topocentric.
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
        private double angle = Twilight.VISUAL.getAngleRad();
        private Double position = Twilight.VISUAL.getAngularPosition();
        private boolean fullCycle = false;
        private double refraction = apparentRefraction(0.0);

        @Override
        public Parameters twilight(Twilight twilight) {
            this.angle = twilight.getAngleRad();
            this.position = twilight.getAngularPosition();
            return this;
        }

        @Override
        public Parameters twilight(double angle) {
            this.angle = toRadians(angle);
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

            Double rise = null;
            Double set = null;
            Double noon = null;
            Double nadir = null;
            double ye;
            double lastXeAbs = Double.MAX_VALUE;
            double noonXeAbs = Double.MAX_VALUE;
            double nadirXeAbs = Double.MAX_VALUE;
            double noonYe = 0.0;
            double nadirYe = 0.0;

            double y_minus = correctedSunHeight(jd);

            int maxHours = fullCycle ? 365 * 24 : 24;
            for (int hour = 1; hour < maxHours; hour += 2) {
                double y_0 = correctedSunHeight(jd.atHour(hour));
                double y_plus = correctedSunHeight(jd.atHour(hour + 1.0));

                QuadraticInterpolation qi = new QuadraticInterpolation(y_minus, y_0, y_plus);
                ye = qi.getYe();

                if (qi.getNumberOfRoots() == 1) {
                    if (y_minus < 0.0) {
                        if (rise == null) {
                            rise = qi.getRoot1() + hour;
                        }
                    } else {
                        if (set == null) {
                            set = qi.getRoot1() + hour;
                        }
                    }
                } else if (qi.getNumberOfRoots() == 2) {
                    if (rise == null) {
                        rise = hour + (ye < 0.0 ? qi.getRoot2() : qi.getRoot1());
                    }
                    if (set == null) {
                        set = hour + (ye < 0.0 ? qi.getRoot1() : qi.getRoot2());
                    }
                }

                if (hour < 24) {
                    double xeAbs = Math.abs(qi.getXe());
                    if (xeAbs < lastXeAbs) {
                        double xeHour = qi.getXe() + hour;
                        if (qi.isMaximum() && xeAbs < noonXeAbs) {
                            noon = xeHour;
                            noonXeAbs = xeAbs;
                            noonYe = ye;
                        } else if (!qi.isMaximum() && xeAbs < nadirXeAbs) {
                            nadir = xeHour;
                            nadirXeAbs = xeAbs;
                            nadirYe = ye;
                        }
                    }
                    lastXeAbs = xeAbs;
                }

                if (hour >= 24 && rise != null && set != null) {
                    break;
                }

                y_minus = y_plus;
            }

            return new SunTimes(
                    rise != null ? jd.atHour(rise).getDateTruncated(getTruncatedTo()) : null,
                    set != null ? jd.atHour(set).getDateTruncated(getTruncatedTo()) : null,
                    noon != null ? jd.atHour(noon).getDateTruncated(getTruncatedTo()) : null,
                    nadir != null ? jd.atHour(nadir).getDateTruncated(getTruncatedTo()) : null,
                    nadir == null || nadirYe > 0.0,
                    noon == null || noonYe < 0.0
                );
        }

        /**
         * Computes the sun height at the given date and position.
         *
         * @param jd {@link JulianDate} to use
         * @return height, in radians
         */
        private double correctedSunHeight(JulianDate jd) {
            Vector pos = Sun.positionHorizontal(jd, getLatitudeRad(), getLongitudeRad());

            double hc = angle;
            if (position != null) {
                hc += parallax(getHeight(), pos.getR());
                hc -= refraction;
                hc -= position * Sun.angularRadius(pos.getR());
            }

            return pos.getTheta() - hc;
        }
    }

    /**
     * Sunrise time. {@code null} if the sun does not rise that day.
     * <p>
     * Always returns a sunrise time if {@link Parameters#fullCycle()} was set.
     */
    @CheckForNull
    public Date getRise() {
        return rise != null ? new Date(rise.getTime()) : null;
    }

    /**
     * Sunset time. {@code null} if the sun does not set that day.
     * <p>
     * Always returns a sunset time if {@link Parameters#fullCycle()} was set.
     */
    @CheckForNull
    public Date getSet() {
        return set != null ? new Date(set.getTime()) : null;
    }

    /**
     * The time when the sun reaches its highest point within the next 24 hours.
     * <p>
     * Use {@link #isAlwaysDown()} to find out if the highest point is still below the
     * twilight angle.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    public Date getNoon() {
        return new Date(noon.getTime());
    }

    /**
     * The time when the sun reaches its lowest point within the next 24 hours.
     * <p>
     * Use {@link #isAlwaysUp()} to find out if the lowest point is still above the
     * twilight angle.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    public Date getNadir() {
        return new Date(nadir.getTime());
    }

    /**
     * {@code true} if the sun never rises/sets, but is always above the twilight angle
     * within the next 24 hours.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    public boolean isAlwaysUp() {
        return alwaysUp;
    }

    /**
     * {@code true} if the sun never rises/sets, but is always below the twilight angle
     * within the next 24 hours.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    public boolean isAlwaysDown() {
        return alwaysDown;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunTimes[rise=").append(rise);
        sb.append(", set=").append(set);
        sb.append(", noon=").append(noon);
        sb.append(", nadir=").append(nadir);
        sb.append(", alwaysUp=").append(alwaysUp);
        sb.append(", alwaysDown=").append(alwaysDown);
        sb.append(']');
        return sb.toString();
    }

}
