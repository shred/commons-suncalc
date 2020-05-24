# Migration Guide

This document will help you migrate your code to the latest _suncalc_ version.

## Version 3.0
* _suncalc_ now requires at least Java 8 or Android 8.0 "Oreo" (API level 26). You can still use _suncalc_ v2 for Java 7 or Android API level 19 compatibility. The v2 branch is not discontinued and will still receive bugfixes.
* The outdated `Date` and `Calendar` classes have been replaced by the Java Date/Time API. All results are now returned as `ZonedDateTime` instances, and now also carry the timezone that was used for calculation. It is now much easier to use the result.

## Version 2.8

* `MoonIllumination` is now also using more accurate formulas, like all the other classes. Unit tests that are based on the results of this class, may fail and need to be readjusted. This was the last relic of the old version 1. _suncalc_ is now a completely independent project.

## Version 2.7

* Changes to `SunTimes` and `MoonTimes` are now giving more accurate results when the start of the time window is close to noon or nadir. As a welcome side effect, calculation is now almost twice as fast due to optimizations. Unit tests that are based on the results, may fail and need to be readjusted.

## Version 2.3

* All `Date` results are now rounded to the nearest full minute. This was done so _suncalc_ does not pretend a higher precision than it can actually deliver. If your code relies on the seconds (e.g. in unit tests), you can add `truncateTo(TimeResultParameter.Unit.SECONDS)` to the parameter chain to get the same results as from the previous version.
* `MoonPhase` calculation was added. If you have previously used other ways to compute e.g. the date of new moon, you may want to migrate to the new class.

## Version 2.2

* Classes and methods now have [JSR 305](https://jcp.org/en/jsr/detail?id=305) annotations for null references, thread safety and immutability. If you're using tools like Spotbugs, or null-safe languages like Kotlin, you may get errors now if you dereference a nullable result without checking.

## Version 2.1

* `SunTimes`'s `getNoon()` and `getNadir()` now always give a result, even if the sun stays below or above the twilight angle, respectively. To emulate the old behavior, use `isAlwaysUp()` and `isAlwaysDown()` (e.g. `Date noon = !sun.isAlwaysDown() ? sun.getNoon() : null`).
* At `SunTimes` and `MoonTimes`, the methods `isAlwaysUp()`, `isAlwaysDown()`, `getNoon()` and `getNadir()` ignore the `fullCycle` option now, and always consider the next 24 hours only.

## Version 2.0

!!! NOTE
    Version 2.0 is a major rewrite. It uses different and (hopefully) more accurate formulae than the previous version. If you rely on reproducable results (e.g. in unit tests), be careful when upgrading. The results may differ up to several minutes between both versions.

* `SunPosition`, `MoonPosition`, and `MoonIllumination` now return all angles in **degrees** instead of radians, as it is more commonly used.
* `SunPosition`'s and `MoonPosition`'s `getAzimuth()` are now **north-based** (north = 0°) and always return positive values. Previously they were south-based (south = 0°).
* The builder pattern is now more verbose. Instead of just invoking methods like `MoonTimes.of()`, you now invoke `MoonTimes.compute()`, add verbs for setting the time, location, and other parameters, and then invoke `execute()` to get the result. An example:

Previously:

```java
Date date = // date of calculation
double lat, lng = // geolocation
SunPosition position = SunPosition.of(date, lat, lng);
```

Now:

```java
Date date = // date of calculation
double lat, lng = // geolocation
SunPosition position = SunPosition.compute()
      .on(date).utc()
      .at(lat, lng)
      .execute();
```

The advantage is that there are many more setter methods for your convenience, and there are also sensible default values. See the [JavaDocs](./apidocs/index.html) and the [Usage](usage.md) chapter.

* The `SunTimes.getTime()` method isn't available any more. Now you pass the desired twilight angle as a `SunTimes` parameter, and get both the raise and set time after executing the computation.

Previously:

```java
SunTimes st = SunTimes.of(date, lat, lng);
Date sunriseTime = st.getTime(SunTimes.Time.SUNRISE);
Date sunsetTime = st.getTime(SunTimes.Time.SUNSET);
```

Now:

```java
SunTimes st = SunTimes.compute()
      .on(date).at(lat, lng)
      .twilight(SunTimes.Twilight.VISUAL) // default, equals SUNRISE/SUNSET
      .execute();
Date sunriseTime = st.getRise();
Date sunsetTime = st.getSet();
```

Noon and nadir times are available from separate getters.
