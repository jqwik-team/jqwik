package net.jqwik.time.api.times.offsetTime;

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
	class DefaultEdgeCases {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes();
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.ofHours(-12)),
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.ofHours(14)),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.ofHours(-12)),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.ofHours(14))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times = Times.offsetTimes().between(LocalTime.of(11, 12, 13), LocalTime.of(12, 13, 14));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 12, 13, 0), ZoneOffset.ofHours(-12)),
				OffsetTime.of(LocalTime.of(11, 12, 13, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(11, 12, 13, 0), ZoneOffset.ofHours(14)),
				OffsetTime.of(LocalTime.of(12, 13, 14, 0), ZoneOffset.ofHours(-12)),
				OffsetTime.of(LocalTime.of(12, 13, 14, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 13, 14, 0), ZoneOffset.ofHours(14))
			);
		}

		@Example
		void betweenOffsets() {
			OffsetTimeArbitrary times = Times.offsetTimes().offsetBetween(ZoneOffset.ofHoursMinutesSeconds(-9, -3, -11), ZoneOffset
																															 .ofHoursMinutesSeconds(4, 11, 12));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(6);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.ofHoursMinutes(-9, 0)),
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.ofHours(4)),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.ofHoursMinutes(-9, 0)),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.ofHours(4))
			);
		}

	}

	@Group
	class PrecisionHours {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes().ofPrecision(HOURS)
											 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 0, 0, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(HOURS)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789))
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(12, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(21, 0, 0, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenHour() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(HOURS)
					 .hourBetween(11, 12)
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 0, 0, 0), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class PrecisionMinutes {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes().ofPrecision(MINUTES)
											 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 0, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(MINUTES)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789))
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 24, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(21, 15, 0, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenMinute() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(MINUTES)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 31, 0, 0), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class PrecisionSeconds {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes().ofPrecision(SECONDS)
											 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 59, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(SECONDS)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789))
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 22, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(21, 15, 19, 0), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenSecond() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(SECONDS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10)
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 5, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 31, 10, 0), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class PrecisionMillis {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes().ofPrecision(MILLIS)
											 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 59, 999_000_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(MILLIS)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789))
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 21, 302_000_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(21, 15, 19, 199_000_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenSecond() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(MILLIS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10)
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 5, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 31, 10, 999_000_000), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class PrecisionMicros {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes().ofPrecision(MICROS)
											 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 59, 999_999_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(MICROS)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789))
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 21, 301_429_000), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(21, 15, 19, 199_321_000), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenSecond() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(MICROS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10)
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 5, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 31, 10, 999_999_000), ZoneOffset.of("Z"))
			);
		}

	}

	@Group
	class PrecisionNanos {

		@Example
		void all() {
			OffsetTimeArbitrary times = Times.offsetTimes().ofPrecision(NANOS)
											 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(0, 0, 0, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(23, 59, 59, 999_999_999), ZoneOffset.of("Z"))
			);
		}

		@Example
		void between() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(NANOS)
					 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789))
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 21, 301_428_111), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(21, 15, 19, 199_321_789), ZoneOffset.of("Z"))
			);
		}

		@Example
		void betweenSecond() {
			OffsetTimeArbitrary times =
				Times.offsetTimes()
					 .ofPrecision(NANOS)
					 .hourBetween(11, 12)
					 .minuteBetween(23, 31)
					 .secondBetween(5, 10)
					 .offsetBetween(ZoneOffset.of("Z"), ZoneOffset.of("Z"));
			Set<OffsetTime> edgeCases = collectEdgeCaseValues(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				OffsetTime.of(LocalTime.of(11, 23, 5, 0), ZoneOffset.of("Z")),
				OffsetTime.of(LocalTime.of(12, 31, 10, 999_999_999), ZoneOffset.of("Z"))
			);
		}

	}

}
