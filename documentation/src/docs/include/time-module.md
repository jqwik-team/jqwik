This module's artefact name is `jqwik-time`. It's supposed to provide arbitraries,
default generation and annotations for date and time types.

This module is part of jqwik's default dependencies.

The module provides: 

- [Generation of Dates](#generation-of-dates)
    - [default generation](#default-generation-of-dates) for date-related Java types
    - [Programmatic API](#programmatic-generation-of-dates) to configure date-related types
    
- [Generation of Times](#generation-of-times)
    - [default generation](#default-generation-of-times) for time-related Java types
    - [Programmatic API](#programmatic-generation-of-times) to configure time-related types
    
- [Generation of DateTimes](#generation-of-datetimes)
    - [default generation](#default-generation-of-datetimes) for date time-related Java types
    - [Programmatic API](#programmatic-generation-of-datetimes) to configure date time-related types

#### Generation of Dates

##### Default Generation of Dates

Default generation currently is supported for `LocalDate`, `Year`, `YearMonth`,
`DayOfWeek`, `MonthDay` and `Period`. Here's a small example:

```java
@Property
void generateLocalDatesWithAnnotation(@ForAll @DateRange(min = "2019-01-01", max = "2020-12-31") LocalDate localDate) {
  assertThat(localDate).isBetween(
    LocalDate.of(2019, 1, 1),
    LocalDate.of(2020, 12, 31)
  );
}
```

The following annotations can be used to constrain default generation of the enumerated types:

- [`@DateRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DateRange.html)
- [`@YearRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/YearRange.html)
- [`@YearMonthRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/YearMonthRange.html)
- [`@MonthRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/MonthRange.html)
- [`@MonthDayRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/MonthDayRange.html)
- [`@DayOfMonthRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DayOfMonthRange.html)
- [`@DayOfWeekRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DayOfWeekRange.html)
- [`@PeriodRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/PeriodRange.html)

`@DateRange`, `@MonthDayRange`, `@YearMonthRange` and `@PeriodRange` 
use the ISO format for date strings. 
Examples: `2013-05-25`, `--05-25`, `2013-05` and `P1Y2M15D`.

##### Programmatic Generation of Dates

Programmatic generation of dates and date-related types always starts with a static
method call on class [`Dates`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html).
For example:

```java
@Property
void generateLocalDates(@ForAll("dates") LocalDate localDate) {
  assertThat(localDate).isAfter(LocalDate.of(2000, 12, 31));
}

@Provide
Arbitrary<LocalDate> dates() {
  return Dates.dates().atTheEarliest(LocalDate.of(2001, 1, 1));
}
```

Here's the list of available methods:

- [`LocalDateArbitrary dates()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#dates())
- [`CalendarArbitrary datesAsCalendar()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#datesAsCalendar())
- [`DateArbitrary datesAsDate()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#datesAsDate())
- [`YearArbitrary years()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#years())
- [`Arbitrary<Month> months()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#months())
- [`Arbitrary<Integer> daysOfMonth()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#daysOfMonth())
- [`Arbitrary<DayOfWeek> daysOfWeek()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#daysOfWeek())
- [`YearMonthArbitrary yearMonths()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#yearMonths())
- [`MonthDayArbitrary monthDays()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#monthDays())
- [`PeriodArbitrary periods()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#periods())

###### LocalDateArbitrary

- The target type is `LocalDate`.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.

###### CalendarArbitrary

- The target type is `Calendar`. The time-related parts of `Calendar` instances are set to 0.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.

###### DateArbitrary

- The target type is `Date`. The time-related parts of `Date` instances are set to 0.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.

###### YearArbitrary

- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`.

###### YearMonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- By default, only years between 1900 and 2500 are generated.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.

###### MonthDayArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.

###### PeriodArbitrary

- By default, periods between `-1000 years` and `1000 years` are generated.
- Generated periods are always in a "reduced" form, 
  i.e. months are always between `-11` and `11` and days between `-30` and `30`.   
- You can constrain the minimum and maximum value using `between(Period min, Period max)`.
- If you really want something like `Period.ofDays(3000)` generate an integer
  and map it on `Period`.

#### Generation of Times

##### Default Generation of Times

Default generation currently is supported for `LocalTime`, `OffsetTime`, `ZoneOffset`,
`TimeZone`, `ZoneId` and `Duration`. Here's a small example:

```java
@Property
void generateLocalTimesWithAnnotation(@ForAll @TimeRange(min = "01:32:21", max = "03:49:32") LocalTime localTime) {
    assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21));
    assertThat(time).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
}
```

The following annotations can be used to constrain default generation of the enumerated types:

- [`@TimeRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/TimeRange.html)
- [`@OffsetRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/OffsetRange.html)
- [`@HourRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/HourRange.html)
- [`@MinuteRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/MinuteRange.html)
- [`@SecondRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/SecondRange.html)
- [`@Precision`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/Precision.html)
- [`@DurationRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DurationRange.html)

`@TimeRange`, `@OffsetRange` and `@DurationRange` 
use the standard format of their classes. 
Examples:

- `@TimeRange`: "01:32:31.394920222", "23:43:21" or "03:02" (See [`LocalTime.parse`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html#parse-java.lang.CharSequence-))
- `@OffsetRange`: "-09:00", "+3", "+11:22:33" or "Z" (See [`ZoneOffset.of`](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneOffset.html#of-java.lang.String-))
- `@DurationRange`: "PT-3000H-39M-22.123111444S", "PT1999H22M11S" or "P2DT3H4M" (See [`Duration.parse`](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-))

##### Programmatic Generation of Times

Programmatic generation of times always starts with a static
method call on class [`Times`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html).
For example:

```java
@Property
void generateLocalTimes(@ForAll("times") LocalTime localTime) {
  assertThat(localTime).isAfter(LocalTime.of(13, 53, 21));
}

@Provide
Arbitrary<LocalTime> times() {
  return Times.times().atTheEarliest(LocalTime.of(13, 53, 22));
}
```

Here's the list of available methods:

- [`LocalTimeArbitrary times()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html#times())
- [`OffsetTimeArbitrary offsetTimes()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html#offsetTimes())
- [`ZoneOffsetArbitrary zoneOffsets()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html#zoneOffsets())
- [`Arbitrary<TimeZone> timeZones()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html#timeZones())
- [`Arbitrary<ZoneId> zoneIds()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html#zoneIds())
- [`DurationArbitrary durations()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Times.html#durations())

###### LocalTimeArbitrary

- The target type is `LocalTime`.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

###### OffsetTimeArbitrary

- The target type is `OffsetTime`.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain the minimum and maximum time value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the minimum and maximum value for offset using `offsetBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

###### ZoneOffsetArbitrary

- The target type is `ZoneOffset`.
- You can constrain its minimum and maximum value using `between(min, max)`.

###### DurationArbitrary

- The target type is `Duration`.
- By default, precision is seconds.
- You can constrain its minimum and maximum value using `between(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

#### Generation of DateTimes

##### Default Generation of DateTimes

Default generation currently is supported for `LocalDateTime`, `Instant` and `OffsetDateTime`. 
Here's a small example:

```java
@Property
void generateLocalDateTimesWithAnnotation(@ForAll @DateTimeRange(min = "2019-01-01T01:32:21", max = "2020-12-31T03:11:11") LocalDateTime localDateTime) {
  assertThat(localDateTime).isBetween(
    LocalDateTime.of(2019, 1, 1, 1, 32, 21),
    LocalDateTime.of(2020, 12, 31, 3, 11, 11)
  );
}
```

The following annotations can be used to constrain default generation of the enumerated types:

- [`@DateTimeRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DateTimeRange.html)
- [`@InstantRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/InstantRange.html)
- [`@DateRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DateRange.html)
- [`@YearRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/YearRange.html)
- [`@MonthRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/MonthRange.html)
- [`@DayOfMonthRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DayOfMonthRange.html)
- [`@DayOfWeekRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/DayOfWeekRange.html)
- [`@TimeRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/TimeRange.html)
- [`@HourRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/HourRange.html)
- [`@MinuteRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/MinuteRange.html)
- [`@SecondRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/SecondRange.html)
- [`@OffsetRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/OffsetRange.html)
- [`@Precision`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/Precision.html)

`@DateTimeRange`, `@InstantRange`, `@DateRange`, `@TimeRange` and `@OffsetRange` use the standard format of their classes. 
Examples: 

- `@DateTimeRange`: `2013-05-25T01:34:22.231`
- `@InstantRange`: `2013-05-25T01:34:22.231Z`
- `@DateRange`: `2013-05-25`
- `@TimeRange`: "01:32:31.394920222", "23:43:21" or "03:02"
- `@OffsetRange`: "-09:00", "+3", "+11:22:33" or "Z"

##### Programmatic Generation of DateTimes

Programmatic generation of date times always starts with a static
method call on class [`DateTimes`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/DateTimes.html).
For example:

```java
@Property
void generateLocalDateTimes(@ForAll("dateTimes") LocalDateTime localDateTime) {
  assertThat(localDateTime).isAfter(LocalDateTime.of(2013, 5, 25, 19, 48, 32));
}

@Provide
Arbitrary<LocalDateTime> dateTimes() {
  return DateTimes.dateTimes().atTheEarliest(LocalDateTime.of(2013, 5, 25, 19, 48, 33));
}
```

Here's the list of available methods:

- [`LocalDateTimeArbitrary dateTimes()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#dateTimes())
- [`InstantArbitrary instants()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#instants())
- [`OffsetDateTimeArbitrary offsetDateTimes()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#offsetDateTimes())

###### LocalDateTimeArbitrary

- The target type is `LocalDateTime`.
- By default, only years between 1900 and 2500 are generated.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain its minimum and maximum value for dates using `dateBetween(min, max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can constrain the minimum and maximum time value using `timeBetween(min, max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

###### InstantArbitrary

- The target type is `Instant`.
- By default, only years between 1900 and 2500 are generated.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- The maximum possible year is 999999999.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain its minimum and maximum value for dates using `dateBetween(min, max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can constrain the minimum and maximum time value using `timeBetween(min, max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

###### OffsetDateTimeArbitrary

- The target type is `OffsetDateTime`.
- By default, only years between 1900 and 2500 are generated.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain its minimum and maximum value for date times using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain its minimum and maximum value for dates using `dateBetween(min, max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can constrain the minimum and maximum time value using `timeBetween(min, max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the minimum and maximum value for offset using `offsetBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.
