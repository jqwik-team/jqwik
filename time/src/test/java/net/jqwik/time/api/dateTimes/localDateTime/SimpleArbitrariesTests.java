package net.jqwik.time.api.dateTimes.localDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
	}

	@Property
	void validLocalDateTimeIsGenerated(@ForAll("dateTimes") LocalDateTime dateTime) {
		assertThat(dateTime).isNotNull();
	}

	@Property
	void onlyFewValuesPossibleAtEndOfDayPrecisionSeconds(
		@ForAll LocalDate date,
		@ForAll @IntRange(min = 50, max = 59) int secondEnd,
		@ForAll @IntRange(max = 10) int secondStart,
		@ForAll JqwikRandom random
	) {

		Assume.that(!date.isEqual(LocalDate.MAX));
		LocalDateTime min = LocalDateTime.of(date, LocalTime.of(23, 59, secondEnd));
		LocalDateTime max = LocalDateTime.of(date.plusDays(1), LocalTime.of(0, 0, secondStart));

		LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().between(min, max);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isNotNull();
			return true;
		});

	}

	@Property
	void onlyFewValuesPossibleAtEndOfDayPrecisionNanos(
		@ForAll LocalDate date,
		@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanoEnd,
		@ForAll @IntRange(max = 200) int nanoStart,
		@ForAll JqwikRandom random
	) {

		Assume.that(!date.isEqual(LocalDate.MAX));
		LocalDateTime min = LocalDateTime.of(date, LocalTime.of(23, 59, 59, nanoEnd));
		LocalDateTime max = LocalDateTime.of(date.plusDays(1), LocalTime.of(0, 0, 0, nanoStart));

		LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().between(min, max).ofPrecision(NANOS);

		checkAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
			assertThat(dateTime).isNotNull();
			return true;
		});

	}

}
