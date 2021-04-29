package net.jqwik.time.api.dates.date;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

public class InvalidConfigurationTests {

	@Property
	void atTheEarliestYearNotBelow1(
		@ForAll @IntRange(min = -5000, max = 0) int year,
		@ForAll Month month,
		@ForAll @DayOfMonthRange int day
	) {
		//Not existing Dates throws no exception but are changed to another existing date
		Calendar calendar = new Calendar.Builder().setDate(year, DefaultCalendarArbitrary.monthToCalendarMonth(month), day).build();
		Date date = calendar.getTime();
		assertThatThrownBy(
			() -> Dates.datesAsDate().atTheEarliest(date)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheEarliestYearTooHigh() {
		Calendar calendar = new Calendar.Builder().setDate(292_278_994, DefaultCalendarArbitrary.monthToCalendarMonth(JANUARY), 1)
												  .build();
		Date date = calendar.getTime();
		assertThatThrownBy(
			() -> Dates.datesAsDate().atTheEarliest(date)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void atTheLatestYearNotBelow1(
		@ForAll @IntRange(min = -5000, max = 0) int year,
		@ForAll Month month,
		@ForAll @DayOfMonthRange int day
	) {
		//Not existing Dates throws no exception but are changed to another existing date
		Calendar calendar = new Calendar.Builder().setDate(year, DefaultCalendarArbitrary.monthToCalendarMonth(month), day).build();
		Date date = calendar.getTime();
		assertThatThrownBy(
			() -> Dates.datesAsDate().atTheLatest(date)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void atTheLatestYearTooHigh() {
		Calendar calendar = new Calendar.Builder().setDate(292_278_994, DefaultCalendarArbitrary.monthToCalendarMonth(JANUARY), 1)
												  .build();
		Date date = calendar.getTime();
		assertThatThrownBy(
			() -> Dates.datesAsDate().atTheLatest(date)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void minYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(0, 2000)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(-1000, 2000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void minYearMustNotBeOver292278993() {
		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(292_278_994, 2000)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(Year.MAX_VALUE, 2000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxYearMustNotBeBelow1() {
		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(2000, 0)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(2000, -1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void maxYearMustNotBeOver292278993() {
		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(2000, 292_278_994)
		).isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().yearBetween(2000, Year.MAX_VALUE)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void monthsMustBeBetween1And12() {
		assertThatThrownBy(
			() -> Dates.datesAsDate().monthBetween(0, 12)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().monthBetween(12, 0)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().monthBetween(1, 13)
		).isInstanceOf(DateTimeException.class);

		assertThatThrownBy(
			() -> Dates.datesAsDate().monthBetween(13, 1)
		).isInstanceOf(DateTimeException.class);
	}

}
