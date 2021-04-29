package net.jqwik.time.api.dates.calendar;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

public class InvalidConfigurationTests {

	@Property
	void atTheEarliestYearMustNotBeBelow1(
		@ForAll @IntRange(min = -100_000, max = 0) int year,
		@ForAll Month month,
		@ForAll @DayOfMonthRange int day
	) {
		Calendar calendar = new Calendar.Builder().setDate(year, DefaultCalendarArbitrary.monthToCalendarMonth(month), day).build();
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().atTheEarliest(calendar)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheEarliestYearTooHigh() {
		Calendar calendar = new Calendar.Builder().setDate(292_278_994, 1, 1).build();
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().atTheEarliest(calendar)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void atTheLatestYearMustNotBeBelow1(
		@ForAll @IntRange(min = -100_000, max = 0) int year,
		@ForAll Month month,
		@ForAll @DayOfMonthRange int day
	) {
		Calendar calendar = new Calendar.Builder().setDate(year, DefaultCalendarArbitrary.monthToCalendarMonth(month), day).build();
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().atTheLatest(calendar)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheLatestYearTooHigh() {
		Calendar calendar = new Calendar.Builder().setDate(292_278_994, 1, 1).build();
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().atTheLatest(calendar)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void minYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(0, 2000)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(-1000, 2000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void minYearMustNotBeOver292278993() {
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(292_278_994, 2000)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(Year.MAX_VALUE, 2000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(2000, 0)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(2000, -1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxYearMustNotBeOver292278993() {
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(2000, 292_278_994)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().yearBetween(2000, Year.MAX_VALUE)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void monthsMustBeBetween1And12() {
		assertThatThrownBy(
			() -> Dates.datesAsCalendar().monthBetween(0, 12)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().monthBetween(12, 0)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().monthBetween(1, 13)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.datesAsCalendar().monthBetween(13, 1)
		).isInstanceOf(DateTimeException.class);
	}

}
