/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2016 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Bases on SunCalc by Vladimir Agafonkin (https://github.com/mourner/suncalc)
 */
package org.shredzone.commons.suncalc;

import static java.lang.Math.*;
import static org.shredzone.commons.suncalc.util.Kopernikus.*;

import java.util.Date;

import org.shredzone.commons.suncalc.util.Kopernikus.Coordinates;

/**
 * Calculates the illumination of the moon.
 *
 * @see <a href="https://github.com/mourner/suncalc">SunCalc</a>
 * @see <a href="http://idlastro.gsfc.nasa.gov/ftp/pro/astro/mphase.pro">Formulas the
 *      calculations base on</a>
 * @see <a href="http://aa.quae.nl/en/reken/hemelpositie.html">Formulas used for moon
 *      calculations</a>
 * @see "Astronomical Algorithms, 2nd edition by Jean Meeus (Willmann-Bell,
 *      Richmond) 1998, Chapter 48"
 * @author Richard "Shred" Körber
 */
public class MoonIllumination {

    /**
     * Calculates the {@link MoonIllumination} of the given {@link Date}.
     */
    public static MoonIllumination of(Date date) {
        double d = toDays(date);
        Coordinates s = sunCoords(d);
        Coordinates m = moonCoords(d);

        double sdist = 149598000.0; // distance from Earth to Sun in km

        double phi = acos(sin(s.dec) * sin(m.dec) + cos(s.dec) * cos(m.dec) * cos(s.ra - m.ra));
        double inc = atan2(sdist * sin(phi), m.dist - sdist * cos(phi));
        double angle = atan2(cos(s.dec) * sin(s.ra - m.ra), sin(s.dec) * cos(m.dec) -
                cos(s.dec) * sin(m.dec) * cos(s.ra - m.ra));

        return new MoonIllumination(
                        (1 + cos(inc)) / 2,
                        0.5 + 0.5 * inc * signum(angle) / PI,
                        angle);
    }

    private final double fraction, phase, angle;

    private MoonIllumination(double fraction, double phase, double angle) {
        this.fraction = fraction;
        this.phase = phase;
        this.angle = angle;
    }

    /**
     * Illuminated fraction. {@code 0.0} indicates new moon, {@code 1.0} indicates full
     * moon.
     */
    public double getFraction() {
        return fraction;
    }

    /**
     * Moon phase. Starts at {@code 0.0} (new moon, waxing), passes {@code 0.5} (full
     * moon) and moves toward {@code 1.0} (waning, new moon).
     */
    public double getPhase() {
        return phase;
    }

    /**
     * Midpoint angle in radians of the illuminated limb of the moon reckoned eastward
     * from the north point of the disk; the moon is waxing if the angle is negative, and
     * waning if positive.
     * <p>
     * By subtracting {@link MoonPosition#getParallacticAngle()} from {@link #getAngle()},
     * one can get the zenith angle of the moons bright limb (anticlockwise). The zenith
     * angle can be used do draw the moon shape from the observers perspective (e.g. moon
     * lying on its back).
     */
    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonIllumination[fraction=").append(fraction);
        sb.append(", phase=").append(phase);
        sb.append(", angle=").append(angle);
        sb.append(']');
        return sb.toString();
    }

}
