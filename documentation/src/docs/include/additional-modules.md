_jqwik_ comes with a few additional modules:

- The [`web` module](#web-module)
- The [`time` module](#time-module)
- The [`testing` module](#testing-module)

### Web Module

This modules artefact name is `jqwik-web`. It's supposed to provide arbitraries,
default generation and annotations for web related types. Currently only
[email generation](#email-address-generation) is supported.

This module is part of jqwik's default dependencies.


#### Email Address Generation

To generate email addresses you can either

- call up the static method [`Emails.emails()`](/docs/${docsVersion}/javadoc/net/jqwik/api/web/Emails.html#emails()).
  The return type is [`EmailArbitrary`](/docs/${docsVersion}/javadoc/net/jqwik/api/web/EmailArbitrary.html)
  which provides a few configuration methods.

- or use the [`@Email`](/docs/${docsVersion}/javadoc/net/jqwik/api/web/Email.html)
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

You can use it as follows:

```java
@Property
void defaultEmailAddresses(@ForAll @Email String email) {
    assertThat(email).contains("@");
}

@Property
void restrictedEmailAddresses(@ForAll @Email(quotedLocalPart = false, ipv4Host = false, ipv6Host = false) String email) {
    assertThat(email).contains("@");
}
```

### Time Module

This modules artefact name is `jqwik-time`. It's supposed to provide arbitraries,
default generation and annotations for date and time types.

This module is part of jqwik's default dependencies.

#### Generation of Dates

The date generation is in an extra module which have to be add in a project's dependency.
By default, years between 1900 and 2500 are generated. You can change this by setting min/max values.
You can create an arbitrary for date values by calling a static method on class `Dates`:

- [`DateArbitrary dates()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#dates())
- [`YearArbitrary years()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#years())
- [`MonthArbitrary months()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#months())
- [`DaysOfMonthArbitrary daysOfMonth()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#daysOfMonth())
- [`YearMonthArbitrary yearMonths()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#yearMonths())
- [`MonthDayArbitrary monthDays()`](/docs/${docsVersion}/javadoc/net/jqwik/time/api/Dates.html#monthDays())

In addition, you can constrain their values using the following functions:

##### DateArbitrary

- The target type is `LocalDate`
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.

You can use it as follows:

```java
@Property
void generateLocalDates(@ForAll("dates") LocalDate localDate) {
    assertThat(localDate).isNotNull();
}

@Provide
Arbitrary<LocalDate> dates() {
    return Dates.dates();
}
```

##### YearArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`.

##### MonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`.
- You can limit the generation of months to only a few months using `only(months)`.

##### DaysOfMonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`.

#### YearMonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.

##### MonthDayArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.

### Testing Module

This modules artefact name is `jqwik-testing`. It provides a few helpful methods
and classes for generator writers to test their generators - including 
edge cases and shrinking.

This module is _not_ in jqwik's default dependencies. It's usually added as a
test-implementation dependency.

