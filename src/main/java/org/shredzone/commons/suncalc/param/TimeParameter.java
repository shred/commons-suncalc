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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
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
@SuppressWarnings("unchecked")
public interface TimeParameter<T> {

    /**
     * Sets date and time. Note that also seconds can be passed in for convenience, but
     * the results are not that accurate.
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
     * Sets midnight of the year, month and date.
     *
     * @param year
     *            Year
     * @param month
     *            Month (1 = January, 2 = February, ...)
     * @param date
     *            Day of month
     * @return itself
     */
    default T on(int year, int month, int date) {
        return on(year, month, date, 0, 0, 0);
    }

    /**
     * Uses the given {@link ZonedDateTime} instance.
     *
     * @param dateTime
     *            {@link ZonedDateTime} to be used.
     * @return itself
     */
    T on(ZonedDateTime dateTime);

    /**
     * Uses the given {@link LocalDateTime} instance.
     *
     * @param dateTime
     *         {@link LocalDateTime} to be used.
     * @return itself
     */
    T on(LocalDateTime dateTime);

    /**
     * Uses the given {@link LocalDate} instance, and assumes midnight.
     *
     * @param date
     *         {@link LocalDate} to be used.
     * @return itself
     */
    T on(LocalDate date);

    /**
     * Uses the given {@link Instant} instance.
     *
     * @param instant
     *            {@link Instant} to be used.
     * @return itself
     */
    T on(Instant instant);

    /**
     * Uses the given {@link Date} instance.
     *
     * @param date
     *         {@link Date} to be used.
     * @return itself
     */
    default T on(Date date) {
        Objects.requireNonNull(date, "date");
        return on(date.toInstant());
    }

    /**
     * Uses the given {@link Calendar} instance.
     *
     * @param cal
     *         {@link Calendar} to be used
     * @return itself
     */
    default T on(Calendar cal) {
        Objects.requireNonNull(cal, "cal");
        return on(ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId()));
    }

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
     * Adds a number of days to the current date.
     *
     * @param days
     *            Number of days to add
     * @return itself
     */
    T plusDays(int days);

    /**
     * Sets today, midnight.
     * <p>
     * It is the same as <code>now().midnight()</code>.
     *
     * @return itself
     */
    default T today() {
        now();
        midnight();
        return (T) this;
    }

    /**
     * Sets tomorrow, midnight.
     * <p>
     * It is the same as <code>now().midnight().plusDays(1)</code>.
     *
     * @return itself
     */
    default T tomorrow() {
        today();
        plusDays(1);
        return (T) this;
    }

    /**
     * Sets the given {@link ZoneId}. The local time is retained, so the parameter order
     * is not important.
     *
     * @param tz
     *            {@link ZoneId} to be used.
     * @return itself
     */
    T timezone(ZoneId tz);

    /**
     * Sets the given timezone. This is a convenience method that just invokes
     * {@link ZoneId#of(String)}.
     *
     * @param id
     *            ID of the time zone.
     * @return itself
     * @see ZoneId#of(String)
     */
    default T timezone(String id) {
        return timezone(ZoneId.of(id));
    }

    /**
     * Sets the system's timezone. This is the default.
     *
     * @return itself
     */
    default T localTime() {
        return timezone(ZoneId.systemDefault());
    }

    /**
     * Sets the time zone to UTC.
     *
     * @return itself
     */
    default T utc() {
        return timezone("UTC");
    }

    /**
     * Sets the {@link TimeZone}.
     *
     * @param tz {@link TimeZone} to be used
     * @return itself
     */
    default T timezone(TimeZone tz) {
        Objects.requireNonNull(tz, "tz");
        return timezone(tz.toZoneId());
    }

    /**
     * Uses the same time as given in the {@link TimeParameter}.
     * <p>
     * Changes to the source parameter will not affect this parameter, though.
     *
     * @param t  {@link TimeParameter} to be used.
     * @return itself
     */
    T sameTimeAs(TimeParameter<?> t);

}
