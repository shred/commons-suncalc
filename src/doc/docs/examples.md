# Examples

In this chapter, you will find code examples that demonstrate the use and the possibilities of the API. You can also find (and run) these examples in the [ExamplesTest](./testapidocs/src-html/org/shredzone/commons/suncalc/ExamplesTest.html) unit test.

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
Sunrise in Paris: Fri May 01 06:30:00 CEST 2020
Sunset in Paris:  Fri May 01 21:07:00 CEST 2020
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
Sunrise in New York: Fri May 01 11:54:00 CEST 2020
Sunset in New York:  Fri May 01 01:52:00 CEST 2020
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

The result looks better. The sun rises at May 1st 11:54, and sets on May 2nd 1:53, but still Center European Summer Time (CEST).

```text
Sunrise in New York: Fri May 01 11:54:00 CEST 2020
Sunset in New York:  Sat May 02 01:53:00 CEST 2020
```

The reason is that `getRise()` and `getSet()` return a Java `Date` object, which represents an instant without timezone. When we use `toString()`, it still uses your system's time zone to print the result. We have to format the `Date` object properly:

```java
DateFormat formatter = DateFormat.getDateTimeInstance();
formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
System.out.println("Sunrise in New York: " + formatter.format(newYorkTz.getRise()));
System.out.println("Sunset in New York:  " + formatter.format(newYorkTz.getSet()));
```

Now, we finally see the actual sunrise and sunset time in New York:

```text
Sunrise in New York: May 1, 2020 5:54:00 AM
Sunset in New York:  May 1, 2020 7:53:00 PM
```

!!! NOTE
    In the next examples, I will assume that the system is already set to the relevant timezone. I do so by invoking `TimeZone.setDefault()` at the beginning of each example, so the code won't become overly complex. This is _not_ the recommended way to do in production code, as it globally sets the timezone in the running Java instance. Use `timezone()` and proper output formatting instead.

## Time Window

[Alert, Nunavut, Canada](https://en.wikipedia.org/wiki/Alert,_Nunavut) is the northernmost place in the world with a permanent population. Let's find out when the sun rises and sets there on March 15th, 2020:

```java
double[] ALERT_CANADA = new double[] { 82.5, -62.316667 };
SunTimes march = SunTimes.compute()
        .on(2020, 3, 15)            // March 15th, 2020, starting midnight
        .at(ALERT_CANADA)           // Coordinates are stored in an array
        .execute();
System.out.println("Sunrise: " + march.getRise());
System.out.println("Sunset:  " + march.getSet());
```

The result is looking fine so far:

```text
Sunrise: Sun Mar 15 06:49:00 EDT 2020
Sunset:  Sun Mar 15 17:53:00 EDT 2020
```

What about June 15th?

```java
SunTimes june = SunTimes.compute()
        .on(2020, 6, 15)            // June 15th, 2020, starting midnight
        .at(ALERT_CANADA)
        .execute();
System.out.println("Sunrise: " + june.getRise());
System.out.println("Sunset:  " + june.getSet());
```

The result:

```text
Sunrise: null
Sunset:  null
```

Oh dear, is it a bug? No, it's because Alert is above the Arctic Circle. The sun never sets there all summer.

By default, _suncalc_ only examines a time window of 24 hours. In the example above, the time window starts on June 15th midnight, and ends on June 16th midnight. The sun is up the entire time window, it neither rises nor sets, so _suncalc_ returns `null`.

Or is the sun down all the time? We can find out:

```java
System.out.println("Sun is up all day:   " + june.isAlwaysUp());
System.out.println("Sun is down all day: " + june.isAlwaysDown());
```

The result:

```text
Sun is up all day:   true
Sun is down all day: false
```

Now it's confirmed that the sun is actually up all day.

By using `.fullCycle()`, we can extend the time window to infinite, to get the next sunrise and sunset time even if they are more than 24 hours ahead. The price is that the calculation may take a bit longer.

```java
SunTimes juneFullCycle = SunTimes.compute()
        .on(2020, 6, 15)            // June 15th, 2020, starting midnight
        .at(ALERT_CANADA)
        .fullCycle()                // No 24h limit, we want to get the full cycle
        .execute();
System.out.println("Sunset:  " + juneFullCycle.getSet());
System.out.println("Sunrise: " + juneFullCycle.getRise());
```

Now we're finally getting a result:

```text
Sunset:  Fri Sep 04 23:56:00 EDT 2020
Sunrise: Sat Sep 05 00:24:00 EDT 2020
```

## Parameter Recycling

As soon as `execute()` is invoked, _suncalc_ performs the calculations according to the given parameters, and creates a result object which is immutable. The parameters can be reused after that:

```java
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
Today, the moon rises in Cologne at Fri May 22 05:42:00 CEST 2020
Tomorrow, the moon will rise in Cologne at Sat May 23 06:08:00 CEST 2020
But today, the moon still rises at Fri May 22 05:42:00 CEST 2020
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

By default `SunTimes` computes the sunrise and sunset times as we would expect it. The sun rises when the upper part of the sun disc just appears on the horizon, and it sets when the upper part just vanishes. Because of our atmosphere, the sun is actually deeper on the horizon as it appears to be. This effect is called [atmospheric refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction), and is factored into the calculation.

There are other [twilights](https://en.wikipedia.org/wiki/Twilight) that may be interesting. Photographers are especially interested in the [golden hour](https://en.wikipedia.org/wiki/Golden_hour_(photography)), which gives a warm and soft sunlight. In the morning, golden hour starts at sunrise and ends when the sun reaches an angle of 6°. In the evening, the golden hour starts when the sun reaches an angle of 6°, and ends at sunset.

Let's calculate the golden hour in Singapore for the next four Mondays starting June 1st, 2020:

```java
SunTimes.Parameters base = SunTimes.compute()
        .at(1.283333, 103.833333)            // Singapore
        .on(2020, 6, 1);

for (int i = 0; i < 4; i++) {
    SunTimes blue = base.copy()
            .plusDays(i * 7)
            .twilight(SunTimes.Twilight.BLUE_HOUR)      // Blue Hour, -4°
            .execute();
    SunTimes golden = base.copy()
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
Morning golden hour starts at Mon Jun 01 06:43:00 SGT 2020
Morning golden hour ends at   Mon Jun 01 07:26:00 SGT 2020
Evening golden hour starts at Mon Jun 01 18:39:00 SGT 2020
Evening golden hour ends at   Mon Jun 01 19:22:00 SGT 2020
Morning golden hour starts at Mon Jun 08 06:44:00 SGT 2020
Morning golden hour ends at   Mon Jun 08 07:28:00 SGT 2020
Evening golden hour starts at Mon Jun 08 18:40:00 SGT 2020
Evening golden hour ends at   Mon Jun 08 19:23:00 SGT 2020
Morning golden hour starts at Mon Jun 15 06:46:00 SGT 2020
Morning golden hour ends at   Mon Jun 15 07:29:00 SGT 2020
Evening golden hour starts at Mon Jun 15 18:41:00 SGT 2020
Evening golden hour ends at   Mon Jun 15 19:25:00 SGT 2020
Morning golden hour starts at Mon Jun 22 06:47:00 SGT 2020
Morning golden hour ends at   Mon Jun 22 07:31:00 SGT 2020
Evening golden hour starts at Mon Jun 22 18:43:00 SGT 2020
Evening golden hour ends at   Mon Jun 22 19:27:00 SGT 2020
```

## Moon Phase

I'd like to print a calendar of 2023, and mark all the days having a full moon. As I print a calendar, I'm only interested in the day of full moon, but I won't care for the concrete time. _suncalc_ can get me a list of all the days having a full moon.

As the visible moon phase is identical on every place on earth, we won't have to set a location here.

But we have to be careful! Since there are about 29.5 days between two full moons, a month may actually have two full moons. For this reason, we cannot simply iterate over the months. Instead we take the previous full moon, add one day so we won't find the same full moon again, and use this date as a base for the next iteration.

As parameters, we set the desired moon phase (`FULL_MOON`), and use `truncatedTo(DAYS)` so the result is truncated to the day of the event (i.e. the time is just cut off).

```java
Calendar cal = Calendar.getInstance();
cal.clear();
cal.set(2023, Calendar.JANUARY, 1);

MoonPhase.Parameters parameters = MoonPhase.compute()
        .phase(MoonPhase.Phase.FULL_MOON)
        .truncatedTo(TimeResultParameter.Unit.DAYS);

while (cal.get(Calendar.MONTH) < Calendar.DECEMBER) {
    MoonPhase phase = parameters.on(cal).execute();
    Date fullMoonAt = phase.getTime();

    System.out.println(fullMoonAt);

    cal.setTime(fullMoonAt);
    cal.add(Calendar.DAY_OF_MONTH, 1);
}
```

The result is:

```text
Sat Jan 07 00:00:00 CET 2023
Sun Feb 05 00:00:00 CET 2023
Tue Mar 07 00:00:00 CET 2023
Thu Apr 06 00:00:00 CEST 2023
Fri May 05 00:00:00 CEST 2023
Sun Jun 04 00:00:00 CEST 2023
Mon Jul 03 00:00:00 CEST 2023
Tue Aug 01 00:00:00 CEST 2023
Thu Aug 31 00:00:00 CEST 2023
Fri Sep 29 00:00:00 CEST 2023
Sat Oct 28 00:00:00 CEST 2023
Mon Nov 27 00:00:00 CET 2023
Wed Dec 27 00:00:00 CET 2023
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

