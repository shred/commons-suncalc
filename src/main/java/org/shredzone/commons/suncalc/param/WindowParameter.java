/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2024 Richard "Shred" Körber
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

import java.time.Duration;

/**
 * Time window based parameters.
 * <p>
 * Use them to give information about the desired time window. If ommitted, a forward
 * window of 365 days is assumed.
 *
 * @since 3.11
 * @param <T>
 *            Type of the final builder
 */
public interface WindowParameter<T> {

    /**
     * Limits the calculation window to the given {@link Duration}.
     *
     * @param duration
     *         Duration of the calculation window. A negative duration sets a reverse time
     *         window, giving result times in the past.
     * @return itself
     */
    T limit(Duration duration);

    /**
     * Limits the time window to the next 24 hours.
     *
     * @return itself
     */
    default T oneDay() {
        return limit(Duration.ofDays(1L));
    }

    /**
     * Computes until all times are found.
     * <p>
     * This is the default.
     *
     * @return itself
     */
    default T fullCycle() {
        return limit(Duration.ofDays(365L));
    }

    /**
     * Sets a reverse calculation window. It will end at the given date.
     *
     * @return itself
     * @since 3.11
     */
    T reverse();

    /**
     * Sets a forward calculation window. It will start at the given date.
     * <p>
     * This is the default.
     *
     * @return itself
     * @since 3.11
     */
    T forward();

    /**
     * Uses the same window as given in the {@link WindowParameter}.
     * <p>
     * Changes to the source parameter will not affect this parameter, though.
     *
     * @param t  {@link WindowParameter} to be used.
     * @return itself
     * @since 3.11
     */
    T sameWindowAs(WindowParameter<?> w);

}
