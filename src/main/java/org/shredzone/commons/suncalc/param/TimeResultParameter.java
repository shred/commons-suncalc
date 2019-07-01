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
package org.shredzone.commons.suncalc.param;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Result time based parameters.
 * <p>
 * Use them to give information about the desired quality of the result.
 *
 * @param <T>
 *            Type of the final builder
 * @since 2.3
 */
@ParametersAreNonnullByDefault
public interface TimeResultParameter<T> {

    /**
     * Time unit to truncate the result to.
     *
     * @param unit
     *            {@link Unit} to use. By default, {@link Unit#MINUTES} is used.
     * @return itself
     */
    T truncatedTo(Unit unit);

    /**
     * Available chrono units.
     *
     * @since 2.3
     */
    enum Unit {

        /**
         * Round to the nearest full second. Note that due to the simplified formulas used
         * in suncalc, the result is never accurate to the second.
         */
        SECONDS,

        /**
         * Round to the nearest full minute. This is the default.
         */
        MINUTES,

        /**
         * Round to the nearest full hour.
         */
        HOURS,

        /**
         * Round down to the full day. Note that, unlike the other {@link Unit}, the
         * result is always rounded down to full days.
         */
        DAYS

    }

}
