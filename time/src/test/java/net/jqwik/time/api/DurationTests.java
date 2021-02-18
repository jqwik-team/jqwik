package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class DurationTests {

	@Provide
	Arbitrary<Duration> durations() {
		return Times.durations();
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validDurationIsGenerated(@ForAll("durations") Duration duration) {
			assertThat(duration).isNotNull();
		}

		@Group
		class WorstCases {

			@Property
			void worstCaseGeneration(
					@ForAll("worstCase") Duration duration,
					@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosStart,
					@ForAll @IntRange(min = 0, max = 200) int nanosEnd,
					@ForAll Random random
			) {

				Duration start = duration.withNanos(nanosStart);
				Duration end = Duration.ofSeconds(start.getSeconds() + 1, nanosEnd);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration2(
					@ForAll("worstCase") Duration duration,
					@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosStart,
					@ForAll @IntRange(min = 0, max = 200) int nanosEnd,
					@ForAll Random random
			) {

				Duration start = duration.withNanos(nanosStart);
				Duration end = Duration.ofSeconds(start.getSeconds() + 2, nanosEnd);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration3(
					@ForAll("worstCaseNegative") Duration duration,
					@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosEnd,
					@ForAll @IntRange(min = 0, max = 200) int nanosStart,
					@ForAll Random random
			) {

				Duration start = duration.withNanos(nanosEnd);
				Duration end = Duration.ofSeconds(start.getSeconds() + 1, nanosStart);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration4(
					@ForAll("worstCaseNegative") Duration duration,
					@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosEnd,
					@ForAll @IntRange(min = 0, max = 200) int nanosStart,
					@ForAll Random random
			) {

				Duration start = duration.withNanos(nanosEnd);
				Duration end = Duration.ofSeconds(start.getSeconds() + 2, nanosStart);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration5(
					@ForAll("worstCase") Duration start,
					@ForAll @IntRange(min = 0, max = 200) int nanosAdd,
					@ForAll Random random
			) {

				Assume.that(start.getNano() < 1_000_000_000 - nanosAdd);

				Duration end = Duration.ofSeconds(start.getSeconds() + 1, start.getNano() + nanosAdd);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration6(
					@ForAll("worstCase") Duration start,
					@ForAll @IntRange(min = 0, max = 200) int nanosAdd,
					@ForAll Random random
			) {

				Assume.that(start.getNano() < 1_000_000_000 - nanosAdd);

				Duration end = Duration.ofSeconds(start.getSeconds() + 2, start.getNano() + nanosAdd);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration7(
					@ForAll("worstCaseNegative") Duration start,
					@ForAll @IntRange(min = 0, max = 200) int nanosSubtract,
					@ForAll Random random
			) {

				Assume.that(start.getNano() > nanosSubtract);

				Duration end = Duration.ofSeconds(start.getSeconds() + 1, start.getNano() - nanosSubtract);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration8(
					@ForAll("worstCaseNegative") Duration start,
					@ForAll @IntRange(min = 0, max = 200) int nanosSubtract,
					@ForAll Random random
			) {

				Assume.that(start.getNano() > nanosSubtract);

				Duration end = Duration.ofSeconds(start.getSeconds() + 2, start.getNano() - nanosSubtract);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Property
			void worstCaseGeneration9(@ForAll Random random) {

				Duration start = Duration.ofSeconds(0, -101);
				Duration end = Duration.ofSeconds(0, 100);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, durationGenerated -> {
					assertThat(durationGenerated).isNotNull();
					return true;
				});

			}

			@Provide
			Arbitrary<Duration> worstCase() {
				return Times.durations().between(Duration.ZERO, Duration.ofSeconds(Long.MAX_VALUE - 2, 999_999_999));
			}

			@Provide
			Arbitrary<Duration> worstCaseNegative() {
				return Times.durations().between(DefaultDurationArbitrary.DEFAULT_MIN, Duration.ofSeconds(-2, 0));
			}

		}

	}

	@Group
	class SimpleAnnotations {

		@Property
		@Disabled("Not available.")
		void validDurationIsGenerated(@ForAll Duration duration) {
			assertThat(duration).isNotNull();
		}

	}

	@Group
	class DurationMethods {

		@Property
		void between(@ForAll("durations") Duration start, @ForAll("durations") Duration end, @ForAll Random random) {

			Assume.that(start.compareTo(end) <= 0);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration.compareTo(start)).isGreaterThanOrEqualTo(0);
				assertThat(duration.compareTo(end)).isLessThanOrEqualTo(0);
			});

		}

		@Property
		void betweenSame(@ForAll("durations") Duration durationSame, @ForAll Random random) {

			Arbitrary<Duration> durations = Times.durations().between(durationSame, durationSame);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration).isEqualTo(durationSame);
			});

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			Duration value = falsifyThenShrink(durations, random);
			assertThat(value).isEqualTo(Duration.ofSeconds(0, 0));
		}

		@Property
		@Disabled("Failing at the moment.")
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(999_392_192, 709_938_291)) < 0;
			Duration value = falsifyThenShrink(durations, random, falsifier);
			assertThat(value).isEqualTo(Duration.ofSeconds(999_392_192, 709_938_291));
		}

		@Property
		@Disabled("Failing at the moment.")
		void shrinksToSmallestFailingNegativeValue(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(-999_392_192, 709_938_291)) > 0;
			Duration value = falsifyThenShrink(durations, random, falsifier);
			assertThat(value).isEqualTo(Duration.ofSeconds(-999_392_192, 709_938_291));
		}

	}

	@Group
	class ExhaustiveGeneration {

		//TODO

	}

	@Group
	class EdgeCasesGeneration {

		//TODO

	}

	@Group
	class CheckEqualDistribution {

		//TODO

	}

}
