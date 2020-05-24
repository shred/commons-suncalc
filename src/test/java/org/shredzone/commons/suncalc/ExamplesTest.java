/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2020 Richard "Shred" Körber
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

/**
 * These are some examples that are meant to be executed manually.
 *
 * @see <a href="https://shredzone.org/maven/commons-suncalc/examples.html">Example
 * chapter of the Documentation</a>
 */
@Ignore // No real unit tests, but meant to be run manually
public class ExamplesTest {

    @Test
    public void testTimezone() {
        // Our example takes place in Paris, so set the timezone accordingly.
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));

        SunTimes paris = SunTimes.compute()
                .on(2020, 5, 1)             // May 1st, 2020, starting midnight
                .latitude(48, 51, 24.0)     // Latitude of Paris: 48°51'24" N
                .longitude(2, 21, 6.0)      // Longitude:          2°21'06" E
                .execute();
        System.out.println("Sunrise in Paris: " + paris.getRise());
        System.out.println("Sunset in Paris:  " + paris.getSet());

        SunTimes newYork = SunTimes.compute()
                .on(2020, 5, 1)             // May 1st, 2020, starting midnight
                .at(40.712778, -74.005833)  // Coordinates of New York
                .execute();
        System.out.println("Sunrise in New York: " + newYork.getRise());
        System.out.println("Sunset in New York:  " + newYork.getSet());

        SunTimes newYorkTz = SunTimes.compute()
                .on(2020, 5, 1)             // May 1st, 2020, starting midnight
                .timezone("America/New_York")   // ...New York timezone
                .at(40.712778, -74.005833)  // Coordinates of New York
                .execute();
        System.out.println("Sunrise in New York: " + newYorkTz.getRise());
        System.out.println("Sunset in New York:  " + newYorkTz.getSet());
    }

    @Test
    public void testTimeWindow() {
        final double[] ALERT_CANADA = new double[] { 82.5, -62.316667 };
        final ZoneId ALERT_TZ = ZoneId.of("Canada/Eastern");

        SunTimes march = SunTimes.compute()
                .on(2020, 3, 15)            // March 15th, 2020, starting midnight
                .at(ALERT_CANADA)           // Coordinates are stored in an array
                .timezone(ALERT_TZ)
                .execute();
        System.out.println("Sunrise: " + march.getRise());
        System.out.println("Sunset:  " + march.getSet());

        SunTimes june = SunTimes.compute()
                .on(2020, 6, 15)            // June 15th, 2020, starting midnight
                .at(ALERT_CANADA)
                .timezone(ALERT_TZ)
                .execute();
        System.out.println("Sunrise: " + june.getRise());
        System.out.println("Sunset:  " + june.getSet());

        System.out.println("Sun is up all day:   " + june.isAlwaysUp());
        System.out.println("Sun is down all day: " + june.isAlwaysDown());

        SunTimes juneFullCycle = SunTimes.compute()
                .on(2020, 6, 15)            // June 15th, 2020, starting midnight
                .at(ALERT_CANADA)
                .timezone(ALERT_TZ)
                .fullCycle()                // No 24h limit, we want to get the full cycle
                .execute();
        System.out.println("Sunset:  " + juneFullCycle.getSet());
        System.out.println("Sunrise: " + juneFullCycle.getRise());
    }

    @Test
    public void testParameterRecycling() {
        final double[] COLOGNE = new double[] { 50.938056, 6.956944 };

        MoonTimes.Parameters parameters = MoonTimes.compute()
                .at(COLOGNE)
                .midnight();

        MoonTimes today = parameters.execute();
        System.out.println("Today, the moon rises in Cologne at " + today.getRise());

        parameters.tomorrow();
        MoonTimes tomorrow = parameters.execute();
        System.out.println("Tomorrow, the moon will rise in Cologne at " + tomorrow.getRise());
        System.out.println("But today, the moon still rises at " + today.getRise());
    }

    @Test
    public void testParameterRecyclingLoop() {
        MoonIllumination.Parameters parameters = MoonIllumination.compute()
                .on(2020, 1, 1);

        for (int i = 1; i <= 31; i++) {
            long percent = Math.round(parameters.execute().getFraction() * 100.0);
            System.out.println("On January " + i + " the moon was " + percent + "% lit.");
            parameters.plusDays(1);
        }
    }

    @Test
    public void testGoldenHour() {
        SunTimes.Parameters base = SunTimes.compute()
                .at(1.283333, 103.833333)            // Singapore
                .twilight(SunTimes.Twilight.VISUAL)  // Visual sunrise, this is the default
                .on(2020, 6, 1)
                .timezone("Asia/Singapore");

        for (int i = 0; i < 4; i++) {
            SunTimes visible = base
                    .copy()                          // Use a copy of base
                    .plusDays(i * 7)
                    .execute();
            SunTimes golden = base
                    .copy()                          // Use a copy of base
                    .twilight(SunTimes.Twilight.GOLDEN_HOUR)    // Golden Hour
                    .plusDays(i * 7)
                    .execute();

            System.out.println("Morning golden hour starts at " + visible.getRise());
            System.out.println("Morning golden hour ends at   " + golden.getRise());
            System.out.println("Evening golden hour starts at " + golden.getSet());
            System.out.println("Evening golden hour ends at   " + visible.getSet());
        }
    }

    @Test
    public void testMoonPhase() {
        LocalDate date = LocalDate.of(2023, 1, 1);

        MoonPhase.Parameters parameters = MoonPhase.compute()
                .phase(MoonPhase.Phase.FULL_MOON);

        while (true) {
            LocalDate nextFullMoon = parameters
                    .on(date)
                    .execute()
                    .getTime()
                    .toLocalDate();
            if (nextFullMoon.getYear() == 2024) {
                break;      // we've reached the next year
            }

            System.out.println(nextFullMoon);

            date = nextFullMoon.plusDays(1);
        }
    }

    @Test
    public void testPositions() {
        SunPosition.Parameters sunParam = SunPosition.compute()
                .at(35.689722, 139.692222)      // Tokyo
                .timezone("Asia/Tokyo")         // local time
                .on(2018, 11, 13, 10, 3, 24);   // 2018-11-13 10:03:24

        MoonPosition.Parameters moonParam = MoonPosition.compute()
                .sameLocationAs(sunParam)
                .sameTimeAs(sunParam);

        SunPosition sun = sunParam.execute();
        System.out.println(String.format(
                "The sun can be seen %.1f° clockwise from the North and "
                + "%.1f° above the horizon.\nIt is about %.0f km away right now.",
                sun.getAzimuth(),
                sun.getAltitude(),
                sun.getDistance()
        ));

        MoonPosition moon = moonParam.execute();
        System.out.println(String.format(
                "The moon can be seen %.1f° clockwise from the North and "
                + "%.1f° above the horizon.\nIt is about %.0f km away right now.",
                moon.getAzimuth(),
                moon.getAltitude(),
                moon.getDistance()
        ));
    }

}