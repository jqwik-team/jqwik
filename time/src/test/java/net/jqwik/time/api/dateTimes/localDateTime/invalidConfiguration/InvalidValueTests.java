package net.jqwik.time.api.dateTimes.localDateTime.invalidConfiguration;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

public class InvalidValueTests {

	@Property
	void atTheEarliestYearMustNotBelow1(
		@ForAll @IntRange(min = Year.MIN_VALUE, max = 0) int year,
		@ForAll @YearRange(min = 2001, max = 2001) LocalDate date,
		@ForAll LocalTime time
	) {
		date = date.withYear(year);
		LocalDateTime dateTime = LocalDateTime.of(date, time);
		assertThatThrownBy(
			() -> DateTimes.dateTimes().atTheEarliest(dateTime)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void atTheLatestYearMustNotBelow1(
		@ForAll @IntRange(min = Year.MIN_VALUE, max = 0) int year,
		@ForAll @YearRange(min = 2001, max = 2001) LocalDate date,
		@ForAll LocalTime time
	) {
		date = date.withYear(year);
		LocalDateTime dateTime = LocalDateTime.of(date, time);
		assertThatThrownBy(
			() -> DateTimes.dateTimes().atTheLatest(dateTime)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void dateBetweenYearMustNotBelow1(
		@ForAll @IntRange(min = Year.MIN_VALUE, max = 0) int year,
		@ForAll @YearRange(min = 2001, max = 2001) LocalDate date
	) {
		LocalDate effective = date.withYear(year);
		assertThatThrownBy(
			() -> DateTimes.dateTimes().dateBetween(effective, effective)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void yearBetweenYearMustNotBelow1(@ForAll @IntRange(min = Year.MIN_VALUE, max = 0) int year) {
		assertThatThrownBy(
			() -> DateTimes.dateTimes().yearBetween(year, year)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void minMaxSecond(@ForAll int minSecond, @ForAll int maxSecond) {

		Assume.that(minSecond < 0 || minSecond > 59 || maxSecond < 0 || maxSecond > 59);

		assertThatThrownBy(
			() -> DateTimes.dateTimes().secondBetween(minSecond, maxSecond)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void minMaxMinute(@ForAll int minMinute, @ForAll int maxMinute) {

		Assume.that(minMinute < 0 || minMinute > 59 || maxMinute < 0 || maxMinute > 59);

		assertThatThrownBy(
			() -> DateTimes.dateTimes().minuteBetween(minMinute, maxMinute)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void minMaxHour(@ForAll int minHour, @ForAll int maxHour) {

		Assume.that(minHour < 0 || minHour > 23 || maxHour < 0 || maxHour > 23);

		assertThatThrownBy(
			() -> DateTimes.dateTimes().hourBetween(minHour, maxHour)
		).isInstanceOf(IllegalArgumentException.class);

	}

}
