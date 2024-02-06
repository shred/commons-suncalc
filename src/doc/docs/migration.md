# Migration Guide

This document will help you migrate your code to the latest _suncalc_ version.

## Version 3.9
* `MoonIllumination` now permits to set an optional geolocation. If set, the results are topocentric. If not set, the result is geocentric, which was the default behavior. For this reason, no migration is necessary here.
* In previous versions, the geolocation was assumed to be 0°N 0°E ([Null Island](https://en.wikipedia.org/wiki/Null_Island)) if not set. This was not very useful in practice. Starting now, `execute()` will throw an exception if latitude or longitude is not set. To emulate the old behavior, use `at(0.0, 0.0)` as parameter. However, if you get an exception now, it rather means that you have used suncalc wrong.
* For the spectator's altitude above sea level, `elevation()` and `elevationFt()` is now used instead of `height()` and `heightFt()`. The old methods are marked as deprecated, but are still functional. Please change to the new methods, they are drop-in replacements.

## Version 3.6
* Due to a very old bug, `MoonPosition.getParallacticAngle()` gave completely wrong results. If you used that method in your code or tests, prepare to get totally different values now.

## Version 3.3
* Due to a very old bug, setting the `height()` had almost no impact on the result, which is obviously wrong (on sunset, a plane in the sky is still illuminated while the sun has just gone at the viewer's position). This bug has been fixed. If you are using `height()`, you will get correct results now. They may deviate by several minutes from the results of earlier versions.

## Version 3.1
* As it was an eternal source of confusion, `SunTime` and `MoonTime` now default to `fullCycle()`, so they always compute until all times have been found. To revert to the previous behavior, use the `oneDay()` parameter. There is also a new `limit()` parameter which limits the window to any end date.
* `isAlwaysUp()`, `isAlwaysDown()`, `getNoon()` and `getNadir()` now also use the given time window instead of the next 24 hours. Use `oneDay()` to revert to the previous behavior.

## Version 3.0
* _suncalc_ now requires at least Java 8 or Android 8.0 "Oreo" (API level 26). You can still use _suncalc_ v2 for Java 7 or Android API level 19 compatibility. The v2 branch is not discontinued and will still receive bugfixes.
* The outdated `Date` and `Calendar` classes have been replaced by the Java Date/Time API. All results are now returned as `ZonedDateTime` instances, and now also carry the timezone that was used for calculation. It is now much easier to use the result.
* Result rounding has been removed from this library, as it can easily be done by `ZoneDateTime.truncateTo()`. Note that even though the results now contain milliseconds, the precision is still only up to about a minute.
* The JSR305 null-safe annotations have been replaced by SpotBugs annotations. This _should_ have no impact on your code, as the method signatures themselves are unchanged. However, the compiler could now complain about some `null` dereferences that have been undetected before. Reason is that JSR305 uses the `javax.annotations` package, which leads to split packages in a Java 9 modular environment.
