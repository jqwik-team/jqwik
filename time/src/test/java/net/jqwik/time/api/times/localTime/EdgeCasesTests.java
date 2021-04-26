package net.jqwik.time.api.times.localTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
public class EdgeCasesTests {

	@Group
	class PrecisionHours {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times().ofPrecision(HOURS);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(0, 0, 0, 0),
				LocalTime.of(23, 0, 0, 0)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(HOURS)
					 .between(LocalTime.of(12, 0, 0), LocalTime.of(21, 0, 0));
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(12, 0, 0, 0),
				LocalTime.of(21, 0, 0, 0)
			);
		}

		@Example
		void betweenHour() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(HOURS)
					 .hourBetween(11, 12);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 0, 0, 0),
				LocalTime.of(12, 0, 0, 0)
			);
		}

	}

	@Group
	class PrecisionMinutes {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times().ofPrecision(MINUTES);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(0, 0, 0, 0),
				LocalTime.of(23, 59, 0, 0)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(MINUTES)
					 .between(LocalTime.of(11, 24, 0), LocalTime.of(21, 15, 0));
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 24, 0, 0),
				LocalTime.of(21, 15, 0, 0)
			);
		}

		@Example
		void betweenMinute() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(MINUTES)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 0, 0),
				LocalTime.of(12, 31, 0, 0)
			);
		}

	}

	@Group
	class PrecisionSeconds {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times().ofPrecision(SECONDS);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(0, 0, 0, 0),
				LocalTime.of(23, 59, 59, 0)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(SECONDS)
					 .between(LocalTime.of(11, 23, 22), LocalTime.of(21, 15, 19));
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 22, 0),
				LocalTime.of(21, 15, 19, 0)
			);
		}

		@Example
		void betweenSecond() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(SECONDS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 5, 0),
				LocalTime.of(12, 31, 10, 0)
			);
		}

	}

	@Group
	class PrecisionMillis {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times().ofPrecision(MILLIS);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(0, 0, 0, 0),
				LocalTime.of(23, 59, 59, 999_000_000)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(MILLIS)
					 .between(LocalTime.of(11, 23, 21, 302_000_000), LocalTime.of(21, 15, 19, 199_000_000));
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 21, 302_000_000),
				LocalTime.of(21, 15, 19, 199_000_000)
			);
		}

		@Example
		void betweenSecond() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(MILLIS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 5, 0),
				LocalTime.of(12, 31, 10, 999_000_000)
			);
		}

	}

	@Group
	class PrecisionMicros {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times().ofPrecision(MICROS);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(0, 0, 0, 0),
				LocalTime.of(23, 59, 59, 999_999_000)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(MICROS)
					 .between(LocalTime.of(11, 23, 21, 301_429_000), LocalTime.of(21, 15, 19, 199_321_000));
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 21, 301_429_000),
				LocalTime.of(21, 15, 19, 199_321_000)
			);
		}

		@Example
		void betweenSecond() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(MICROS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 5, 0),
				LocalTime.of(12, 31, 10, 999_999_000)
			);
		}

	}

	@Group
	class PrecisionNanos {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times().ofPrecision(NANOS);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(0, 0, 0, 0),
				LocalTime.of(23, 59, 59, 999_999_999)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(NANOS)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 21, 301_428_111),
				LocalTime.of(21, 15, 19, 199_321_789)
			);
		}

		@Example
		void betweenSecond() {
			LocalTimeArbitrary times =
				Times.times()
					 .ofPrecision(NANOS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10);
			Set<LocalTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalTime.of(11, 23, 5, 0),
				LocalTime.of(12, 31, 10, 999_999_999)
			);
		}

	}

}
