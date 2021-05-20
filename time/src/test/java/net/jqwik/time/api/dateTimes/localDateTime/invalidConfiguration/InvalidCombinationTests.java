package net.jqwik.time.api.dateTimes.localDateTime.invalidConfiguration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@PropertyDefaults(tries = 5_000, shrinking = ShrinkingMode.OFF)
public class InvalidCombinationTests {

	@Provide
	Arbitrary<Integer> seconds() {
		return Arbitraries.integers().between(0, 59);
	}

	@Provide
	Arbitrary<Integer> hours() {
		return Arbitraries.integers().between(0, 23);
	}

	@Provide
	Arbitrary<Integer> minutes() {
		return Arbitraries.integers().between(0, 59);
	}

	@Property(maxDiscardRatio = 25)
	void hourMinuteSecondTimeBetween(
		@ForAll @Precision(value = NANOS) LocalTime min,
		@ForAll @Precision(value = NANOS) LocalTime max,
		@ForAll("hours") int minHour,
		@ForAll("hours") int maxHour,
		@ForAll("minutes") int minMinute,
		@ForAll("minutes") int maxMinute,
		@ForAll("seconds") int minSecond,
		@ForAll("seconds") int maxSecond
	) {

		LocalTime minFromValues = LocalTime.of(minHour, minMinute, minSecond);
		LocalTime maxFromValues = LocalTime.of(maxHour, maxMinute, maxSecond);

		Assume.that(!min.isAfter(max));
		Assume.that(minHour <= maxHour && minMinute <= maxMinute && minSecond <= maxSecond);
		Assume.that(max.isBefore(minFromValues) || min.isAfter(maxFromValues));

		assertThatThrownBy(
			() -> DateTimes.dateTimes().timeBetween(min, max)
						   .hourBetween(minHour, maxHour)
						   .minuteBetween(minMinute, maxMinute)
						   .secondBetween(minSecond, maxSecond)
						   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property(maxDiscardRatio = 25)
	void hourMinuteSecondBetweenAndBetween(
		@ForAll LocalDate date,
		@ForAll @Precision(value = NANOS) LocalTime min,
		@ForAll @Precision(value = NANOS) LocalTime max,
		@ForAll("hours") int minHour,
		@ForAll("hours") int maxHour,
		@ForAll("minutes") int minMinute,
		@ForAll("minutes") int maxMinute,
		@ForAll("seconds") int minSecond,
		@ForAll("seconds") int maxSecond
	) {

		LocalTime minFromValues = LocalTime.of(minHour, minMinute, minSecond);
		LocalTime maxFromValues = LocalTime.of(maxHour, maxMinute, maxSecond, 999_999_999);

		Assume.that(!min.isAfter(max));
		Assume.that(minHour <= maxHour && minMinute <= maxMinute && minSecond <= maxSecond);
		Assume.that(max.isBefore(minFromValues) || min.isAfter(maxFromValues));

		LocalDateTime minDateTime = LocalDateTime.of(date, min);
		LocalDateTime maxDateTime = LocalDateTime.of(date, max);

		assertThatThrownBy(
			() -> DateTimes.dateTimes()
						   .between(minDateTime, maxDateTime)
						   .hourBetween(minHour, maxHour)
						   .minuteBetween(minMinute, maxMinute)
						   .secondBetween(minSecond, maxSecond)
						   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property(maxDiscardRatio = 25)
	void timeBetweenAndBetween(
		@ForAll LocalDate date,
		@ForAll @Precision(value = NANOS) LocalTime min,
		@ForAll @Precision(value = NANOS) LocalTime max,
		@ForAll @Precision(value = NANOS) LocalTime minTime,
		@ForAll @Precision(value = NANOS) LocalTime maxTime
	) {

		Assume.that(!min.isAfter(max));
		Assume.that(!minTime.isAfter(maxTime));
		Assume.that(max.isBefore(minTime) || min.isAfter(maxTime));

		LocalDateTime minDateTime = LocalDateTime.of(date, min);
		LocalDateTime maxDateTime = LocalDateTime.of(date, max);

		assertThatThrownBy(
			() -> DateTimes.dateTimes().between(minDateTime, maxDateTime).timeBetween(minTime, maxTime).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void dateBetweenAndBetween(
		@ForAll LocalDate date,
		@ForAll @Size(min = 1) Set<Month> months,
		@ForAll @DayOfMonthRange int minDayOfMonth,
		@ForAll @DayOfMonthRange int maxDayOfMonth
	) {

		Assume.that(minDayOfMonth <= maxDayOfMonth);
		Assume.that(
			!months.contains(date.getMonth()) || date.getDayOfMonth() > maxDayOfMonth || date.getDayOfMonth() < minDayOfMonth
		);

		assertThatThrownBy(
			() -> DateTimes.dateTimes()
						   .dateBetween(date, date)
						   .onlyMonths(months.toArray(new Month[]{}))
						   .dayOfMonthBetween(minDayOfMonth, maxDayOfMonth)
						   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

}
