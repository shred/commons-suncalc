# Migration Guide

This document will help you migrate your code to the latest _suncalc_ version.

## Version 2.3

* `MoonPhase` calculation was added. If you have previously used other ways to compute e.g. the date of new moon, you may want to migrate to the new class.

## Version 2.2

* Classes and methods now have [JSR 305](https://jcp.org/en/jsr/detail?id=305) annotations for null references, thread safety and immutability. If you're using tools like Spotbugs, or null-safe languages like Kotlin, you may get errors now if you dereference a nullable result without checking.

## Version 2.1

* `SunTimes`'s `getNoon()` and `getNadir()` now always give a result, even if the sun stays below or above the twilight angle, respectively. To emulate the old behavior, use `isAlwaysUp()` and `isAlwaysDown()` (e.g. `Date noon = !sun.isAlwaysDown() ? sun.getNoon() : null`).
* At `SunTimes` and `MoonTimes`, the methods `isAlwaysUp()`, `isAlwaysDown()`, `getNoon()` and `getNadir()` ignore the `fullCycle` option now, and always consider the next 24 hours only.

## Version 2.0

> **NOTE:** Version 2.0 is a major rewrite. It uses different and (hopefully) more accurate formulae than the previous version. If you rely on reproducable results (e.g. in unit tests), be careful when upgrading. The results may differ up to several minutes between both versions.

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

The advantage is that there are many more setter methods for your convenience, and there are also sensible default values. See the [JavaDocs](./apidocs/index.html) and the [Usage](usage.html) chapter.

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
