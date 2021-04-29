package net.jqwik.time.api.dates.localDate;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

public class InvalidConfigurationTests {

	@Example
	void minDateMustNotBeBeforeJan1_1() {
		assertThatThrownBy(
			() -> Dates.dates().between(LocalDate.of(0, 12, 31), LocalDate.of(2000, 1, 1))
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void minYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.dates().yearBetween(0, 2000)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.dates().yearBetween(-1000, 2000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.dates().yearBetween(2000, 0)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.dates().yearBetween(2000, -1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void monthsMustBeBetween1And12() {
		assertThatThrownBy(
			() -> Dates.dates().monthBetween(0, 12)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.dates().monthBetween(12, 0)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.dates().monthBetween(1, 13)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.dates().monthBetween(13, 1)
		).isInstanceOf(DateTimeException.class);
	}

	@Property
	void atTheEarliestYearMustNotBelow1(
		@ForAll @IntRange(min = -999_999_999, max = 0) int year,
		@ForAll Month month,
		@ForAll @DayOfMonthRange int day
	) {
		try {
			LocalDate date = LocalDate.of(year, month, day);
			assertThatThrownBy(
				() -> Dates.dates().atTheEarliest(date)
			).isInstanceOf(IllegalArgumentException.class);
		} catch (DateTimeException e) {
			//do nothing
		}
	}

	@Property
	void atTheLatestYearMustNotBelow1(
		@ForAll @IntRange(min = -999_999_999, max = 0) int year,
		@ForAll Month month,
		@ForAll @DayOfMonthRange int day
	) {
		try {
			LocalDate date = LocalDate.of(year, month, day);
			assertThatThrownBy(
				() -> Dates.dates().atTheLatest(date)
			).isInstanceOf(IllegalArgumentException.class);
		} catch (DateTimeException e) {
			//do nothing
		}
	}

	@Example
	void invalidCombinationOfMinMaxValues() {
		assertThatThrownBy(
			() -> Dates.dates()
					   .between(LocalDate.of(2011, 5, 1), LocalDate.of(2012, 2, 1))
					   .monthBetween(3, 4)
					   .dayOfMonthBetween(11, 13)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void monthWithout31DaysButDayOfMonth31(@ForAll("monthWithout31Days") @Size(min = 1) Set<Month> months) {
		assertThatThrownBy(
			() -> Dates.dates()
					   .onlyMonths(months.toArray(new Month[]{}))
					   .dayOfMonthBetween(31, 31)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void februaryButDayOfMonth30Or31(@ForAll @IntRange(max = 1) int offset, @ForAll boolean different) {
		Assume.that(offset == 0 || !different);

		int min = 30 + offset;
		int max = different ? min + 1 : min;

		assertThatThrownBy(
			() -> Dates.dates()
					   .onlyMonths(FEBRUARY)
					   .dayOfMonthBetween(min, max)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property(tries = 2_000, maxDiscardRatio = 10)
	void februaryDayOfMonth29ButNoLeapYear(@ForAll @YearRange Year year, @ForAll @IntRange(max = 2) int offset) {
		Assume.that(year.getValue() % 4 == 1);
		LocalDate minDate = LocalDate.of(year.getValue(), JANUARY, 1);
		LocalDate maxDate = LocalDate.of(year.getValue() + 2, DECEMBER, 31);

		int min = 29;
		int max = min + offset;

		assertThatThrownBy(
			() -> Dates.dates()
					   .between(minDate, maxDate)
					   .onlyMonths(FEBRUARY)
					   .dayOfMonthBetween(min, max)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void betweenAndMonthWithout31DaysButDayOfMonth31(
		@ForAll LocalDate minDate,
		@ForAll MonthDay monthDay,
		@ForAll("monthWithout31Days") @Size(min = 1) Set<Month> months
	) {
		Assume.that(!monthDay.equals(MonthDay.of(FEBRUARY, 29)) || new GregorianCalendar().isLeapYear(minDate.getYear() + 1));

		LocalDate maxDate = LocalDate.of(minDate.getYear() + 1, monthDay.getMonth(), monthDay.getDayOfMonth());

		assertThatThrownBy(
			() -> Dates.dates()
					   .between(minDate, maxDate)
					   .onlyMonths(months.toArray(new Month[]{}))
					   .dayOfMonthBetween(31, 31)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void betweenAndFebruaryButDayOfMonth30Or31(
		@ForAll LocalDate minDate,
		@ForAll MonthDay monthDay,
		@ForAll @IntRange(max = 1) int offset,
		@ForAll boolean different
	) {
		Assume.that(offset == 0 || !different);
		Assume.that(!monthDay.equals(MonthDay.of(FEBRUARY, 29)) || new GregorianCalendar().isLeapYear(minDate.getYear() + 1));

		LocalDate maxDate = LocalDate.of(minDate.getYear() + 1, monthDay.getMonth(), monthDay.getDayOfMonth());
		int min = 30 + offset;
		int max = different ? min + 1 : min;

		assertThatThrownBy(
			() -> Dates.dates()
					   .between(minDate, maxDate)
					   .onlyMonths(FEBRUARY)
					   .dayOfMonthBetween(min, max)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property(tries = 2_000, maxDiscardRatio = 10)
	void betweenAndFebruaryDayOfMonth29ButNoLeapYear(
		@ForAll LocalDate minDate,
		@ForAll MonthDay monthDay,
		@ForAll @IntRange(max = 2) int offset
	) {
		Assume.that(minDate.getYear() % 4 == 1 || minDate.getYear() % 4 == 2);
		Assume.that(!monthDay.equals(MonthDay.of(FEBRUARY, 29)) || new GregorianCalendar().isLeapYear(minDate.getYear() + 1));

		LocalDate maxDate = LocalDate.of(minDate.getYear() + 1, monthDay.getMonth(), monthDay.getDayOfMonth());
		int min = 29;
		int max = min + offset;

		assertThatThrownBy(
			() -> Dates.dates()
					   .between(minDate, maxDate)
					   .onlyMonths(FEBRUARY)
					   .dayOfMonthBetween(min, max)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Provide
	Arbitrary<Set<Month>> monthWithout31Days() {
		Arbitrary<Month> months = Arbitraries.of(FEBRUARY, APRIL, JUNE, SEPTEMBER, NOVEMBER);
		return months.set();
	}

}
