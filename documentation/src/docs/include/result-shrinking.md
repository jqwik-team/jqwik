If a property could be falsified with a generated set of values, _jqwik_ will
try to "shrink" this sample in order to find a "smaller" sample that also falsifies the property.

Try this property:

```java
@Property
boolean stringShouldBeShrunkToAA(@ForAll @AlphaChars String aString) {
    return aString.length() > 5 || aString.length() < 2;
}
```

The test run result should look something like:

```
AssertionFailedError: Property [stringShouldBeShrunkToAA] falsified with sample {0="aa"}

tries = 38 
checks = 38 
...
Shrunk Sample (5 steps)
-------------------------
  aString: "AA"

Original Sample
---------------
  aString: "RzZ"
```

In this case the _original sample_ could be any string between 2 and 5 chars,
whereas the final _sample_ should be exactly `AA` since this is the shortest
failing string and `A` has the lowest numeric value of all allowed characters.

### Integrated Shrinking

_jqwik_'s shrinking approach is called _integrated shrinking_, as opposed to _type-based shrinking_
which most property-based testing tools use.
The general idea and its advantages are explained
[here](http://hypothesis.works/articles/integrated-shrinking/).

Consider a somewhat more complicated example:

```java
@Property
boolean shrinkingCanTakeAWhile(@ForAll("first") String first, @ForAll("second") String second) {
    String aString = first + second;
    return aString.length() > 5 || aString.length() < 2;
}

@Provide
Arbitrary<String> first() {
    return Arbitraries.strings()
        .withCharRange('a', 'z')
        .ofMinLength(1).ofMaxLength(10)
        .filter(string -> string.endsWith("h"));
}

@Provide
Arbitrary<String> second() {
    return Arbitraries.strings()
        .withCharRange('0', '9')
        .ofMinLength(0).ofMaxLength(10)
        .filter(string -> string.length() >= 1);
}
```

Shrinking still works, although there's quite a bit of filtering and string concatenation happening:
```
AssertionFailedError: Property [shrinkingCanTakeLong] falsified with sample {0="a", 1="000"}}

checks = 20 
tries = 20 
...
Shrunk Sample (3 steps)
-----------------------
  first: "a"
  second: "000"

Original Sample
---------------
  first: "h"
  second: "901"
```

This example also shows that sometimes there is no single "smallest example".
Depending on the starting random seed, this property will shrink to either
`{0="a", 1="000"}`, `{0="ah", 1="00"}` or `{0="aah", 1="0"}`, all of which
are considered to be the smallest possible for jqwik's current way of
measuring a sample's size.

### Switch Shrinking Off

Sometimes shrinking takes a really long time or won't finish at all (usually a _jqwik_ bug!).
In those cases you can switch shrinking off for an individual property:

```java
@Property(shrinking = ShrinkingMode.OFF)
void aPropertyWithLongShrinkingTimes(
	@ForAll List<Set<String>> list1, 
	@ForAll List<Set<String>> list2
) {	... }
```

### Switch Shrinking to Full Mode

Sometimes you can find a message like

```
shrinking bound reached = after 1000 steps.
```

in your testrun's output.
This happens in rare cases when _jqwik_ has not found the end of its search for
simpler falsifiable values after 1000 iterations. In those cases you
can try

```java
@Property(shrinking = ShrinkingMode.FULL)
```

to tell _jqwik_ to go all the way, even if it takes a million steps,
even if it never ends...

### Change the Shrinking Target

By default shrinking of numbers will move towards zero (0).
If zero is outside the bounds of generation the closest number to zero -
either the min or max value - is used as a target for shrinking.
There are cases, however, when you'd like _jqwik_ to choose a different
shrinking target, usually when the default value of a number is not 0.

Consider generating signals with a standard frequency of 50 hz that can vary by
plus/minus 5 hz. If possible, shrinking of falsified scenarios should move
towards the standard frequency. Here's how the provider method might look:

```java
@Provide
Arbitrary<List<Signal>> signals() {
	Arbitrary<Long> frequencies = 
	    Arbitraries
            .longs()
            .between(45, 55)
            .shrinkTowards(50);

	return frequencies.map(f -> Signal.withFrequency(f)).list().ofMaxSize(1000);
}
```

Currently shrinking targets are supported for all [number types](#numeric-arbitrary-types).


