package net.jqwik.time.api;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class DurationTests {

	@Provide
	Arbitrary<Duration> durations() {
		return Times.durations();
	}

	@Provide
	Arbitrary<Duration> precisionHours() {
		return Times.durations().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<Duration> precisionMinutes() {
		return Times.durations().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<Duration> precisionSeconds() {
		return Times.durations().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<Duration> precisionMilliseconds() {
		return Times.durations().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<Duration> precisionMicroseconds() {
		return Times.durations().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<Duration> precisionNanoseconds() {
		return Times.durations().ofPrecision(NANOS);
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
			void positiveOneSecondDifferenceNanosHigh(
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
			void positiveTwoSecondDifferenceNanosHigh(
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
			void negativeOneSecondDifferenceNanosHigh(
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
			void negativeTwoSecondDifferenceNanosHigh(
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
			void positiveOneSecondDifferenceNanosLow(
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
			void positiveTwoSecondDifferenceNanosLow(
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
			void negativeOneSecondDifferenceNanosLow(
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
			void negativeTwoSecondsDifferenceNanosLow(
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
			void aroundZero(@ForAll Random random) {

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
				return Times.durations().ofPrecision(NANOS).between(DefaultDurationArbitrary.DEFAULT_MIN, Duration.ofSeconds(-2, 0));
			}

		}

	}

	@Group
	class DefaultGeneration {

		@Property
		void validDurationIsGenerated(@ForAll Duration duration) {
			assertThat(duration).isNotNull();
		}

	}

	@Group
	class CheckDurationMethods {

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
			void betweenEndDurationAfterStartDuration(
					@ForAll("durations") Duration start,
					@ForAll("durations") Duration end,
					@ForAll Random random
			) {

				Assume.that(start.compareTo(end) > 0);

				Arbitrary<Duration> durations = Times.durations().between(start, end);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.compareTo(end)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(start)).isLessThanOrEqualTo(0);
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
		class PrecisionMethods {

			@Group
			class Hours {

				@Property
				void precision(@ForAll("precisionHours") Duration duration) {
					assertThat(getMinute(duration)).isEqualTo(0);
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
				}

				@Property
				void precisionBetween(
						@ForAll("durations") Duration startDuration,
						@ForAll("durations") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7 - 30 * 60);
					Assume.that(getHour(startDuration) != getHour(endDuration));

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(HOURS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(getMinute(duration)).isEqualTo(0);
						assertThat(getSecond(duration)).isEqualTo(0);
						assertThat(duration.getNano()).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

				@Property
				void precisionBetweenWithPrecisionMinutes(
						@ForAll("precisionMinutes") Duration startDuration,
						@ForAll("precisionMinutes") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7 - 30 * 60);
					Assume.that(getHour(startDuration) != getHour(endDuration));

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(HOURS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(getMinute(duration)).isEqualTo(0);
						assertThat(getSecond(duration)).isEqualTo(0);
						assertThat(duration.getNano()).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

			}

			@Group
			class Minutes {

				@Property
				void precision(@ForAll("precisionMinutes") Duration duration) {
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
				}

				@Property
				void precisionBetween(
						@ForAll("durations") Duration startDuration,
						@ForAll("durations") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7);
					Assume.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration));

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(getSecond(duration)).isEqualTo(0);
						assertThat(duration.getNano()).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

				@Property
				void precisionBetweenWithPrecisionSeconds(
						@ForAll("precisionSeconds") Duration startDuration,
						@ForAll("precisionSeconds") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7);
					Assume.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration));

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(getSecond(duration)).isEqualTo(0);
						assertThat(duration.getNano()).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

			}

			@Group
			class Seconds {

				@Property
				void precision(@ForAll("precisionSeconds") Duration duration) {
					assertThat(duration.getNano()).isEqualTo(0);
				}

				@Property
				void precisionBetween(
						@ForAll("durations") Duration startDuration,
						@ForAll("durations") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration) || getSecond(startDuration) != getSecond(endDuration));

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(duration.getNano()).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

				@Property
				void precisionBetweenWithPrecisionMillis(
						@ForAll("precisionMilliseconds") Duration startDuration,
						@ForAll("precisionMilliseconds") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration) || getSecond(startDuration) != getSecond(endDuration));

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(duration.getNano()).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

			}

			@Group
			class Millis {

				@Property
				void precision(@ForAll("precisionMilliseconds") Duration duration) {
					assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
				}

				@Property
				void precisionBetween(
						@ForAll("durations") Duration startDuration,
						@ForAll("durations") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(getHour(startDuration) != getHour(endDuration)
										|| getMinute(startDuration) != getMinute(endDuration)
										|| getSecond(startDuration) != getSecond(endDuration)
										|| startDuration.getNano() / 1_000_000 != endDuration.getNano() / 1_000_000);

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

				@Property
				void precisionBetweenWithPrecisionMicros(
						@ForAll("precisionMicroseconds") Duration startDuration,
						@ForAll("precisionMicroseconds") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(getHour(startDuration) != getHour(endDuration)
										|| getMinute(startDuration) != getMinute(endDuration)
										|| getSecond(startDuration) != getSecond(endDuration)
										|| startDuration.getNano() / 1_000_000 != endDuration.getNano() / 1_000_000);

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

			}

			@Group
			class Micros {

				@Property
				void precision(@ForAll("precisionMicroseconds") Duration duration) {
					assertThat(duration.getNano() % 1_000).isEqualTo(0);
				}

				@Property
				void precisionBetween(
						@ForAll("durations") Duration startDuration,
						@ForAll("durations") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(getHour(startDuration) != getHour(endDuration)
										|| getMinute(startDuration) != getMinute(endDuration)
										|| getSecond(startDuration) != getSecond(endDuration)
										|| startDuration.getNano() / 1_000 != endDuration.getNano() / 1_000);

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MICROS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(duration.getNano() % 1_000).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

				@Property
				void precisionBetweenWithPrecisionNanos(
						@ForAll("precisionNanoseconds") Duration startDuration,
						@ForAll("precisionNanoseconds") Duration endDuration,
						@ForAll Random random
				) {

					Assume.that(startDuration.compareTo(endDuration) <= 0);
					Assume.that(getHour(startDuration) != getHour(endDuration)
										|| getMinute(startDuration) != getMinute(endDuration)
										|| getSecond(startDuration) != getSecond(endDuration)
										|| startDuration.getNano() / 1_000 != endDuration.getNano() / 1_000);

					Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MICROS);

					assertAllGenerated(durations.generator(1000), random, duration -> {
						assertThat(duration.getNano() % 1_000).isEqualTo(0);
						assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
						assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
						return true;
					});

				}

			}

			@Group
			class Nanos {

				@Property
				void precision(@ForAll("precisionNanoseconds") Duration duration) {
					assertThat(duration).isNotNull();
				}

			}

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
			assertThat(value).isEqualTo(Duration.ofSeconds(999_392_193));
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

		@Group
		class PrecisionHours {

			@Example
			void betweenPositive() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(HOURS)
							 .between(
									 Duration.ofSeconds(7 * 60 * 60 + 4 * 60 + 55, 997_997_921),
									 Duration.ofSeconds(11 * 60 * 60 + 8 * 60 + 31, 1_213_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(8 * 60 * 60, 0),
						Duration.ofSeconds(9 * 60 * 60, 0),
						Duration.ofSeconds(10 * 60 * 60, 0),
						Duration.ofSeconds(11 * 60 * 60, 0)
				);
			}

			@Example
			void betweenNegative() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(HOURS)
							 .between(
									 Duration.ofSeconds(-11 * 60 * 60 - 8 * 60 - 11, 997_123_998),
									 Duration.ofSeconds(-7 * 60 * 60 - 4 * 60 - 55, 1_999_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-11 * 60 * 60, 0),
						Duration.ofSeconds(-10 * 60 * 60, 0),
						Duration.ofSeconds(-9 * 60 * 60, 0),
						Duration.ofSeconds(-8 * 60 * 60, 0)
				);
			}

			@Example
			void betweenAroundZero() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(HOURS)
							 .between(
									 Duration.ofSeconds(-2 * 60 * 60 - 2 * 60 - 33, -2_321_392),
									 Duration.ofSeconds(60 * 60 + 60 + 28, 1_392_392)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-2 * 60 * 60, 0),
						Duration.ofSeconds(-60 * 60, 0),
						Duration.ZERO,
						Duration.ofSeconds(60 * 60, 0)
				);
			}

		}

		@Group
		class PrecisionMinutes {

			@Example
			void betweenPositive() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MINUTES)
							 .between(
									 Duration.ofSeconds(4 * 60 + 55, 997_997_921),
									 Duration.ofSeconds(8 * 60 + 31, 1_213_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(5 * 60, 0),
						Duration.ofSeconds(6 * 60, 0),
						Duration.ofSeconds(7 * 60, 0),
						Duration.ofSeconds(8 * 60, 0)
				);
			}

			@Example
			void betweenNegative() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MINUTES)
							 .between(
									 Duration.ofSeconds(-8 * 60 - 11, 997_123_998),
									 Duration.ofSeconds(-4 * 60 - 55, 1_999_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-8 * 60, 0),
						Duration.ofSeconds(-7 * 60, 0),
						Duration.ofSeconds(-6 * 60, 0),
						Duration.ofSeconds(-5 * 60, 0)
				);
			}

			@Example
			void betweenAroundZero() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MINUTES)
							 .between(
									 Duration.ofSeconds(-2 * 60 - 33, -2_321_392),
									 Duration.ofSeconds(60 + 28, 1_392_392)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-2 * 60, 0),
						Duration.ofSeconds(-60, 0),
						Duration.ZERO,
						Duration.ofSeconds(60, 0)
				);
			}

		}

		@Group
		class PrecisionSeconds {

			@Example
			void betweenPositive() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(SECONDS)
							 .between(
									 Duration.ofSeconds(183726, 997_997_921),
									 Duration.ofSeconds(183730, 1_213_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(183727, 0),
						Duration.ofSeconds(183728, 0),
						Duration.ofSeconds(183729, 0),
						Duration.ofSeconds(183730, 0)
				);
			}

			@Example
			void betweenNegative() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(SECONDS)
							 .between(
									 Duration.ofSeconds(-183730, 997_123_998),
									 Duration.ofSeconds(-183726, 1_999_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-183729, 0),
						Duration.ofSeconds(-183728, 0),
						Duration.ofSeconds(-183727, 0),
						Duration.ofSeconds(-183726, 0)
				);
			}

			@Example
			void betweenAroundZero() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(SECONDS)
							 .between(
									 Duration.ofSeconds(-2, -2_321_392),
									 Duration.ofSeconds(1, 1_392_392)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-2, 0),
						Duration.ofSeconds(-1, 0),
						Duration.ZERO,
						Duration.ofSeconds(1, 0)
				);
			}

		}

		@Group
		class PrecisionMillis {

			@Example
			void betweenPositive() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MILLIS)
							 .between(
									 Duration.ofSeconds(183729, 997_997_921),
									 Duration.ofSeconds(183730, 1_213_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(183729, 998_000_000),
						Duration.ofSeconds(183729, 999_000_000),
						Duration.ofSeconds(183730, 0),
						Duration.ofSeconds(183730, 1_000_000)
				);
			}

			@Example
			void betweenNegative() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MILLIS)
							 .between(
									 Duration.ofSeconds(-183730, 997_123_998),
									 Duration.ofSeconds(-183729, 1_999_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-183730, 998_000_000),
						Duration.ofSeconds(-183730, 999_000_000),
						Duration.ofSeconds(-183729, 0),
						Duration.ofSeconds(-183729, 1_000_000)
				);
			}

			@Example
			void betweenNegativeOneSecond() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MILLIS)
							 .between(
									 Duration.ofSeconds(-1, -1_999_999),
									 Duration.ofSeconds(0, -997_382_492)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-1, -1_000_000),
						Duration.ofSeconds(-1, 0),
						Duration.ofSeconds(0, -999_000_000),
						Duration.ofSeconds(0, -998_000_000)
				);
			}

			@Example
			void betweenAroundZero() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MILLIS)
							 .between(
									 Duration.ofSeconds(0, -2_321_392),
									 Duration.ofSeconds(0, 1_392_392)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(0, -2_000_000),
						Duration.ofSeconds(0, -1_000_000),
						Duration.ZERO,
						Duration.ofSeconds(0, 1_000_000)
				);
			}

			@Example
			void betweenPositiveOneSecond() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MILLIS)
							 .between(
									 Duration.ofSeconds(0, 997_128_492),
									 Duration.ofSeconds(1, 1_039_392)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(0, 998_000_000),
						Duration.ofSeconds(0, 999_000_000),
						Duration.ofSeconds(1, 0),
						Duration.ofSeconds(1, 1_000_000)
				);
			}

		}

		@Group
		class PrecisionMicros {

			@Example
			void betweenPositive() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MICROS)
							 .between(
									 Duration.ofSeconds(183729, 999_997_921),
									 Duration.ofSeconds(183730, 1_213)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(183729, 999_998_000),
						Duration.ofSeconds(183729, 999_999_000),
						Duration.ofSeconds(183730, 0),
						Duration.ofSeconds(183730, 1_000)
				);
			}

			@Example
			void betweenNegative() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MICROS)
							 .between(
									 Duration.ofSeconds(-183730, 999_997_998),
									 Duration.ofSeconds(-183729, 1_999)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-183730, 999_998_000),
						Duration.ofSeconds(-183730, 999_999_000),
						Duration.ofSeconds(-183729, 0),
						Duration.ofSeconds(-183729, 1_000)
				);
			}

			@Example
			void betweenNegativeOneSecond() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MICROS)
							 .between(
									 Duration.ofSeconds(-1, -1_302),
									 Duration.ofSeconds(0, -999_997_323)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-1, -1_000),
						Duration.ofSeconds(-1, 0),
						Duration.ofSeconds(0, -999_999_000),
						Duration.ofSeconds(0, -999_998_000)
				);
			}

			@Example
			void betweenAroundZero() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MICROS)
							 .between(
									 Duration.ofSeconds(0, -2_321),
									 Duration.ofSeconds(0, 1_392)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(0, -2_000),
						Duration.ofSeconds(0, -1_000),
						Duration.ZERO,
						Duration.ofSeconds(0, 1_000)
				);
			}

			@Example
			void betweenPositiveOneSecond() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(MICROS)
							 .between(
									 Duration.ofSeconds(0, 999_997_213),
									 Duration.ofSeconds(1, 1_023)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(0, 999_998_000),
						Duration.ofSeconds(0, 999_999_000),
						Duration.ofSeconds(1, 0),
						Duration.ofSeconds(1, 1_000)
				);
			}

		}

		@Group
		class PrecisionNanos {

			@Example
			void betweenPositive() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(NANOS)
							 .between(
									 Duration.ofSeconds(183729, 999_999_998),
									 Duration.ofSeconds(183730, 1)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(183729, 999_999_998),
						Duration.ofSeconds(183729, 999_999_999),
						Duration.ofSeconds(183730, 0),
						Duration.ofSeconds(183730, 1)
				);
			}

			@Example
			void betweenNegative() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(NANOS)
							 .between(
									 Duration.ofSeconds(-183730, 999_999_998),
									 Duration.ofSeconds(-183729, 1)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-183730, 999_999_998),
						Duration.ofSeconds(-183730, 999_999_999),
						Duration.ofSeconds(-183729, 0),
						Duration.ofSeconds(-183729, 1)
				);
			}

			@Example
			void betweenNegativeOneSecond() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(NANOS)
							 .between(
									 Duration.ofSeconds(-1, -1),
									 Duration.ofSeconds(0, -999_999_998)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(-1, -1),
						Duration.ofSeconds(-1, 0),
						Duration.ofSeconds(0, -999_999_999),
						Duration.ofSeconds(0, -999_999_998)
				);
			}

			@Example
			void betweenAroundZero() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(NANOS)
							 .between(
									 Duration.ofSeconds(0, -2),
									 Duration.ofSeconds(0, 1)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(0, -2),
						Duration.ofSeconds(0, -1),
						Duration.ZERO,
						Duration.ofSeconds(0, 1)
				);
			}

			@Example
			void betweenPositiveOneSecond() {
				Optional<ExhaustiveGenerator<Duration>> optionalGenerator =
						Times.durations()
							 .ofPrecision(NANOS)
							 .between(
									 Duration.ofSeconds(0, 999_999_998),
									 Duration.ofSeconds(1, 1)
							 )
							 .exhaustive();
				assertThat(optionalGenerator).isPresent();

				ExhaustiveGenerator<Duration> generator = optionalGenerator.get();
				assertThat(generator.maxCount()).isEqualTo(4);
				assertThat(generator).containsExactly(
						Duration.ofSeconds(0, 999_999_998),
						Duration.ofSeconds(0, 999_999_999),
						Duration.ofSeconds(1, 0),
						Duration.ofSeconds(1, 1)
				);
			}

		}

	}

	@Group
	class EdgeCasesTests {

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
												   .between(Duration.ofSeconds(3 * 60 * 60 + 7 * 60 + 43, 321_483_212), Duration.ofSeconds(7 * 60 * 60 + 19 * 60 + 12, 231_493_202));
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
												   .between(Duration.ofSeconds(-7 * 60 * 60 - 19 * 60 - 12, -231_493_202), Duration.ofSeconds(-3 * 60 * 60 - 7 * 60 - 43, -321_483_212));
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
												   .between(Duration.ofSeconds(3 * 60 * 60 + 7 * 60 + 43, 321_483_212), Duration.ofSeconds(7 * 60 * 60 + 19 * 60 + 12, 231_493_202));
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
												   .between(Duration.ofSeconds(-7 * 60 * 60 - 19 * 60 - 12, -231_493_202), Duration.ofSeconds(-3 * 60 * 60 - 7 * 60 - 43, -321_483_212));
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
												   .between(Duration.ofSeconds(9402042, 321_483_212), Duration.ofSeconds(39402042, 231_493_202));
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
												   .between(Duration.ofSeconds(-39402042, -231_493_202), Duration.ofSeconds(-9402042, -321_483_212));
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
												   .between(Duration.ofSeconds(9402042, 321_483_212), Duration.ofSeconds(39402042, 231_493_202));
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
												   .between(Duration.ofSeconds(-39402042, -231_493_202), Duration.ofSeconds(-9402042, -321_483_212));
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
												   .between(Duration.ofSeconds(9402042, 321_483_212), Duration.ofSeconds(39402042, 231_493_202));
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
												   .between(Duration.ofSeconds(-39402042, -231_493_202), Duration.ofSeconds(-9402042, -321_483_212));
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
		void milliseconds(@ForAll("precisionMilliseconds") Duration duration) {

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
		void microseconds(@ForAll("precisionMicroseconds") Duration duration) {

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
		void nanoseconds(@ForAll("precisionNanoseconds") Duration duration) {

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
			return Times.durations().ofPrecision(NANOS).between(Duration.ofSeconds(-10, 0), Duration.ofSeconds(10, 999_999_999));
		}

		@Provide
		Arbitrary<Duration> durationsNear0_2() {
			return Times.durations().ofPrecision(NANOS).between(Duration.ofSeconds(0, -999_999_999), Duration.ofSeconds(0, 999_999_999));
		}

	}

	@Group
	class InvalidConfigurations {

		@Property
		void ofPrecision(@ForAll ChronoUnit chronoUnit) {

			Assume.that(!chronoUnit.equals(NANOS));
			Assume.that(!chronoUnit.equals(MICROS));
			Assume.that(!chronoUnit.equals(MILLIS));
			Assume.that(!chronoUnit.equals(SECONDS));
			Assume.that(!chronoUnit.equals(MINUTES));
			Assume.that(!chronoUnit.equals(HOURS));

			assertThatThrownBy(
					() -> Times.durations().ofPrecision(chronoUnit)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionHoursMaxDurationSoonAfterMinDuration(
				@ForAll("precisionNanoseconds") Duration startDuration,
				@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

			Duration endDuration = startDuration.plusNanos(nanos);

			Assume.that(getMinute(startDuration) != 0 && getSecond(startDuration) != 0 && startDuration.getNano() != 0);
			Assume.that(getHour(startDuration) == getHour(endDuration));

			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(HOURS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMinutesMaxDurationSoonAfterMinDuration(
				@ForAll("precisionNanoseconds") Duration startDuration,
				@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

			Duration endDuration = startDuration.plusNanos(nanos);

			Assume.that(getSecond(startDuration) != 0 && startDuration.getNano() != 0);
			Assume.that(getMinute(startDuration) == getMinute(endDuration));

			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionSecondsMaxDurationSoonAfterMinDuration(
				@ForAll("precisionNanoseconds") Duration startDuration,
				@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

			Duration endDuration = startDuration.plusNanos(nanos);

			Assume.that(startDuration.getNano() != 0);
			Assume.that(getSecond(startDuration) == getSecond(endDuration));

			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMillisMaxDurationSoonAfterMinDuration(
				@ForAll("precisionNanoseconds") Duration startDuration,
				@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

			Duration endDuration = startDuration.plusNanos(nanos);

			Assume.that(startDuration.getNano() % 1_000_000 != 0);
			Assume.that(startDuration.getNano() % 1_000_000 + nanos < 1_000_000);

			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMicrosMaxDurationSoonAfterMinDuration(
				@ForAll("precisionNanoseconds") Duration startDuration,
				@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

			Duration endDuration = startDuration.plusNanos(nanos);

			Assume.that(startDuration.getNano() % 1_000 != 0);
			Assume.that(startDuration.getNano() % 1_000 + nanos < 1_000);

			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MICROS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionHoursMinDurationTooLate(
				@ForAll("lateDurations") Duration startDuration,
				@ForAll("lateDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(HOURS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void precisionMinutesMinDurationTooLate(
				@ForAll("lateDurations") Duration startDuration,
				@ForAll("lateDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void precisionSecondsMinDurationTooLate(
				@ForAll("lateDurations") Duration startDuration,
				@ForAll("lateDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void precisionMillisMinDurationTooLate(
				@ForAll("lateDurations") Duration startDuration,
				@ForAll("lateDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void precisionMicrosMinDurationTooLate(
				@ForAll("lateDurations") Duration startDuration,
				@ForAll("lateDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MICROS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void precisionHoursMaxDurationTooEarly(
				@ForAll("earlyDurations") Duration startDuration,
				@ForAll("earlyDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(HOURS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void precisionMinutesMaxDurationTooEarly(
				@ForAll("earlyDurations") Duration startDuration,
				@ForAll("earlyDurations") Duration endDuration
		) {
			Assume.that(startDuration.compareTo(endDuration) <= 0);
			assertThatThrownBy(
					() -> Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Provide
		Arbitrary<Duration> lateDurations() {
			return Times.durations()
						.ofPrecision(NANOS)
						.between(Duration.ofSeconds(Long.MAX_VALUE, 999_999_001), DefaultDurationArbitrary.DEFAULT_MAX);
		}

		@Provide
		Arbitrary<Duration> earlyDurations() {
			return Times.durations()
						.ofPrecision(NANOS)
						.between(DefaultDurationArbitrary.DEFAULT_MIN, Duration.ofSeconds(Long.MIN_VALUE, 999));
		}

	}

	int getSecond(Duration d) {
		return (int) (d.getSeconds() % 60);
	}

	int getMinute(Duration d) {
		return (int) ((d.getSeconds() % 3600) / 60);
	}

	long getHour(Duration d) {
		return d.getSeconds() / 3600;
	}

}
