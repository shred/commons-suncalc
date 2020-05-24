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
import org.shredzone.commons.suncalc.util.Moon;
import org.shredzone.commons.suncalc.util.QuadraticInterpolation;
import org.shredzone.commons.suncalc.util.Vector;

/**
 * Calculates the times of the moon.
 */
public final class MoonTimes {

    private final @Nullable ZonedDateTime rise;
    private final @Nullable ZonedDateTime set;
    private final boolean alwaysUp;
    private final boolean alwaysDown;

    private MoonTimes(@Nullable ZonedDateTime rise, @Nullable ZonedDateTime set,
              boolean alwaysUp, boolean alwaysDown) {
        this.rise = rise;
        this.set = set;
        this.alwaysUp = alwaysUp;
        this.alwaysDown = alwaysDown;
    }

    /**
     * Starts the computation of {@link MoonTimes}.
     *
     * @return {@link Parameters} to set.
     */
    public static Parameters compute() {
        return new MoonTimesBuilder();
    }

    /**
     * Collects all parameters for {@link MoonTimes}.
     */
    public static interface Parameters extends
            GenericParameter<Parameters>,
            LocationParameter<Parameters>,
            TimeParameter<Parameters>,
            TimeResultParameter<Parameters>,
            Builder<MoonTimes> {

        /**
         * Checks only the next 24 hours. Rise or set times can be {@code null} if the
         * moon never reaches the point during one day.
         * <p>
         * This is the default.
         *
         * @return itself
         */
        Parameters oneDay();

        /**
         * Computes until rise and set times are found, even if the moon needs more than a
         * day for it. This can increase computation time.
         *
         * @return itself
         */
        Parameters fullCycle();
    }

    /**
     * Builder for {@link MoonTimes}. Performs the computations based on the parameters,
     * and creates a {@link MoonTimes} object that holds the result.
     */
    private static class MoonTimesBuilder extends BaseBuilder<Parameters> implements Parameters {
        private boolean fullCycle = false;
        private double refraction = apparentRefraction(0.0);

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
        public MoonTimes execute() {
            JulianDate jd = getJulianDate();

            Double rise = null;
            Double set = null;
            boolean alwaysUp = false;
            boolean alwaysDown = false;

            int hour = 0;
            int maxHours = fullCycle ? 365 * 24 : 24;

            double y_minus = correctedMoonHeight(jd.atHour(hour - 1.0));
            double y_0 = correctedMoonHeight(jd.atHour(hour));
            double y_plus = correctedMoonHeight(jd.atHour(hour + 1.0));

            while (hour <= maxHours) {
                QuadraticInterpolation qi = new QuadraticInterpolation(y_minus, y_0, y_plus);
                double ye = qi.getYe();

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

                if (hour == 23 && rise == null && set == null) {
                    alwaysUp = ye >= 0.0;
                    alwaysDown = ye < 0.0;
                }

                if (rise != null && set != null) {
                    break;
                }

                hour++;
                y_minus = y_0;
                y_0 = y_plus;
                y_plus = correctedMoonHeight(jd.atHour(hour + 1.0));
            }

            if (!fullCycle) {
                if (rise != null && rise >= 24.0) {
                    rise = null;
                }
                if (set != null && set >= 24.0) {
                    set = null;
                }
            }

            return new MoonTimes(
                    rise != null ? jd.atHour(rise).getDateTruncated(getTruncatedTo()) : null,
                    set != null ? jd.atHour(set).getDateTruncated(getTruncatedTo()) : null,
                    alwaysUp,
                    alwaysDown);
        }

        /**
         * Computes the moon height at the given date and position.
         *
         * @param jd {@link JulianDate} to use
         * @return height, in radians
         */
        private double correctedMoonHeight(JulianDate jd) {
            Vector pos = Moon.positionHorizontal(jd, getLatitudeRad(), getLongitudeRad());
            double hc = parallax(getHeight(), pos.getR())
                            - refraction
                            - Moon.angularRadius(pos.getR());
            return pos.getTheta() - hc;
        }
    }

    /**
     * Moonrise time. {@code null} if the moon does not rise that day.
     */
    @Nullable
    public ZonedDateTime getRise() {
        return rise;
    }

    /**
     * Moonset time. {@code null} if the moon does not set that day.
     */
    @Nullable
    public ZonedDateTime getSet() {
        return set;
    }

    /**
     * {@code true} if the moon never rises/sets, but is always above the horizon within
     * the next 24 hours.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    public boolean isAlwaysUp() {
        return alwaysUp;
    }

    /**
     * {@code true} if the moon never rises/sets, but is always below the horizon within
     * the next 24 hours.
     * <p>
     * Note that {@link Parameters#fullCycle()} does not affect this result.
     */
    public boolean isAlwaysDown() {
        return alwaysDown;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonTimes[rise=").append(rise);
        sb.append(", set=").append(set);
        sb.append(", alwaysUp=").append(alwaysUp);
        sb.append(", alwaysDown=").append(alwaysDown);
        sb.append(']');
        return sb.toString();
    }

}
