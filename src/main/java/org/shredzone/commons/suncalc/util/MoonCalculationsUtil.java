package org.shredzone.commons.suncalc.util;

import static java.lang.Math.*;

import java.util.Date;
import java.util.TimeZone;

import static org.shredzone.commons.suncalc.util.Kopernikus.*;
import static org.shredzone.commons.suncalc.util.Kopernikus.E;
import static org.shredzone.commons.suncalc.util.TimeUtil.dateToMJD;
import static org.shredzone.commons.suncalc.util.TimeUtil.hourToDays;

/**
 * Utility class for calculating the moon's altitude
 *
 * Moon calculations are based on:
 * Astronomy on the Personal Computer 4th Edition
 *
 * Oliver MontenBruck
 * Thomas Pfleger
 *
 * ISBN - 978-3-662-11187-1
 */
public class MoonCalculationsUtil {
    private static final double ARC = 206264.8062; //radian to arcsecond
    private static final double PI_TIMES_2 = PI * 2.0;
    private static final double COS_E = cos(E);
    private static final double SIN_E = sin(E);

    public static double preciseAltitude(Date date, TimeZone tz, double hour, double lat, double lng) {
        final double phi = RAD * lat;
        final double MJD = dateToMJD(date, tz.getOffset(date.getTime())) + hourToDays(hour);
        final Coordinates mc = moonCoords(MJD);
        final double h = 15.0 * (localMeanSiderealTime(MJD, lng) - mc.ra) * RAD; //1 hour = 15 degrees

        return sin(phi) * sin(RAD * mc.dec) + cos(phi) * cos(RAD * mc.dec) * cos(h);
    }

    //Calculations based on Astronomy on the Personal Computer, p. 38
    private static Coordinates moonCoords(double MJD) {
        final double T = (MJD - 51544.5) / 36525.0;
        final double L0 = FRAC(0.606433 + 1336.855225 * T);
        final double L = PI_TIMES_2 * FRAC(0.374897 + 1325.552410 * T);
        final double LS = PI_TIMES_2 * FRAC(0.993133 + 99.997361 * T);
        final double D = PI_TIMES_2 * FRAC(0.827361 + 1236.853086 * T);
        final double F = PI_TIMES_2 * FRAC(0.259086 + 1342.227825 * T);

        final double DL = 22640 * sin(L) - 4586 * sin(L - 2 * D) + 2370
                * sin(2 * D) + 769 * sin(2 * L) - 668 * sin(LS)
                - 412 * sin(2 * F) - 212 * sin(2 * L - 2 * D) - 206
                * sin(L + LS - 2 * D) + 192 * sin(L + 2 * D) - 165
                * sin(LS - 2 * D) - 125 * sin(D) - 110
                * sin(L + LS) + 148 * sin(L - LS) - 55
                * sin(2 * F - 2 * D);

        final double S = F + (DL + 412 * sin(2 * F) + 541 * sin(LS)) / ARC;
        final double H = F - 2 * D;
        final double N = -526 * sin(H) + 44 * sin(L + H) - 31
                * sin(-L + H) - 23 * sin(LS + H) + 11
                * sin(-LS + H) - 25 * sin(-2 * L + F) + 21
                * sin(-L + F);
        // angle in radians
        final double L_MOON = PI_TIMES_2 * FRAC(L0 + DL / 1296.0E3);
        final double B_MOON = (18520.0 * sin(S) + N) / ARC;

        return equatorialToPolar(L_MOON, B_MOON);
    }

    private static Coordinates equatorialToPolar(double L, double B) {
        final double cosB = cos(B);
        final double sinB = sin(B);
        final double cosL = cos(L);
        final double sinL = sin(L);

        //equatorial to cartesian coordinates
        final double X = cosB * cosL;
        final double Y = COS_E * cosB * sinL - SIN_E * sinB;
        final double Z = SIN_E * cosB * sinL + COS_E * sinB;

        //cartesian to polar coordinates
        final double RHO = sqrt(1.0 - Z * Z);
        final double dec = (360.0 / PI_TIMES_2) * atan(Z / RHO);
        double ra = (48.0 / PI_TIMES_2) * atan(Y / (X + RHO));

        if (ra < 0.0) ra += 24.0;

        return new Coordinates(dec, ra);
    }

    private static double localMeanSiderealTime(double MJD, double longitude) {
        final double MJD0 = floor(MJD);
        final double UT = (MJD - MJD0) * 24.0;
        final double T = (MJD0 - 51544.5) / 36525.0;
        final double GMST = 6.697374558 + 1.0027379093 * UT
                + (8640184.812866 + (0.093104 - 6.2E-6 * T) * T) * T / 3600.0;
        final double localMeanSiderealTime = 24.0 * FRAC((GMST + longitude / 15.0) / 24.0);
        return (localMeanSiderealTime);
    }

    private static double FRAC(double value) {
        double result = value - TRUNC(value);
        if (result < 0.0) result += 1.0;
        return (result);
    }

    private static double TRUNC(double value) {
        double result = floor(abs(value));
        result = result * signum(result);
        return (result);
    }
}
