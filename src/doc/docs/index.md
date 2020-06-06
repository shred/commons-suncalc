# commons-suncalc

A Java library for calculation of sun and moon positions and phases.

The source code can be found at [GitHub](https://github.com/shred/commons-suncalc) and is distributed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Accuracy

Astronomical calculations are far more complex than throwing a few numbers into an obscure formula and then getting a fully accurate result. There is always a tradeoff between accuracy and computing time.

This library has its focus on getting _acceptable_ results at low cost, so it can also run on mobile devices, or devices with a low computing power. The results have an accuracy of about a minute, which should be good enough for common applications (like sunrise/sunset timers), but is probably not sufficient for astronomical purposes.

## Documentation

[Read the commons-suncalc documentation.](https://shredzone.org/maven/commons-suncalc/index.html)


## Dependencies and Requirements

_commons-suncalc_ requires at least Java 8, but has no other runtime dependencies. It can also be used on Android, API level 26 (Android 8.0 "Oreo") or higher.

!!! NOTE
    **For Android versions before API level 26**, please use [commons-suncalc v2](https://github.com/shred/commons-suncalc/tree/v2). It is similar to this version, but does not use the Java Date/Time API. Also see the [commons-suncalc v2 documentation](https://shredzone.org/maven/commons-suncalc-v2/index.html).

## Installation

_commons-suncalc_ is available at Maven Central. Just add this snippet to your `pom.xml`:

```xml
<dependency>
  <groupId>org.shredzone.commons</groupId>
  <artifactId>commons-suncalc</artifactId>
  <version>$version</version>
</dependency>
```

Or use this snippet in your `build.gradle` (e.g. in Android Studio):

```groovy
dependencies {
    compile('org.shredzone.commons:commons-suncalc:$version')
}
```

Replace `$version` with your desired version. The latest version is: ![maven central](https://shredzone.org/maven-central/org.shredzone.commons/commons-suncalc/badge.svg)

## Java Module

Add this line to your module descriptor:

```
requires org.shredzone.commons.suncalc;
```

## References

This library bases on:

* "Astronomy on the Personal Computer", 4th edition, by Oliver Montenbruck and Thomas Pfleger
* "Astronomical Algorithms" by Jean Meeus

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-suncalc). Feel free to send pull requests.
* Found a bug? Please [file a bug report](https://github.com/shred/commons-suncalc/issues).

## License

_commons-suncalc_ is open source software. The source code is distributed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
