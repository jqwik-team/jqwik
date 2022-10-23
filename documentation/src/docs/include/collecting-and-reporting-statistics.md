In many situations you'd like to know if _jqwik_ will really generate
the kind of values you expect and if the frequency and distribution of
certain value classes meets your testing needs.
[`Statistics.collect()`](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/Statistics.html#collect(java.lang.Object...))
is made for this exact purpose.

In the most simple case you'd like to know how often a certain value
is being generated:

```java
@Property
void simpleStats(@ForAll RoundingMode mode) {
    Statistics.collect(mode);
}
```

will create an output similar to that:

```
[MyTest:simpleStats] (1000) statistics = 
    FLOOR       (158) : 16 %
    HALF_EVEN   (135) : 14 %
    DOWN        (126) : 13 %
    UP          (120) : 12 %
    HALF_UP     (118) : 12 %
    CEILING     (117) : 12 %
    UNNECESSARY (117) : 12 %
    HALF_DOWN   (109) : 11 %
```

More typical is the case in which you'll classify generated values
into two or more groups:

```java
@Property
void integerStats(@ForAll int anInt) {
    Statistics.collect(anInt > 0 ? "positive" : "negative");
}
```

```
[MyTest:integerStats] (1000) statistics = 
    negative (506) : 51 %
    positive (494) : 49 %
```

You can also collect the distribution in more than one category
and combine those categories:

```java
@Property
void combinedIntegerStats(@ForAll int anInt) {
    String posOrNeg = anInt > 0 ? "positive" : "negative";
    String evenOrOdd = anInt % 2 == 0 ? "even" : "odd";
    String bigOrSmall = Math.abs(anInt) > 50 ? "big" : "small";
    Statistics.collect(posOrNeg, evenOrOdd, bigOrSmall);
}
```

```
[MyTest:combinedIntegerStats] (1000) statistics = 
    negative even big   (222) : 22 %
    positive even big   (201) : 20 %
    positive odd big    (200) : 20 %
    negative odd big    (194) : 19 %
    negative even small ( 70) :  7 %
    positive odd small  ( 42) :  4 %
    negative odd small  ( 38) :  4 %
    positive even small ( 33) :  3 %
```

And, of course, you can combine different generated parameters into
one statistical group:

```java
@Property
void twoParameterStats(
    @ForAll @Size(min = 1, max = 10) List<Integer> aList,
    @ForAll @IntRange(min = 0, max = 10) int index
) {
    Statistics.collect(aList.size() > index ? "index within size" : null);
}
```

```
[MyTest:twoParameterStats] (1000) statistics = 
    index within size (507) : 51 %
```

As you can see, collected `null` values are not being reported.

[Here](https://github.com/jlink/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/statistics/StatisticsExamples.java)
are a couple of examples to try out.

### Labeled Statistics

If you want more than one statistic in a single property, you must give them labels for differentiation:

```java
@Property
void severalStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
    String range = anInt < 3 ? "small" : "large";
    Statistics.label("range").collect(range);
    Statistics.label("value").collect(anInt);
}
```

produces the following reports:

```
[MyTest:labeledStatistics] (1000) range = 
    large (783) : 78 %
    small (217) : 22 %

[MyTest:labeledStatistics] (1000) value = 
    1  (115) : 12 %
    5  (109) : 11 %
    10 (105) : 11 %
    4  (103) : 10 %
    2  (102) : 10 %
    3  ( 99) : 10 %
    6  ( 97) : 10 %
    8  ( 92) :  9 %
    7  ( 91) :  9 %
    9  ( 87) :  9 %
```

### Statistics Report Formatting

There is a
[`@StatisticsReport`](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/StatisticsReport.html)
annotation that allows to change statistics report
formats or to even switch it off. The annotation can be used on property methods
or on container classes.

The `value` attribute is of type
[StatisticsReportMode.OFF](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/StatisticsReport.StatisticsReportMode.html) and can have one of:

- __`STANDARD`__: Use jqwik's standard reporting format. This is used anyway
  if you leave the annotation away.
- __`OFF`__: Switch statistics reporting off
- __`PLUG_IN`__: Plug in your homemade format. This is the default so that
  you only have to provide the `format` attribute
  [as shown below](#plug-in-your-own-statistics-report-format)

When using [labeled statistics](#labeled-statistics) you can set mode and format
for each label individually by using the annotation attribute `@StatisticsReport.value`.

#### Switch Statistics Reporting Off

You can switch off statistics report as simple as that:

```java
@Property
@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
void queryStatistics(@ForAll int anInt) {
    Statistics.collect(anInt);
}
```

Or you can just switch it off for properties that do not fail:

```java
@Property
@StatisticsReport(onFailureOnly = true)
void queryStatistics(@ForAll int anInt) {
    Statistics.collect(anInt);
}
```


#### Histograms

_jqwik_ comes with two report formats to display collected data as histograms:
[`Histogram`](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/Histogram.html)
and [`NumberRangeHistogram`](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/NumberRangeHistogram.html).

`Histogram` displays the collected raw data as a histogram:

```java
@Property(generation = GenerationMode.RANDOMIZED)
@StatisticsReport(format = Histogram.class)
void integers(@ForAll("gaussians") int aNumber) {
    Statistics.collect(aNumber);
}

@Provide
Arbitrary<Integer> gaussians() {
    return Arbitraries
            .integers()
            .between(0, 20)
            .shrinkTowards(10)
            .withDistribution(RandomDistribution.gaussian());
}
```

```
[HistogramExamples:integers] (1000) statistics = 
       # | label | count | 
    -----|-------|-------|---------------------------------------------------------------------------------
       0 |     0 |    13 | ■■■■
       1 |     1 |    13 | ■■■■
       2 |     2 |    15 | ■■■■■
       3 |     3 |     6 | ■■
       4 |     4 |    10 | ■■■
       5 |     5 |    22 | ■■■■■■■
       6 |     6 |    49 | ■■■■■■■■■■■■■■■■
       7 |     7 |    60 | ■■■■■■■■■■■■■■■■■■■■
       8 |     8 |   102 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
       9 |     9 |   100 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      10 |    10 |   233 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      11 |    11 |   114 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      12 |    12 |    74 | ■■■■■■■■■■■■■■■■■■■■■■■■■
      13 |    13 |    64 | ■■■■■■■■■■■■■■■■■■■■■
      14 |    14 |    43 | ■■■■■■■■■■■■■■
      15 |    15 |    32 | ■■■■■■■■■■
      16 |    16 |    16 | ■■■■■
      17 |    17 |     8 | ■■
      18 |    18 |     7 | ■■
      19 |    20 |    19 | ■■■■■■
```

`NumberRangeHistogram` clusters the collected raw data into ranges:

```java
@Property(generation = GenerationMode.RANDOMIZED)
@StatisticsReport(format = NumberRangeHistogram.class)
void integersInRanges(@ForAll @IntRange(min = -1000, max = 1000) int aNumber) {
    Statistics.collect(aNumber);
}
```

```
[HistogramExamples:integersInRanges] (1000) statistics = 
       # |         label | count | 
    -----|---------------|-------|---------------------------------------------------------------------------------
       0 | [-1000..-900[ |    20 | ■■■■■
       1 |  [-900..-800[ |    17 | ■■■■
       2 |  [-800..-700[ |    16 | ■■■■
       3 |  [-700..-600[ |     8 | ■■
       4 |  [-600..-500[ |    12 | ■■■
       5 |  [-500..-400[ |    14 | ■■■
       6 |  [-400..-300[ |    17 | ■■■■
       7 |  [-300..-200[ |    46 | ■■■■■■■■■■■
       8 |  [-200..-100[ |    59 | ■■■■■■■■■■■■■■
       9 |     [-100..0[ |   315 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      10 |      [0..100[ |   276 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      11 |    [100..200[ |    47 | ■■■■■■■■■■■
      12 |    [200..300[ |    49 | ■■■■■■■■■■■■
      13 |    [300..400[ |    25 | ■■■■■■
      14 |    [400..500[ |    14 | ■■■
      15 |    [500..600[ |    13 | ■■■
      16 |    [600..700[ |    15 | ■■■
      17 |    [700..800[ |    14 | ■■■
      18 |    [800..900[ |    11 | ■■
      19 |   [900..1000] |    12 | ■■■
```

Both types can be subclassed to override behaviour like the number of buckets,
the maximum drawing range of the bar, the order of elements, the label of a bucket
and the header of the label column.

#### Make Your Own Statistics Report Format

In order to format statistics to your own liking you have to create an
implementation of type
[StatisticsReportFormat](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/StatisticsReportFormat.html) and

```java
@Property
@StatisticsReport(format = MyStatisticsFormat.class)
void statisticsWithHandMadeFormat(@ForAll Integer anInt) {
    String range = anInt < 0 ? "negative" : anInt > 0 ? "positive" : "zero";
    Statistics.collect(range);
}

class MyStatisticsFormat implements StatisticsReportFormat {
    @Override
    public List<String> formatReport(List<StatisticsEntry> entries) {
        return entries.stream()
    	              .map(e -> String.format("%s: %d", e.name(), e.count()))
    	              .collect(Collectors.toList());
    }
}
```

Running this property should produce a report similar to that:

```
[StatisticsExamples:statisticsWithHandMadeFormat] (1000) statistics = 
    negative: 520
    positive: 450
    zero: 30
```

### Checking Coverage of Collected Statistics

Just looking at the statistics of generated values might not be sufficient.
Sometimes you want to make sure that certain scenarios are being covered by
your generators and fail a property otherwise. In _jqwik_ you do that
by first
[collecting statistics](#collecting-and-reporting-statistics)
and then specifying coverage conditions for those statistics.

#### Check Percentages and Counts

The following example does that for generated values of enum `RoundingMode`:

```java
@Property(generation = GenerationMode.RANDOMIZED)
void simpleStats(@ForAll RoundingMode mode) {
    Statistics.collect(mode);

    Statistics.coverage(coverage -> {
        coverage.check(RoundingMode.CEILING).percentage(p -> p > 5.0);
        coverage.check(RoundingMode.FLOOR).count(c -> c > 2);
    });
}
```

The same thing is possible for values collected with a specific label
and in a fluent API style.

```java
@Property(generation = GenerationMode.RANDOMIZED)
void labeledStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
    String range = anInt < 3 ? "small" : "large";
	
    Statistics.label("range")
              .collect(range)
              .coverage(coverage -> coverage.check("small").percentage(p -> p > 20.0));
    Statistics.label("value")
              .collect(anInt)
              .coverage(coverage -> coverage.check(0).count(c -> c > 0));
}
```

Start by looking at
[`Statistics.coverage()`](/docs/${docsVersion}/javadoc/net/jqwik/api/statistics/Statistics.html#coverage(java.util.function.Consumer))
to see all the options you have for checking percentages and counts.

#### Check Ad-hoc Query Coverage

Instead of classifying values at collection time you have the possibility to
collect the raw data and use a query when doing coverage checking:

```java
@Property
@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
void queryStatistics(@ForAll int anInt) {
    Statistics.collect(anInt);
	
    Statistics.coverage(coverage -> {
    Predicate<List<Integer>> isZero = params -> params.get(0) == 0;
        coverage.checkQuery(isZero).percentage(p -> p > 5.0);
    });
}
```

In those cases you probably want to
[switch off reporting](#switch-statistics-reporting-off),
otherwise the reports might get very long - and without informative value.


#### Check Coverage of Regex Pattern

Another option - similar to [ad-hoc querying](#check-ad-hoc-query-coverage) -
is the possibility to check coverage of a regular expression pattern:

```java
@Property
@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
void patternStatistics(@ForAll @NumericChars String aString) {
    Statistics.collect(aString);
	
    Statistics.coverage(coverage -> {
        coverage.checkPattern("0.*").percentage(p -> p >= 10.0);
    });
}
```

Mind that only _single_ values of type `CharSequence`, which includes `String`, 
can be checked against a pattern.
All other types will not match the pattern.
