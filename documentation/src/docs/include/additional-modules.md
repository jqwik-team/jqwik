_jqwik_ comes with a few additional modules:

- The [`web` module](#web-module)
- The [`time` module](#time-module)
- The [`testing` module](#testing-module)

### Web Module

This module's artefact name is `jqwik-web`. It's supposed to provide arbitraries,
default generation and annotations for web related types. Currently only
[email generation](#email-address-generation) is supported.

This module is part of jqwik's default dependencies.


#### Email Address Generation

To generate email addresses you can either

- call up the static method [`Emails.emails()`](/docs/${docsVersion}/javadoc/net/jqwik/web/api/Emails.html#emails()).
  The return type is [`EmailArbitrary`](/docs/${docsVersion}/javadoc/net/jqwik/web/api/EmailArbitrary.html)
  which provides a few configuration methods.

- or use the [`@Email`](/docs/${docsVersion}/javadoc/net/jqwik/web/api/Email.html)
  annotation on `@ForAll` parameters as in the examples below.

An email address consists of two parts: `local-part` and `host`.
The complete email address is therefore `local-part@host`.
The `local-part` can be `unquoted` or `quoted` (in double quotes), which allows for more characters to be used.
The `host` can be a standard domain name, but also an IP (v4 or v6) address, surrounded by square brackets `[]`.

For example, valid email addresses are:
```
abc@example
abc@example.com
" "@example.example
"admin@server"@[192.168.201.0]
admin@[32::FF:aBc:79a:83B:FFFF:345]
```

You can use the following restrictions in `@Email` annotation:
- `unquotedLocalPart` to decide whether unquoted local parts are generated
- `quotedLocalPart` to decide whether quoted local parts are generated
- `domainHost` to decide whether domains are generated in the host part
- `ipv4Host` to decide whether ipv4 addresses are generated in the host part
- `ipv6Host` to decide whether ipv6 addresses are generated in the host part 
  
By default, only addresses with unquoted local part and domain hosts are 
generated (e.g. `me@myhost.com`), because many - if not most - applications 
and web forms only accept those.

You can use it as follows:

```java
@Property
void defaultEmailAddresses(@ForAll @Email String email) {
    assertThat(email).contains("@");
}

@Property
void restrictedEmailAddresses(@ForAll @Email(quotedLocalPart = true, ipv4Host = true, domainHost = false) String email) {
    assertThat(email).contains("@");
}
```

### Time Module

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

### Testing Module

This module's artefact name is `jqwik-testing`. It provides a few helpful methods
and classes for generator writers to test their generators - including 
edge cases and shrinking.

This module is _not_ in jqwik's default dependencies. It's usually added as a
test-implementation dependency.

