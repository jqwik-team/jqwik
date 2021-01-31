This module's artefact name is `jqwik-time`. It's supposed to provide arbitraries,
default generation and annotations for date and time types.

This module is part of jqwik's default dependencies.

The module provides: 
- [default generation](#default-generation-of-dates) for date-related Java types
- [Programmatic API](#programmatic-generation-of-dates) to configure date-related types

#### Default Generation of Dates

Default generation currently is supported for `LocalDate`, `Year`, `YearMonth`,
`DayOfWeek`, `MonthDay` and `Period`. Here's an small example:

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
- [`@LeapYears`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/LeapYears.html)
- [`@PeriodRange`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/constraints/PeriodRange.html)

`@DateRange`, `@MonthDayRange`, `@YearMonthRange` and `@PeriodRange` 
use the ISO format for date strings. 
Examples: `2013-05-25`, `--05-25`, `2013-05` and `P1Y2M15D`.

#### Programmatic Generation of Dates

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

##### LocalDateArbitrary

- The target type is `LocalDate`.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

##### CalendarArbitrary

- The target type is `Calendar`. The time-related parts of `Calendar` instances are set to 0.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

##### DateArbitrary

- The target type is `Date`. The time-related parts of `Date` instances are set to 0.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

##### YearArbitrary

- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`.

#### YearMonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- By default, only years between 1900 and 2500 are generated.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

##### MonthDayArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.

##### PeriodArbitrary

- By default, periods between `-1000 years` and `1000 years` are generated.
- Generated periods are always in a "reduced" form, 
  i.e. months are always between `-11` and `11` and days between `-30` and `30`.   
- You can constrain the minimum and maximum value using `between(Period min, Period max)`.
- If you really want something like `Period.ofDays(3000)` generate an integer
  and map it on `Period`.
