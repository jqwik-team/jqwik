package net.jqwik.time.api.dateTimes.localDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
public class EdgeCasesTests {

	@Group
	class BetweenMethods {

		@Example
		void dateBetweenAndBetweenMethods() {
			LocalDateTimeArbitrary dateTimes =
				DateTimes.dateTimes()
						 .dateBetween(
							 LocalDate.of(2013, 5, 25),
							 LocalDate.of(2014, 11, 29)
						 )
						 .timeBetween(LocalTime.of(0, 0, 0), LocalTime.of(0, 0, 0))
						 .onlyMonths(AUGUST)
						 .dayOfMonthBetween(11, 12);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(2);

			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, 8, 11, 0, 0, 0),
				LocalDateTime.of(2014, 8, 12, 0, 0, 0)
			);
		}

		@Example
		void dateBetweenAndBetweenMethodsNearAtBeginningAndEnd() {
			LocalDateTimeArbitrary dateTimes =
				DateTimes.dateTimes()
						 .dateBetween(
							 LocalDate.of(2013, 5, 25),
							 LocalDate.of(2015, 5, 26)
						 )
						 .timeBetween(LocalTime.of(0, 0, 0), LocalTime.of(0, 0, 0))
						 .onlyMonths(MAY)
						 .dayOfMonthBetween(25, 26);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(2);

			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, 5, 25, 0, 0, 0),
				LocalDateTime.of(2015, 5, 26, 0, 0, 0)
			);
		}

	}

	@Group
	class PrecisionHours {

		@Example
		void all() {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().ofPrecision(HOURS);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(1900, 1, 1, 0, 0, 0),
				LocalDateTime.of(1900, 1, 1, 23, 0, 0),
				LocalDateTime.of(1904, 2, 29, 0, 0, 0),
				LocalDateTime.of(1904, 2, 29, 23, 0, 0),
				LocalDateTime.of(2500, 12, 31, 0, 0, 0),
				LocalDateTime.of(2500, 12, 31, 23, 0, 0)
			);
		}

		@Example
		void between() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(HOURS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 12, 0, 0),
							 LocalDateTime.of(2020, AUGUST, 23, 21, 0, 0)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 12, 0, 0),
				LocalDateTime.of(2013, MAY, 25, 23, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 0, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 23, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 0, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 21, 0, 0)
			);
		}

		@Example
		void betweenSameDate() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(HOURS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 12, 0, 0),
							 LocalDateTime.of(2013, MAY, 25, 21, 0, 0)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 12, 0, 0),
				LocalDateTime.of(2013, MAY, 25, 21, 0, 0)
			);
		}

	}

	@Group
	class PrecisionMinutes {

		@Example
		void all() {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().ofPrecision(MINUTES);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(1900, 1, 1, 0, 0, 0),
				LocalDateTime.of(1900, 1, 1, 23, 59, 0),
				LocalDateTime.of(1904, 2, 29, 0, 0, 0),
				LocalDateTime.of(1904, 2, 29, 23, 59, 0),
				LocalDateTime.of(2500, 12, 31, 0, 0, 0),
				LocalDateTime.of(2500, 12, 31, 23, 59, 0)
			);
		}

		@Example
		void between() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(MINUTES)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 24, 0),
							 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 0)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 24, 0),
				LocalDateTime.of(2013, MAY, 25, 23, 59, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 0, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 23, 59, 0),
				LocalDateTime.of(2020, AUGUST, 23, 0, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 21, 15, 0)
			);
		}

		@Example
		void betweenSameDate() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(MINUTES)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 24, 0),
							 LocalDateTime.of(2013, MAY, 25, 21, 15, 0)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 24, 0),
				LocalDateTime.of(2013, MAY, 25, 21, 15, 0)
			);
		}

	}

	@Group
	class PrecisionSeconds {

		@Example
		void all() {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().ofPrecision(SECONDS);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(1900, 1, 1, 0, 0, 0),
				LocalDateTime.of(1900, 1, 1, 23, 59, 59),
				LocalDateTime.of(1904, 2, 29, 0, 0, 0),
				LocalDateTime.of(1904, 2, 29, 23, 59, 59),
				LocalDateTime.of(2500, 12, 31, 0, 0, 0),
				LocalDateTime.of(2500, 12, 31, 23, 59, 59)
			);
		}

		@Example
		void between() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(SECONDS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 22),
							 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 22),
				LocalDateTime.of(2013, MAY, 25, 23, 59, 59),
				LocalDateTime.of(2016, FEBRUARY, 29, 0, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 23, 59, 59),
				LocalDateTime.of(2020, AUGUST, 23, 0, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19)
			);
		}

		@Example
		void betweenSameDate() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(SECONDS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 22),
							 LocalDateTime.of(2013, MAY, 25, 21, 15, 19)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 22),
				LocalDateTime.of(2013, MAY, 25, 21, 15, 19)
			);
		}

	}

	@Group
	class PrecisionMillis {

		@Example
		void all() {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().ofPrecision(MILLIS);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(1900, 1, 1, 0, 0, 0, 0),
				LocalDateTime.of(1900, 1, 1, 23, 59, 59, 999_000_000),
				LocalDateTime.of(1904, 2, 29, 0, 0, 0, 0),
				LocalDateTime.of(1904, 2, 29, 23, 59, 59, 999_000_000),
				LocalDateTime.of(2500, 12, 31, 0, 0, 0),
				LocalDateTime.of(2500, 12, 31, 23, 59, 59, 999_000_000)
			);
		}

		@Example
		void between() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(MILLIS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 302_000_000),
							 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_000_000)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 302_000_000),
				LocalDateTime.of(2013, MAY, 25, 23, 59, 59, 999_000_000),
				LocalDateTime.of(2016, FEBRUARY, 29, 0, 0, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 23, 59, 59, 999_000_000),
				LocalDateTime.of(2020, AUGUST, 23, 0, 0, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_000_000)
			);
		}

		@Example
		void betweenSameDate() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(MILLIS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 302_000_000),
							 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_000_000)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 302_000_000),
				LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_000_000)
			);
		}

	}

	@Group
	class PrecisionMicros {

		@Example
		void all() {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().ofPrecision(MICROS);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(1900, 1, 1, 0, 0, 0, 0),
				LocalDateTime.of(1900, 1, 1, 23, 59, 59, 999_999_000),
				LocalDateTime.of(1904, 2, 29, 0, 0, 0, 0),
				LocalDateTime.of(1904, 2, 29, 23, 59, 59, 999_999_000),
				LocalDateTime.of(2500, 12, 31, 0, 0, 0),
				LocalDateTime.of(2500, 12, 31, 23, 59, 59, 999_999_000)
			);
		}

		@Example
		void between() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(MICROS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_429_000),
							 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_000)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_429_000),
				LocalDateTime.of(2013, MAY, 25, 23, 59, 59, 999_999_000),
				LocalDateTime.of(2016, FEBRUARY, 29, 0, 0, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 23, 59, 59, 999_999_000),
				LocalDateTime.of(2020, AUGUST, 23, 0, 0, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_000)
			);
		}

		@Example
		void betweenSameDate() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(MICROS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_429_000),
							 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_000)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_429_000),
				LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_000)
			);
		}

	}

	@Group
	class PrecisionNanos {

		@Example
		void all() {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().ofPrecision(NANOS);
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(dateTimes.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(1900, 1, 1, 0, 0, 0, 0),
				LocalDateTime.of(1900, 1, 1, 23, 59, 59, 999_999_999),
				LocalDateTime.of(1904, 2, 29, 0, 0, 0, 0),
				LocalDateTime.of(1904, 2, 29, 23, 59, 59, 999_999_999),
				LocalDateTime.of(2500, 12, 31, 0, 0, 0),
				LocalDateTime.of(2500, 12, 31, 23, 59, 59, 999_999_999)
			);
		}

		@Example
		void between() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(NANOS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
							 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
				LocalDateTime.of(2013, MAY, 25, 23, 59, 59, 999_999_999),
				LocalDateTime.of(2016, FEBRUARY, 29, 0, 0, 0, 0),
				LocalDateTime.of(2016, FEBRUARY, 29, 23, 59, 59, 999_999_999),
				LocalDateTime.of(2020, AUGUST, 23, 0, 0, 0, 0),
				LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
			);
		}

		@Example
		void betweenSameDate() {
			LocalDateTimeArbitrary times =
				DateTimes.dateTimes()
						 .ofPrecision(NANOS)
						 .between(
							 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
							 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
						 );
			Set<LocalDateTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
				LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
			);
		}

	}

}
