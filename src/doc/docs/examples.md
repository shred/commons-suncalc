# Examples

In this chapter, you will find code examples that demonstrate the use and the possibilities of the API. You can also find (and run) these examples in the [ExamplesTest](https://github.com/shred/commons-suncalc/blob/master/src/test/java/org/shredzone/commons/suncalc/ExamplesTest.java) unit test.

I know this is a long chapter. It is because _suncalc_ offers a lot of functionality. I still recommend to read it, or at least skim it, to get an idea of what is possible or best practice.

## Time Zone

All calculations use your own system's local time and time zone, unless you give other parameters. This can give surprising and confusing results.

Let's assume we're living in Paris, and we want to compute our sunrise and sunset time for May 1st, 2020.

```java
SunTimes paris = SunTimes.compute()
        .on(2020, 5, 1)             // May 1st, 2020, starting midnight
        .latitude(48, 51, 24.0)     // Latitude of Paris: 48°51'24" N
        .longitude(2, 21, 6.0)      // Longitude:          2°21'06" E
        .execute();
System.out.println("Sunrise in Paris: " + paris.getRise());
System.out.println("Sunset in Paris:  " + paris.getSet());
```

The result is not very surprising:

```text
Sunrise in Paris: 2020-05-01T06:29:47+02:00[Europe/Paris]
Sunset in Paris:  2020-05-01T21:06:45+02:00[Europe/Paris]
```

Now we want to compute the sunrise and sunset times of New York.

```java
SunTimes newYork = SunTimes.compute()
        .on(2020, 5, 1)             // May 1st, 2020, starting midnight
        .at(40.712778, -74.005833)  // Coordinates of New York
        .execute();
System.out.println("Sunrise in New York: " + newYork.getRise());
System.out.println("Sunset in New York:  " + newYork.getSet());
```

The result is:

```text
Sunrise in New York: 2020-05-01T11:54:05+02:00[Europe/Paris]
Sunset in New York:  2020-05-01T01:51:51+02:00[Europe/Paris]
```

Huh? The sun rises at noon and sets past midnight? The sun also sets before it is rising that day?

The reason is that we're still using the Paris timezone. On May 1st, midnight **Paris time**, the sun is still up in New York. It sets in New York when it's 1:52 in Paris, and raises again when it's 11:54 in Paris.

We can pass a `timezone()` parameter to tell _suncalc_ that we actually want to use a different timezone.

```java
SunTimes newYorkTz = SunTimes.compute()
        .on(2020, 5, 1)             // May 1st, 2020, starting midnight
        .timezone("America/New_York") // ...New York timezone
        .at(40.712778, -74.005833)  // Coordinates of New York
        .execute();
System.out.println("Sunrise in New York: " + newYorkTz.getRise());
System.out.println("Sunset in New York:  " + newYorkTz.getSet());
```

Now, we finally see the actual sunrise and sunset time in New York:

```text
Sunrise in New York: 2020-05-01T05:54:05-04:00[America/New_York]
Sunset in New York:  2020-05-01T19:52:53-04:00[America/New_York]
```

## Time Window

[Alert, Nunavut, Canada](https://en.wikipedia.org/wiki/Alert,_Nunavut) is the northernmost place in the world with a permanent population. Let's find out when the sun rises and sets there on March 15th, 2020:

```java
final double[] ALERT_CANADA = new double[] { 82.5, -62.316667 };
final ZoneId ALERT_TZ = ZoneId.of("Canada/Eastern");

SunTimes march = SunTimes.compute()
        .on(2020, 3, 15)            // March 15th, 2020, starting midnight
        .at(ALERT_CANADA)           // Coordinates are stored in an array
        .timezone(ALERT_TZ)
        .execute();
System.out.println("Sunrise: " + march.getRise());
System.out.println("Sunset:  " + march.getSet());
```

The result is looking fine so far:

```text
Sunrise: 2020-03-15T06:49:03-04:00[Canada/Eastern]
Sunset:  2020-03-15T17:52:53-04:00[Canada/Eastern]
```

What about June 15th?

```java
SunTimes june = SunTimes.compute()
        .on(2020, 6, 15)            // June 15th, 2020, starting midnight
        .at(ALERT_CANADA)
        .timezone(ALERT_TZ)
        .execute();
System.out.println("Sunrise: " + june.getRise());
System.out.println("Sunset:  " + june.getSet());
```

The result:

```text
Sunrise: 2020-09-05T00:24:03-04:00[Canada/Eastern]
Sunset:  2020-09-04T23:55:46-04:00[Canada/Eastern]
```

The sun will set on September 4th, and will rise again about 30 minutes later. This is technically correct, because Alert is above the Arctic Circle, where the sun never sets all summer.

However, we wanted to get a result for June 15th, so we limit the window to 24 hours:

```java
SunTimes june15OnlyCycle = SunTimes.compute()
        .on(2020, 6, 15)            // June 15th, 2020, starting midnight
        .at(ALERT_CANADA)
        .timezone(ALERT_TZ)
        .limit(Duration.ofHours(24))
        .execute();
System.out.println("Sunset:  " + june15OnlyCycle.getSet());
System.out.println("Sunrise: " + june15OnlyCycle.getRise());
```

Instead of `limit(Duration.ofHours(24))`, we could also use `oneDay()`.

Now we get a different result. There is no sunrise or sunset on June 15th:

```text
Sunrise: null
Sunset:  null
```

But is the sun up or down all that day?

```java
System.out.println("Sun is up all day:   " + june15OnlyCycle.isAlwaysUp());
System.out.println("Sun is down all day: " + june15OnlyCycle.isAlwaysDown());
```

The result confirms that the sun is up all day:

```text
Sun is up all day:   true
Sun is down all day: false
```

## Parameter Reuse

As soon as `execute()` is invoked, _suncalc_ performs the calculations according to the given parameters, and creates a result object which is immutable. The parameters can be reused after that:

```java
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
```

The result is (at the time of writing):

```text
Today, the moon rises in Cologne at 2020-05-24T06:40:45+02:00[Europe/Berlin]
Tomorrow, the moon will rise in Cologne at 2020-05-25T07:23:06+02:00[Europe/Berlin]
But today, the moon still rises at 2020-05-24T06:40:45+02:00[Europe/Berlin]
```

As you can see in the last line, the invocation of `tomorrow()` did not affect the `today` result.

This can be useful for loops. Let's find out how much of the visible moon surface is lit by the sun on each day of January 2020.

```java
MoonIllumination.Parameters parameters = MoonIllumination.compute()
        .on(2020, 1, 1);

for (int i = 1; i <= 31; i++) {
    long percent = Math.round(parameters.execute().getFraction() * 100.0);
    System.out.println("On January " + i + " the moon was " + percent + "% lit.");
    parameters.plusDays(1);
}
```

The result (excerpt):

```text
On January 1 the moon was 29% lit.
On January 2 the moon was 38% lit.
On January 3 the moon was 48% lit.
 [...]
On January 29 the moon was 15% lit.
On January 30 the moon was 22% lit.
On January 31 the moon was 30% lit.
```

## Twilight

By default `SunTimes` computes the sunrise and sunset times as we would expect it. The sun rises when the upper part of the sun disc just appears on the horizon, and it sets when the upper part just vanishes. Because of our atmosphere, the sun is actually deeper on the horizon as it appears to be. This effect is called [atmospheric refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction), and it is factored into the calculation.

There are other [twilights](https://en.wikipedia.org/wiki/Twilight) that may be interesting. Photographers are especially interested in the [Golden hour](https://en.wikipedia.org/wiki/Golden_hour_(photography)), which gives a warm and soft sunlight. In the morning, Golden hour starts at an angle of -4° (which is the end of the Blue hour), and ends when the sun reaches an angle of 6°. In the evening, the golden hour starts when the sun reaches an angle of 6°, and ends at an angle of -4°.

To learn more about the individual twilight transitions, see the [illustration of twilights](usage.md#twilight).

Let's calculate the golden hour in Singapore for the next four Mondays starting June 1st, 2020:

```java
SunTimes.Parameters base = SunTimes.compute()
        .at(1.283333, 103.833333)            // Singapore
        .on(2020, 6, 1)
        .timezone("Asia/Singapore");

for (int i = 0; i < 4; i++) {
    SunTimes blue = base
            .copy()                          // Use a copy of base
            .plusDays(i * 7)
            .twilight(SunTimes.Twilight.BLUE_HOUR)      // Blue Hour, -4°
            .execute();
    SunTimes golden = base
            .copy()                          // Use a copy of base
            .plusDays(i * 7)
            .twilight(SunTimes.Twilight.GOLDEN_HOUR)    // Golden Hour, 6°
            .execute();

    System.out.println("Morning golden hour starts at " + blue.getRise());
    System.out.println("Morning golden hour ends at   " + golden.getRise());
    System.out.println("Evening golden hour starts at " + golden.getSet());
    System.out.println("Evening golden hour ends at   " + blue.getSet());
}
```

Note the `copy()` method! It copies the current set of parameters into a new parameter object. Both objects can then be changed independently of each other. This is very useful when you need to have different parameters in loops.

This is the result:

```text
Morning golden hour starts at 2020-06-01T06:43:13+08:00[Asia/Singapore]
Morning golden hour ends at   2020-06-01T07:26:24+08:00[Asia/Singapore]
Evening golden hour starts at 2020-06-01T18:38:50+08:00[Asia/Singapore]
Evening golden hour ends at   2020-06-01T19:22:02+08:00[Asia/Singapore]
Morning golden hour starts at 2020-06-08T06:44:16+08:00[Asia/Singapore]
Morning golden hour ends at   2020-06-08T07:27:41+08:00[Asia/Singapore]
Evening golden hour starts at 2020-06-08T18:40:01+08:00[Asia/Singapore]
Evening golden hour ends at   2020-06-08T19:23:27+08:00[Asia/Singapore]
Morning golden hour starts at 2020-06-15T06:45:35+08:00[Asia/Singapore]
Morning golden hour ends at   2020-06-15T07:29:10+08:00[Asia/Singapore]
Evening golden hour starts at 2020-06-15T18:41:25+08:00[Asia/Singapore]
Evening golden hour ends at   2020-06-15T19:25:00+08:00[Asia/Singapore]
Morning golden hour starts at 2020-06-22T06:47:04+08:00[Asia/Singapore]
Morning golden hour ends at   2020-06-22T07:30:41+08:00[Asia/Singapore]
Evening golden hour starts at 2020-06-22T18:42:56+08:00[Asia/Singapore]
Evening golden hour ends at   2020-06-22T19:26:32+08:00[Asia/Singapore]
```

## Moon Phase

I'd like to print a calendar of 2023, and mark all the days having a full moon. As I print a calendar, I'm only interested in the day of full moon, but I won't care for the concrete time. _suncalc_ can get me a list of all the days having a full moon.

As the visible moon phase is identical on every place on earth, we won't have to set a location here.

But we have to be careful! Since there are about 29.5 days between two full moons, a month might actually have two full moons. For this reason, we cannot simply iterate over the months. Instead we take the previous full moon, add one day so we won't find the same full moon again, and use this date as a base for the next iteration.

```java
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
```

The result is:

```text
2023-01-07
2023-02-05
2023-03-07
2023-04-06
2023-05-05
2023-06-04
2023-07-03
2023-08-01
2023-08-31
2023-09-29
2023-10-28
2023-11-27
2023-12-27
```

As you can see, there are two full moons in August 2023.

## Sun and Moon Positions

I'm in Tokyo. It's November 13th 2018, 10:03:24. In what direction do I have to look in order to see the sun and the moon?

```java
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
```

Note the invocations of `sameLocationAs()` and `sameTimeAs()`. Both methods are useful to copy the location and time parameter from other pararameter objects. The other parameter object won't need to be of the same type, so the `MoonPosition` can just "steal" the location and time from the `SunPosition`.

The result is:

```text
The sun can be seen 156,6° clockwise from the North and 33,0° above the horizon.
It is about 148075152 km away right now.
The moon can be seen 109,0° clockwise from the North and -9,5° above the horizon.
It is about 404629 km away right now.
```

The sun is in the southeast and about 33° above the horizon. The moon is to the east, but below the horizon, so it is not visible right now.

