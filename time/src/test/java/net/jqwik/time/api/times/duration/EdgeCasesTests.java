package net.jqwik.time.api.times.duration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
public class EdgeCasesTests {

	@Group
	class PrecisionHours {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations().ofPrecision(HOURS);
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(Long.MIN_VALUE + 8 + 30 * 60, 0),
				Duration.ZERO,
				Duration.ofSeconds(Long.MAX_VALUE - 7 - 30 * 60, 0)
			);
		}

		@Example
		void betweenPositive() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(HOURS)
											   .between(Duration.ofSeconds(4 * 60 * 60), Duration.ofSeconds(7 * 60 * 60));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(4 * 60 * 60, 0),
				Duration.ofSeconds(7 * 60 * 60, 0)
			);
		}

		@Example
		void betweenNegative() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(HOURS)
											   .between(Duration.ofSeconds(-7 * 60 * 60), Duration.ofSeconds(-4 * 60 * 60));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-7 * 60 * 60, 0),
				Duration.ofSeconds(-4 * 60 * 60, 0)
			);
		}

	}

	@Group
	class PrecisionMinutes {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations().ofPrecision(MINUTES);
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(Long.MIN_VALUE + 8, 0),
				Duration.ZERO,
				Duration.ofSeconds(Long.MAX_VALUE - 7, 0)
			);
		}

		@Example
		void betweenPositive() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(MINUTES)
											   .between(Duration.ofSeconds(3 * 60 * 60 + 8 * 60), Duration
																									  .ofSeconds(7 * 60 * 60 + 19 * 60));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(3 * 60 * 60 + 8 * 60, 0),
				Duration.ofSeconds(7 * 60 * 60 + 19 * 60, 0)
			);
		}

		@Example
		void betweenNegative() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(MINUTES)
											   .between(Duration.ofSeconds(-7 * 60 * 60 - 19 * 60), Duration
																										.ofSeconds(-3 * 60 * 60 - 8 * 60));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-7 * 60 * 60 - 19 * 60, 0),
				Duration.ofSeconds(-3 * 60 * 60 - 8 * 60, 0)
			);
		}

	}

	@Group
	class PrecisionSeconds {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations().ofPrecision(SECONDS);
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				DefaultDurationArbitrary.DEFAULT_MIN,
				Duration.ZERO,
				Duration.ofSeconds(Long.MAX_VALUE, 0)
			);
		}

		@Example
		void betweenPositive() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(SECONDS)
											   .between(Duration.ofSeconds(9402043), Duration.ofSeconds(39402042));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(9402043, 0),
				Duration.ofSeconds(39402042, 0)
			);
		}

		@Example
		void betweenNegative() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(SECONDS)
											   .between(Duration.ofSeconds(-39402042), Duration.ofSeconds(-9402043));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-9402043, 0),
				Duration.ofSeconds(-39402042, 0)
			);
		}

	}

	@Group
	class PrecisionMillis {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations().ofPrecision(MILLIS);
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				DefaultDurationArbitrary.DEFAULT_MIN,
				Duration.ZERO,
				Duration.ofSeconds(Long.MAX_VALUE, 999_000_000)
			);
		}

		@Example
		void betweenPositive() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(MILLIS)
											   .between(Duration.ofSeconds(9402042, 322_000_000), Duration
																									  .ofSeconds(39402042, 231_000_000));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(9402042, 322_000_000),
				Duration.ofSeconds(39402042, 231_000_000)
			);
		}

		@Example
		void betweenNegative() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(MILLIS)
											   .between(Duration.ofSeconds(-39402042, -231_000_000), Duration
																										 .ofSeconds(-9402042, -322_000_000));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-9402042, -322_000_000),
				Duration.ofSeconds(-39402042, -231_000_000)
			);
		}

	}

	@Group
	class PrecisionMicros {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations().ofPrecision(MICROS);
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				DefaultDurationArbitrary.DEFAULT_MIN,
				Duration.ZERO,
				Duration.ofSeconds(Long.MAX_VALUE, 999_999_000)
			);
		}

		@Example
		void betweenPositive() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(MICROS)
											   .between(Duration.ofSeconds(9402042, 321_484_000), Duration
																									  .ofSeconds(39402042, 231_493_000));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(9402042, 321_484_000),
				Duration.ofSeconds(39402042, 231_493_000)
			);
		}

		@Example
		void betweenNegative() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(MICROS)
											   .between(Duration.ofSeconds(-39402042, -231_493_000), Duration
																										 .ofSeconds(-9402042, -321_484_000));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-9402042, -321_484_000),
				Duration.ofSeconds(-39402042, -231_493_000)
			);
		}

	}

	@Group
	class PrecisionNanos {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations().ofPrecision(NANOS);
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				DefaultDurationArbitrary.DEFAULT_MIN,
				Duration.ZERO,
				DefaultDurationArbitrary.DEFAULT_MAX
			);
		}

		@Example
		void betweenPositive() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(NANOS)
											   .between(Duration.ofSeconds(9402042, 483_212), Duration.ofSeconds(39402042, 202));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(9402042, 483_212),
				Duration.ofSeconds(39402042, 202)
			);
		}

		@Example
		void betweenNegative() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(NANOS)
											   .between(Duration.ofSeconds(-39402042, 202), Duration.ofSeconds(-9402042, 483_212));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-9402042, 483_212),
				Duration.ofSeconds(-39402042, 202)
			);
		}

		@Example
		void betweenMinusOneAndOneNanosHigh() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(NANOS)
											   .between(Duration.ofSeconds(-1, -999_888_777), Duration.ofSeconds(1, 999_888_777));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-1, -999_888_777),
				Duration.ZERO,
				Duration.ofSeconds(1, 999_888_777)
			);
		}

		@Example
		void betweenMinusOneAndOneNanosLow() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(NANOS)
											   .between(Duration.ofSeconds(-1, -1), Duration.ofSeconds(1, 1));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(-1, -1),
				Duration.ZERO,
				Duration.ofSeconds(1, 1)
			);
		}

		@Example
		void betweenAroundZeroNanosLow() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(NANOS)
											   .between(Duration.ofSeconds(0, -100), Duration.ofSeconds(0, 100));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(0, -100),
				Duration.ZERO,
				Duration.ofSeconds(0, 100)
			);
		}

		@Example
		void betweenAroundZeroNanosHigh() {
			DurationArbitrary durations = Times.durations()
											   .ofPrecision(NANOS)
											   .between(Duration.ofSeconds(0, -999_888_777), Duration.ofSeconds(0, 999_888_777));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				Duration.ofSeconds(0, -999_888_777),
				Duration.ZERO,
				Duration.ofSeconds(0, 999_888_777)
			);
		}

	}

}
