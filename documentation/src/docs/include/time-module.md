This module's artefact name is `jqwik-time`. It's supposed to provide arbitraries,
default generation and annotations for date and time types.

This module is part of jqwik's default dependencies.

#### Generation of Dates

The date generation is in an extra module which have to be add in a project's dependency.
By default, years between 1900 and 2500 are generated. You can change this by setting min/max values.
You can create an arbitrary for date values by using `@ForAll` annotation to date specific types (`LocalDate`, `Calendar`, `Date`, `MonthDay`, `Period`, `Year` and `YearMonth`) or by calling a static method on class `Dates`:

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

In addition, you can constrain their values using the following functions and annotations:

##### LocalDateArbitrary

- The target type is `LocalDate`
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)`, `atTheLatest(max)` and `@DateRange`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)` and `@YearRange`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)` and `@MonthRange`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)` and `@DayOfMonthRange`.
- You can constrain the minimum and maximum value for days of week using `@DayOfWeekRange`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)` and `@LeapYears`.

You can use it as follows:

```java
@Property
void generateLocalDatesWithAnnotation(@ForAll LocalDate localDate) {
    assertThat(localDate).isNotNull();
}

@Property
void generateLocalDates(@ForAll("dates") LocalDate localDate) {
    assertThat(localDate).isNotNull();
}

@Provide
Arbitrary<LocalDate> dates() {
    return Dates.dates();
}
```

##### CalendarArbitrary

- The target type is `Calendar`
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)`, `atTheLatest(max)` and `@DateRange`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)` and `@YearRange`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)` and `@MonthRange`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)` and `@DayOfMonthRange`.
- You can constrain the minimum and maximum value for days of week using `@DayOfWeekRange`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)` and `@LeapYears`.

##### DateArbitrary

- The target type is `Date`
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)`, `atTheLatest(max)` and `@DateRange`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)` and `@YearRange`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)` and `@MonthRange`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)` and `@DayOfMonthRange`.
- You can constrain the minimum and maximum value for days of week using `@DayOfWeekRange`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)` and `@LeapYears`.

##### YearArbitrary

- You can constrain its minimum and maximum value using `between(min, max)` and `@YearRange`.

#### YearMonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)`, `atTheLatest(max)` and `@YearMonthRange`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)` and `@YearRange`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)` and `@MonthRange`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)` and `@LeapYears`.

##### MonthDayArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)`, `atTheLatest(max)` and `@MonthDayRange`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)` and `@MonthRange`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.

##### PeriodArbitrary

- You can constrain the minimum and maximum value for years using `yearsBetween(min, max)` and `@PeriodYearRange`.
- You can constrain the minimum and maximum value for months using `monthsBetween(min, max)` and `@PeriodMonthRange`.
- You can constrain the minimum and maximum value for days using `daysBetween(min, max)` and `@PeriodDayRange`.
- By default, years between `Integer.MIN_VALUE` and `Integer.MAX_VALUE`, months between `0` and `11` and days between `0` and `30` are generated.

##### Special note on `@DateRange`, `@MonthDayRange` and `@YearMonthRange`

`@DateRange`, `@MonthDayRange` and `@YearMonthRange` use the ISO format for date strings. Examples: `2013-05-25`, `--05-25` and `2013-05`.
