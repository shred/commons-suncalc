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
package org.shredzone.commons.suncalc.param;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Time based parameters.
 * <p>
 * Use them to give information about the desired time. If ommitted, the current time and
 * the system's time zone is used.
 *
 * @param <T>
 *            Type of the final builder
 */

public interface TimeParameter<T> {

    /**
     * Sets midnight of the year, month and date. Uses the system's time zone unless a
     * different time zone is given.
     *
     * @param year
     *            Year
     * @param month
     *            Month (1 = January, 2 = February, ...)
     * @param date
     *            Day of month
     * @return itself
     */
    T on(int year, int month, int date);

    /**
     * Sets date and time. Note that also seconds can be passed in for convenience, but
     * the results are not that accurate. Uses the system's time zone unless a different
     * time zone is given.
     *
     * @param year
     *            Year
     * @param month
     *            Month (1 = January, 2 = February, ...)
     * @param date
     *            Day of month
     * @param hour
     *            Hour of day
     * @param minute
     *            Minute
     * @param second
     *            Second
     * @return itself
     */
    T on(int year, int month, int date, int hour, int minute, int second);

    /**
     * Uses the given {@link Calendar} instance.
     *
     * @param cal
     *            {@link Calendar}. A copy of the date, time, and time zone is used. The
     *            {@link Calendar} instance may be reused after that.
     * @return itself
     */
    T on(Calendar cal);

    /**
     * Uses the given {@link Date} instance. Uses the system's time zone unless a
     * different time zone is given.
     *
     * @param date
     *            {@link Date}. A copy of the date is used. The {@link Date} instance may
     *            be reused after that.
     * @return itself
     */
    T on(Date date);

    /**
     * Adds a number of days to the current date.
     *
     * @param days
     *            Number of days to add
     * @return itself
     * @since 2.2
     */
    T plusDays(int days);

    /**
     * Sets today, midnight.
     * <p>
     * It is the same as <code>now().midnight()</code>.
     *
     * @return itself
     */
    T today();

    /**
     * Sets tomorrow, midnight.
     * <p>
     * It is the same as <code>now().midnight().plusDays(1)</code>.
     *
     * @return itself
     * @since 2.2
     */
    T tomorrow();

    /**
     * Sets the current date and time. This is the default.
     *
     * @return itself
     */
    T now();

    /**
     * Sets the time to the start of the current date ("last midnight").
     *
     * @return itself
     */
    T midnight();

    /**
     * Sets the given {@link TimeZone}.
     *
     * @param tz
     *            {@link TimeZone} to be used.
     * @return itself
     */
    T timezone(TimeZone tz);

    /**
     * Sets the given {@link TimeZone}. This is a convenience method that just invokes
     * {@link TimeZone#getTimeZone(String)}.
     *
     * @param id
     *            ID of the time zone. "GMT" is used if the time zone ID was not
     *            understood.
     * @return itself
     * @see TimeZone#getTimeZone(String)
     */
    T timezone(String id);

    /**
     * Sets the system's {@link TimeZone}. This is the default.
     *
     * @return itself
     */
    T localTime();

    /**
     * Sets the time zone to UTC.
     *
     * @return itself
     */
    T utc();

}
