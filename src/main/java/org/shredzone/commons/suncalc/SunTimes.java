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
import static org.shredzone.commons.suncalc.util.ExtendedMath.apparentRefraction;
import static org.shredzone.commons.suncalc.util.ExtendedMath.parallax;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.shredzone.commons.suncalc.param.Builder;
import org.shredzone.commons.suncalc.param.GenericParameter;
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
public class SunTimes {

    private final @Nullable ZonedDateTime rise;
    private final @Nullable ZonedDateTime set;
    private final @Nullable ZonedDateTime noon;
    private final @Nullable ZonedDateTime nadir;
    private final boolean alwaysUp;
    private final boolean alwaysDown;

    private SunTimes(@Nullable ZonedDateTime rise, @Nullable ZonedDateTime set,
                     @Nullable ZonedDateTime noon, @Nullable ZonedDateTime nadir,
                     boolean alwaysUp, boolean alwaysDown) {
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
    public interface Parameters extends
            GenericParameter<Parameters>,
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
        private final @Nullable Double position;

        Twilight(double angle) {
            this(angle, null);
        }

        Twilight(double angle, @Nullable Double position) {
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
        @Nullable
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
        private @Nullable Double position = Twilight.VISUAL.getAngularPosition();
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
            boolean alwaysUp = false;
            boolean alwaysDown = false;
            double ye;
            double noonYe = 0.0;
            double nadirYe = 0.0;

            int hour = 0;
            int maxHours = fullCycle ? 365 * 24 : 24;

            double y_minus = correctedSunHeight(jd.atHour(hour - 1.0));
            double y_0 = correctedSunHeight(jd.atHour(hour));
            double y_plus = correctedSunHeight(jd.atHour(hour + 1.0));

            if (y_0 > 0.0) {
                alwaysUp = true;
            } else {
                alwaysDown = true;
            }

            while (hour <= maxHours) {
                QuadraticInterpolation qi = new QuadraticInterpolation(y_minus, y_0, y_plus);
                ye = qi.getYe();

                if (qi.getNumberOfRoots() == 1) {
                    double rt = qi.getRoot1() + hour;
                    if (y_minus < 0.0) {
                        if (rise == null && rt >= 0.0) {
                            rise = rt;
                        }
                    } else {
                        if (set == null && rt >= 0.0) {
                            set = rt;
                        }
                    }
                } else if (qi.getNumberOfRoots() == 2) {
                    if (rise == null) {
                        double rt = hour + (ye < 0.0 ? qi.getRoot2() : qi.getRoot1());
                        if (rt >= 0.0) {
                            rise = rt;
                        }
                    }
                    if (set == null) {
                        double rt = hour + (ye < 0.0 ? qi.getRoot1() : qi.getRoot2());
                        if (rt >= 0.0) {
                            set = rt;
                        }
                    }
                }

                if (hour <= 24) {
                    double xeAbs = Math.abs(qi.getXe());
                    if (xeAbs <= 1.0) {
                        double xeHour = qi.getXe() + hour;
                        if (xeHour >= 0.0 && xeHour < 24.0) {
                            if (qi.isMaximum()) {
                                if (noon == null || ye > noonYe) {
                                    noon = xeHour;
                                    noonYe = ye;
                                }
                            } else {
                                if (nadir == null || ye < nadirYe) {
                                    nadir = xeHour;
                                    nadirYe = ye;
                                }
                            }
                        }
                    }
                }

                if (hour == 23) {
                    if (rise != null) {
                        alwaysDown = false;
                    }
                    if (set != null) {
                        alwaysUp = false;
                    }
                }

                if (hour >= 24 && rise != null && set != null) {
                    break;
                }

                hour++;
                y_minus = y_0;
                y_0 = y_plus;
                y_plus = correctedSunHeight(jd.atHour(hour + 1.0));
            }

            if (!fullCycle) {
                if (rise != null && rise >= 24.0) {
                    rise = null;
                }
                if (set != null && set >= 24.0) {
                    set = null;
                }
            }

            return new SunTimes(
                    rise != null ? jd.atHour(rise).getDateTruncated(getTruncatedTo()) : null,
                    set != null ? jd.atHour(set).getDateTruncated(getTruncatedTo()) : null,
                    noon != null ? jd.atHour(noon).getDateTruncated(getTruncatedTo()) : null,
                    nadir != null ? jd.atHour(nadir).getDateTruncated(getTruncatedTo()) : null,
                    alwaysUp,
                    alwaysDown
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
    @Nullable
    public ZonedDateTime getRise() {
        return rise;
    }

    /**
     * Sunset time. {@code null} if the sun does not set that day.
     * <p>
     * Always returns a sunset time if {@link Parameters#fullCycle()} was set.
     */
    @Nullable
    public ZonedDateTime getSet() {
        return set;
    }

    /**
     * The time when the sun reaches its highest point within the next 24 hours.
     * <p>
     * Use {@link #isAlwaysDown()} to find out if the highest point is still below the
     * twilight angle.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    @Nullable
    public ZonedDateTime getNoon() {
        return noon;
    }

    /**
     * The time when the sun reaches its lowest point within the next 24 hours.
     * <p>
     * Use {@link #isAlwaysUp()} to find out if the lowest point is still above the
     * twilight angle.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    @Nullable
    public ZonedDateTime getNadir() {
        return nadir;
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
