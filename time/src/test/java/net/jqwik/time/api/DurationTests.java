package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
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

		@Property(tries = 40)
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(999_392_192, 709_938_291)) < 0;
			Duration value = falsifyThenShrink(durations, random, falsifier);
			assertThat(value).isLessThanOrEqualTo(Duration.ofSeconds(999_392_193));
			assertThat(value).isGreaterThanOrEqualTo(Duration.ofSeconds(999_392_192, 709_938_291));
		}

		@Property(tries = 10)
		void shrinksToSmallestFailingNegativeValue(@ForAll Random random) {
			DurationArbitrary durations = Times.durations();
			TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(-999_392_192, 709_938_291)) > 0;
			Duration value = falsifyThenShrink(durations, random, falsifier);
			assertThat(value).isEqualTo(Duration.ofSeconds(-999_392_192));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
					Times.durations()
						 .between(
								 Duration.ofSeconds(183729, 999_999_998),
								 Duration.ofSeconds(183730, 1)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					Duration.ofSeconds(183729, 999_999_998),
					Duration.ofSeconds(183729, 999_999_999),
					Duration.ofSeconds(183730, 0),
					Duration.ofSeconds(183730, 1)
			);
		}

		@Example
		void between2() {
			Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
					Times.durations()
						 .between(
								 Duration.ofSeconds(-183730, 999_999_998),
								 Duration.ofSeconds(-183729, 1)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					Duration.ofSeconds(-183730, 999_999_998),
					Duration.ofSeconds(-183730, 999_999_999),
					Duration.ofSeconds(-183729, 0),
					Duration.ofSeconds(-183729, 1)
			);
		}

		@Example
		void between3() {
			Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
					Times.durations()
						 .between(
								 Duration.ofSeconds(-1, -1),
								 Duration.ofSeconds(0, -999_999_998)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					Duration.ofSeconds(-1, -1),
					Duration.ofSeconds(-1, 0),
					Duration.ofSeconds(0, -999_999_999),
					Duration.ofSeconds(0, -999_999_998)
			);
		}

		@Example
		void between4() {
			Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
					Times.durations()
						 .between(
								 Duration.ofSeconds(0, -2),
								 Duration.ofSeconds(0, 1)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					Duration.ofSeconds(0, -2),
					Duration.ofSeconds(0, -1),
					Duration.ZERO,
					Duration.ofSeconds(0, 1)
			);
		}

		@Example
		void between5() {
			Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
					Times.durations()
						 .between(
								 Duration.ofSeconds(0, 999_999_998),
								 Duration.ofSeconds(1, 1)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					Duration.ofSeconds(0, 999_999_998),
					Duration.ofSeconds(0, 999_999_999),
					Duration.ofSeconds(1, 0),
					Duration.ofSeconds(1, 1)
			);
		}

	}

	@Group
	class EdgeCasesGeneration {

		@Example
		void all() {
			DurationArbitrary durations = Times.durations();
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					DefaultDurationArbitrary.DEFAULT_MIN,
					Duration.ZERO,
					DefaultDurationArbitrary.DEFAULT_MAX
			);
		}

		@Example
		void between() {
			DurationArbitrary durations = Times.durations()
											   .between(Duration.ofSeconds(9402042, 483_212), Duration.ofSeconds(39402042, 202));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Duration.ofSeconds(9402042, 483_212),
					Duration.ofSeconds(39402042, 202)
			);
		}

		@Example
		void between2() {
			DurationArbitrary durations = Times.durations()
											   .between(Duration.ofSeconds(-39402042, 202), Duration.ofSeconds(-9402042, 483_212));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Duration.ofSeconds(-9402042, 483_212),
					Duration.ofSeconds(-39402042, 202)
			);
		}

		@Example
		void between3() {
			DurationArbitrary durations = Times.durations()
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
		void between4() {
			DurationArbitrary durations = Times.durations().between(Duration.ofSeconds(-1, -1), Duration.ofSeconds(1, 1));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Duration.ofSeconds(-1, -1),
					Duration.ZERO,
					Duration.ofSeconds(1, 1)
			);
		}

		@Example
		void between5() {
			DurationArbitrary durations = Times.durations().between(Duration.ofSeconds(0, -100), Duration.ofSeconds(0, 100));
			Set<Duration> edgeCases = collectEdgeCaseValues(durations.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Duration.ofSeconds(0, -100),
					Duration.ZERO,
					Duration.ofSeconds(0, 100)
			);
		}

		@Example
		void between6() {
			DurationArbitrary durations = Times.durations()
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

	@Group
	class CheckEqualDistribution {

		@Property
		void negativeAndPositive0ValuesArePossible(@ForAll("durationsNear0_2") Duration duration) {
			Assume.that(!duration.isZero());
			Statistics.label("Seconds")
					  .collect(duration.isNegative())
					  .coverage(this::check5050BooleanCoverage);
		}

		@Property
		void seconds(@ForAll("durationsNear0") Duration duration) {
			Statistics.label("Seconds")
					  .collect(duration.getSeconds())
					  .coverage(this::checkSecondsCoverage);
		}

		@Property
		void milliseconds(@ForAll("durations") Duration duration) {

			Statistics.label("Milliseconds x--")
					  .collect(duration.getNano() / 100_000_000)
					  .coverage(this::check10Coverage);

			Statistics.label("Milliseconds -x-")
					  .collect((duration.getNano() / 10_000_000) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Milliseconds --x")
					  .collect((duration.getNano() / 1_000_000) % 10)
					  .coverage(this::check10Coverage);

		}

		@Property
		void microseconds(@ForAll("durations") Duration duration) {

			Statistics.label("Microseconds x--")
					  .collect((duration.getNano() % 1_000_000) / 100_000)
					  .coverage(this::check10Coverage);

			Statistics.label("Microseconds -x-")
					  .collect(((duration.getNano() % 1_000_000) / 10_000) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Microseconds --x")
					  .collect(((duration.getNano() % 1_000_000) / 1_000) % 10)
					  .coverage(this::check10Coverage);

		}

		@Property
		void nanoseconds(@ForAll("durations") Duration duration) {

			Statistics.label("Nanoseconds x--")
					  .collect((duration.getNano() % 1_000) / 100)
					  .coverage(this::check10Coverage);

			Statistics.label("Nanoseconds -x-")
					  .collect(((duration.getNano() % 1_000) / 10) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Nanoseconds --x")
					  .collect((duration.getNano() % 1_000) % 10)
					  .coverage(this::check10Coverage);

		}

		private void check10Coverage(StatisticsCoverage coverage) {
			for (int value = 0; value < 10; value++) {
				coverage.check(value).percentage(p -> p >= 5);
			}
		}

		private void checkSecondsCoverage(StatisticsCoverage coverage) {
			for (long value = -10; value <= 10; value++) {
				coverage.check(value).percentage(p -> p >= 2.0);
			}
		}

		private void check5050BooleanCoverage(StatisticsCoverage coverage) {
			coverage.check(true).percentage(p -> p >= 35);
			coverage.check(false).percentage(p -> p >= 35);
		}

		@Provide
		Arbitrary<Duration> durationsNear0() {
			return Times.durations().between(Duration.ofSeconds(-10, 0), Duration.ofSeconds(10, 999_999_999));
		}

		@Provide
		Arbitrary<Duration> durationsNear0_2() {
			return Times.durations().between(Duration.ofSeconds(0, -999_999_999), Duration.ofSeconds(0, 999_999_999));
		}

	}

}
