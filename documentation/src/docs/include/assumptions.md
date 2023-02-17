If you want to constrain the set of generated values in a way that embraces
more than one parameter, [filtering](#filtering) does not work. What you
can do instead is putting one or more assumptions at the beginning of your property.

[The following property](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/AssumptionExamples.java)
works only on strings that are not equal:

```java
@Property
boolean comparingUnequalStrings(
        @ForAll @StringLength(min = 1, max = 10) String string1,
        @ForAll @StringLength(min = 1, max = 10) String string2
) {
    Assume.that(!string1.equals(string2));

    return string1.compareTo(string2) != 0;
}
```

This is a reasonable use of
[`Assume.that(boolean condition)`](/docs/${docsVersion}/javadoc/net/jqwik/api/Assume.html#that(boolean))
because most generated value sets will pass through.

Have a look at a seemingly similar example:

```java
@Property
boolean findingContainedStrings(
        @ForAll @StringLength(min = 1, max = 10) String container,
        @ForAll @StringLength(min = 1, max = 5) String contained
) {
    Assume.that(container.contains(contained));

    return container.indexOf(contained) >= 0;
}
```

Despite the fact that the property condition itself is correct, the property will most likely
fail with the following message:

```
org.opentest4j.AssertionFailedError: 
    Property [findingContainedStrings] exhausted after [1000] tries and [980] rejections

tries = 1000 
checks = 20 
generation = RANDOMIZED
after-failure = SAMPLE_FIRST
when-fixed-seed = ALLOW
edge-cases#mode = MIXIN 
seed = 1066117555581106850
```

The problem is that - given a random generation of two strings - only in very few cases
one string will be contained in the other. _jqwik_ will report a property as `exhausted`
if the ratio between generated and accepted parameters is higher than 5. You can change
the maximum discard ratio by specifying a parameter `maxDiscardRatio` in the
[`@Property`](/docs/${docsVersion}/javadoc/net/jqwik/api/Property.html) annotation.
That's why changing to `@Property(maxDiscardRatio = 100)` in the previous example
will probably result in a successful property run, even though only a handful
cases - of 1000 generated - will actually be checked.

In many cases turning up the accepted discard ration is a bad idea. With some creativity
we can often avoid the problem by generating out test data a bit differently.
Look at this variant of the above property, which also uses
[`Assume.that()`](/docs/${docsVersion}/javadoc/net/jqwik/api/Assume.html#that(boolean))
but with a much lower discard ratio:

```java
@Property
boolean findingContainedStrings_variant(
        @ForAll @StringLength(min = 5, max = 10) String container,
        @ForAll @IntRange(min = 1, max = 5) int length,
        @ForAll @IntRange(min = 0, max = 9) int startIndex
) {
    Assume.that((length + startIndex) <= container.length());

    String contained = container.substring(startIndex, startIndex + length);
    return container.indexOf(contained) >= 0;
}
```
