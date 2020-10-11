# FAQ

## There is a different result on another website/app. Which one is right?

Probably both. Calculating sun and moon positions is rather complex, there is no universal formula that can be used. Other tools might also include the topology or other factors. If the difference is less than two minutes, it is within the acceptable tolerance.

There is no official definition of supermoon and micromoon, so the results may differ from other sources. _suncalc_ assumes a supermoon if the moon is closer than 360,000 km from Earth, and a micromoon if the moon is farther than 405,000 km from Earth.

## Can you enhance the precision?

The positions of the sun and moon are approximated by a rather simple set of formulae. The results have an accuracy of about a minute, which should be good enough for common applications (like sunrise/sunset timers). This library is targeted for mobile devices, or devices with low computing power, and the precision is acceptable for that target. A higher precision would involve perturbation tables of all planets, and would multiply the necessary computing load.

## Can you add other planets or stars?

No. It would add much more complexity to this library. It is not meant to be used for astronomical purposes.

## What about sea tide levels?

As the tide directly depends on the position of the sun and moon, there could be some kind of `Tide` calculator class. But it's not as easy as that. The tide at a location also depends on geological conditions and currents. This library could only give a very rough estimation. At best it would have no practical use, at worst it would even be life threatening if someone relied on the results. There are sophisitcated tools that have been made just for this purpose, e.g. [JTides](https://arachnoid.com/JTides/).
