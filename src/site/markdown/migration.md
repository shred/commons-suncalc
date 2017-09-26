# Migration Guide

This document will help you migrate your code to the latest _suncalc_ version.

## Version 2.0

> **NOTE:** Version 2.0 is a major rewrite. It uses different and (hopefully) more accurate formulae than the previous version. If you rely on reproducable results (e.g. in unit tests), be careful before upgrading. The results may differ up to several minutes between both versions.

* `SunPosition`, `MoonPosition`, and `MoonIllumination` now return all angles in **degrees** instead of radians, as it is more commonly used.
* `SunPosition`'s and `MoonPosition`'s `getAzimuth()` are now **north-based** (previously they were south-based) and always return positive values.
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
      .twilight(SunTimes.Twilight.VISUAL) // this is the default
      .execute();
Date sunriseTime = st.getRise();
Date sunsetTime = st.getSet();
```

Noon and nadir times are available from separate getters.
