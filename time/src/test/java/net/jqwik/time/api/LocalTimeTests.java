package net.jqwik.time.api;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class LocalTimeTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Provide
	Arbitrary<LocalTime> precisionHours() {
		return Times.times().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMinutes() {
		return Times.times().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<LocalTime> precisionSeconds() {
		return Times.times().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMilliseconds() {
		return Times.times().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMicroseconds() {
		return Times.times().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<LocalTime> precisionNanoseconds() {
		return Times.times().ofPrecision(NANOS);
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validLocalTimeIsGenerated(@ForAll("times") LocalTime time) {
			assertThat(time).isNotNull();
		}

		@Property
		void worstCaseTimeGeneration4NanosPossible(@ForAll("worstCase4NanosPossible") LocalTime time) {
			assertThat(time).isNotNull();
		}

		@Provide
		Arbitrary<LocalTime> worstCase4NanosPossible() {
			return Times.times().between(LocalTime.of(22, 59, 59, 999_999_998), LocalTime.of(23, 0, 0, 1));
		}

		@Property
		void worstCaseTimeGeneration2Minutes2SecondsPossible(@ForAll("worstCase2Minutes2SecondsPossible") LocalTime time) {
			assertThat(time).isNotNull();
		}

		@Provide
		Arbitrary<LocalTime> worstCase2Minutes2SecondsPossible() {
			return Times.times().minuteBetween(0, 1).secondBetween(0, 1);
		}

		@Property
		void validTimeZoneIsGenerated(@ForAll("timeZones") TimeZone timeZone) {
			assertThat(timeZone).isNotNull();
		}

		@Provide
		Arbitrary<TimeZone> timeZones() {
			return Times.timeZones();
		}

		@Property
		void validZoneIdIsGenerated(@ForAll("zoneIds") ZoneId zoneId) {
			assertThat(zoneId).isNotNull();
		}

		@Provide
		Arbitrary<ZoneId> zoneIds() {
			return Times.zoneIds();
		}

	}

	@Group
	class DefaultGeneration {

		@Property
		void validLocalTimeIsGenerated(@ForAll LocalTime time) {
			assertThat(time).isNotNull();
		}

		@Property
		void validZoneIdIsGenerated(@ForAll ZoneId zoneId) {
			assertThat(zoneId).isNotNull();
		}

		@Property
		void validTimeZoneIsGenerated(@ForAll TimeZone timeZone) {
			assertThat(timeZone).isNotNull();
		}

	}

	@Group
	class CheckTimeMethods {

		@Group
		class TimeMethods {

			@Property
			void atTheEarliest(@ForAll("times") LocalTime startTime, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(startTime);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("times") LocalTime endTime, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().atTheLatest(endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isBeforeOrEqualTo(endTime);
					return true;
				});

			}

			@Property
			void between(@ForAll("times") LocalTime startTime, @ForAll("times") LocalTime endTime, @ForAll Random random) {

				Assume.that(!startTime.isAfter(endTime));

				Arbitrary<LocalTime> times = Times.times().between(startTime, endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(startTime);
					assertThat(time).isBeforeOrEqualTo(endTime);
					return true;
				});
			}

			@Property
			void betweenEndTimeBeforeStartTime(
					@ForAll("times") LocalTime startTime,
					@ForAll("times") LocalTime endTime,
					@ForAll Random random
			) {

				Assume.that(startTime.isAfter(endTime));

				Arbitrary<LocalTime> times = Times.times().between(startTime, endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(endTime);
					assertThat(time).isBeforeOrEqualTo(startTime);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("times") LocalTime sameTime, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().between(sameTime, sameTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isEqualTo(sameTime);
					return true;
				});

			}

		}

		@Group
		class HourMethods {

			@Property
			void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll Random random) {

				Assume.that(startHour <= endHour);

				Arbitrary<LocalTime> times = Times.times().hourBetween(startHour, endHour);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getHour()).isGreaterThanOrEqualTo(startHour);
					assertThat(time.getHour()).isLessThanOrEqualTo(endHour);
					return true;
				});

			}

			@Property
			void hourBetweenSame(@ForAll("hours") int hour, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().hourBetween(hour, hour);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getHour()).isEqualTo(hour);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> hours() {
				return Arbitraries.integers().between(0, 23);
			}

		}

		@Group
		class MinuteMethods {

			@Property
			void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll Random random) {

				Assume.that(startMinute <= endMinute);

				Arbitrary<LocalTime> times = Times.times().minuteBetween(startMinute, endMinute);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getMinute()).isGreaterThanOrEqualTo(startMinute);
					assertThat(time.getMinute()).isLessThanOrEqualTo(endMinute);
					return true;
				});

			}

			@Property
			void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().minuteBetween(minute, minute);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getMinute()).isEqualTo(minute);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> minutes() {
				return Arbitraries.integers().between(0, 59);
			}

		}

		@Group
		class SecondMethods {

			@Property
			void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll Random random) {

				Assume.that(startSecond <= endSecond);

				Arbitrary<LocalTime> times = Times.times().secondBetween(startSecond, endSecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getSecond()).isGreaterThanOrEqualTo(startSecond);
					assertThat(time.getSecond()).isLessThanOrEqualTo(endSecond);
					return true;
				});

			}

			@Property
			void secondBetweenSame(@ForAll("seconds") int second, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().secondBetween(second, second);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getSecond()).isEqualTo(second);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> seconds() {
				return Arbitraries.integers().between(0, 59);
			}

		}

		@Group
		class PrecisionMethods {

			@Group
			class Hours {

				@Property
				void precision(@ForAll("precisionHours") LocalTime time) {
					assertThat(time.getMinute()).isEqualTo(0);
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionMinutes") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(HOURS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getMinute()).isEqualTo(0);
						assertThat(time.getSecond()).isEqualTo(0);
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(HOURS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getMinute()).isEqualTo(0);
						assertThat(time.getSecond()).isEqualTo(0);
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

			}

			@Group
			class Minutes {

				@Property
				void precision(@ForAll("precisionMinutes") LocalTime time) {
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionSeconds") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MINUTES);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getSecond()).isEqualTo(0);
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MINUTES);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getSecond()).isEqualTo(0);
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

			}

			@Group
			class Seconds {

				@Property
				void precision(@ForAll("precisionSeconds") LocalTime time) {
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionMilliseconds") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(SECONDS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(SECONDS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

			}

			@Group
			class Milliseconds {

				@Property
				void precision(@ForAll("precisionMilliseconds") LocalTime time) {
					assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionMicroseconds") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																																   .getNano() < 999_000_001);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MILLIS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano() % 1_000_000).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																																   .getNano() < 999_000_001);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MILLIS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano() % 1_000_000).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

			}

			@Group
			class Microseconds {

				@Property
				void precision(@ForAll("precisionMicroseconds") LocalTime time) {
					assertThat(time.getNano() % 1_000).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("times") LocalTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																																   .getNano() < 999_999_001);

					Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MICROS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano() % 1_000).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

			}

			@Group
			class Nanos {

				@Property
				void precisionNanoseconds(@ForAll("precisionNanoseconds") LocalTime time) {
					assertThat(time).isNotNull();
				}

			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			LocalTimeArbitrary times = Times.times();
			LocalTime value = falsifyThenShrink(times, random);
			assertThat(value).isEqualTo(LocalTime.of(0, 0, 0, 0));
		}

		@Property(tries=100)
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			LocalTimeArbitrary times = Times.times().ofPrecision(SECONDS);
			TestingFalsifier<LocalTime> falsifier = time -> time.isBefore(LocalTime.of(9, 13, 42, 143_921_111));
			LocalTime value = falsifyThenShrink(times, random, falsifier);
			assertThat(value).isEqualTo(LocalTime.of(9, 13, 43, 0));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void precisionNanos() {
			Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
					Times.times()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 33, 392_211_325)
						 )
						 .ofPrecision(NANOS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 33, 392_211_322),
					LocalTime.of(11, 22, 33, 392_211_323),
					LocalTime.of(11, 22, 33, 392_211_324),
					LocalTime.of(11, 22, 33, 392_211_325)
			);
		}

		@Example
		void precisionMicros() {
			Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
					Times.times()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 33, 392_214_325)
						 )
						 .ofPrecision(MICROS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 33, 392_212_000),
					LocalTime.of(11, 22, 33, 392_213_000),
					LocalTime.of(11, 22, 33, 392_214_000)
			);
		}

		@Example
		void precisionMillis() {
			Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
					Times.times()
						 .ofPrecision(MILLIS)
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 33, 395_214_325)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 33, 393_000_000),
					LocalTime.of(11, 22, 33, 394_000_000),
					LocalTime.of(11, 22, 33, 395_000_000)
			);
		}

		@Example
		void precisionSeconds() {
			Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
					Times.times()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 36, 395_214_325)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 34, 0),
					LocalTime.of(11, 22, 35, 0),
					LocalTime.of(11, 22, 36, 0)
			);
		}

		@Example
		void precisionMinutes() {
			Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
					Times.times()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 25, 36, 395_214_325)
						 )
						 .ofPrecision(MINUTES)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 23, 0, 0),
					LocalTime.of(11, 24, 0, 0),
					LocalTime.of(11, 25, 0, 0)
			);
		}

		@Example
		void precisionHours() {
			Optional<ExhaustiveGenerator<LocalTime>> optionalGenerator =
					Times.times()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(14, 25, 36, 395_214_325)
						 )
						 .ofPrecision(HOURS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(12, 0, 0, 0),
					LocalTime.of(13, 0, 0, 0),
					LocalTime.of(14, 0, 0, 0)
			);
		}

	}

	@Group
	class EdgeCasesTests {

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
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
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
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
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
				LocalTimeArbitrary times = Times.times();
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
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
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
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
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
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
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

	@Group
	class CheckEqualDistribution {

		@Property
		void hours(@ForAll("times") LocalTime time) {
			Statistics.label("Hours")
					  .collect(time.getHour())
					  .coverage(this::check24Coverage);
		}

		@Property
		void minutes(@ForAll("times") LocalTime time) {
			Statistics.label("Minutes")
					  .collect(time.getMinute())
					  .coverage(this::check60Coverage);
		}

		@Property
		void seconds(@ForAll("times") LocalTime time) {
			Statistics.label("Seconds")
					  .collect(time.getSecond())
					  .coverage(this::check60Coverage);
		}

		@Property
		void milliseconds(@ForAll("precisionMilliseconds") LocalTime time) {

			Statistics.label("Milliseconds x--")
					  .collect(time.getNano() / 100_000_000)
					  .coverage(this::check10Coverage);

			Statistics.label("Milliseconds -x-")
					  .collect((time.getNano() / 10_000_000) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Milliseconds --x")
					  .collect((time.getNano() / 1_000_000) % 10)
					  .coverage(this::check10Coverage);

		}

		@Property
		void microseconds(@ForAll("precisionMicroseconds") LocalTime time) {

			Statistics.label("Microseconds x--")
					  .collect((time.getNano() % 1_000_000) / 100_000)
					  .coverage(this::check10Coverage);

			Statistics.label("Microseconds -x-")
					  .collect(((time.getNano() % 1_000_000) / 10_000) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Microseconds --x")
					  .collect(((time.getNano() % 1_000_000) / 1_000) % 10)
					  .coverage(this::check10Coverage);

		}

		@Property
		void nanoseconds(@ForAll("precisionNanoseconds") LocalTime time) {

			Statistics.label("Nanoseconds x--")
					  .collect((time.getNano() % 1_000) / 100)
					  .coverage(this::check10Coverage);

			Statistics.label("Nanoseconds -x-")
					  .collect(((time.getNano() % 1_000) / 10) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Nanoseconds --x")
					  .collect((time.getNano() % 1_000) % 10)
					  .coverage(this::check10Coverage);

		}

		private void check10Coverage(StatisticsCoverage coverage) {
			for (int value = 0; value < 10; value++) {
				coverage.check(value).percentage(p -> p >= 5);
			}
		}

		private void check24Coverage(StatisticsCoverage coverage) {
			for (int value = 0; value < 24; value++) {
				coverage.check(value).percentage(p -> p >= 1.5);
			}
		}

		private void check60Coverage(StatisticsCoverage coverage) {
			for (int value = 0; value < 60; value++) {
				coverage.check(value).percentage(p -> p >= 0.3);
			}
		}

	}

	@Group
	class InvalidConfigurations {

		@Group
		class InvalidValues {

			@Property
			void minTimeAfterMaxTime(@ForAll("times") LocalTime minTime, @ForAll("times") LocalTime maxTime) {

				Assume.that(minTime.isAfter(maxTime));

				assertThatThrownBy(
						() -> Times.times().atTheLatest(maxTime).atTheEarliest(minTime)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void maxTimeBeforeMinTime(@ForAll("times") LocalTime minTime, @ForAll("times") LocalTime maxTime) {

				Assume.that(maxTime.isBefore(minTime));

				assertThatThrownBy(
						() -> Times.times().atTheEarliest(minTime).atTheLatest(maxTime)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void minMaxSecond(@ForAll int minSecond, @ForAll int maxSecond) {

				Assume.that(minSecond < 0 || minSecond > 59 || maxSecond < 0 || maxSecond > 59);

				assertThatThrownBy(
						() -> Times.times().hourBetween(minSecond, maxSecond)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void minMaxMinute(@ForAll int minMinute, @ForAll int maxMinute) {

				Assume.that(minMinute < 0 || minMinute > 59 || maxMinute < 0 || maxMinute > 59);

				assertThatThrownBy(
						() -> Times.times().hourBetween(minMinute, maxMinute)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void minMaxHour(@ForAll int minHour, @ForAll int maxHour) {

				Assume.that(minHour < 0 || minHour > 23 || maxHour < 0 || maxHour > 23);

				assertThatThrownBy(
						() -> Times.times().hourBetween(minHour, maxHour)
				).isInstanceOf(IllegalArgumentException.class);

			}

		}

		@Group
		class TimeGenerationPrecision {

			@Group
			class Generally {

				@Property
				void ofPrecision(@ForAll ChronoUnit chronoUnit) {

					Assume.that(!chronoUnit.equals(NANOS));
					Assume.that(!chronoUnit.equals(MICROS));
					Assume.that(!chronoUnit.equals(MILLIS));
					Assume.that(!chronoUnit.equals(SECONDS));
					Assume.that(!chronoUnit.equals(MINUTES));
					Assume.that(!chronoUnit.equals(HOURS));

					assertThatThrownBy(
							() -> Times.times().ofPrecision(chronoUnit)
					).isInstanceOf(IllegalArgumentException.class);

				}

			}

			@Group
			class Hours {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") LocalTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					LocalTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getMinute() != 0 && startTime.getSecond() != 0 && startTime.getNano() != 0);
					Assume.that(startTime.getHour() == endTime.getHour());

					assertThatThrownBy(
							() -> Times.times().between(startTime, endTime).ofPrecision(HOURS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") LocalTime time) {

					Assume.that(time.getMinute() != 0 || time.getSecond() != 0 || time.getNano() != 0);

					assertThatThrownBy(
							() -> Times.times().atTheEarliest(time).ofPrecision(HOURS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
					return Times.times().hourBetween(23, 23);
				}

			}

			@Group
			class Minutes {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") LocalTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					LocalTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getSecond() != 0 && startTime.getNano() != 0);
					Assume.that(startTime.getMinute() == endTime.getMinute());

					assertThatThrownBy(
							() -> Times.times().between(startTime, endTime).ofPrecision(MINUTES).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") LocalTime time) {

					Assume.that(time.getSecond() != 0 || time.getNano() != 0);

					assertThatThrownBy(
							() -> Times.times().atTheEarliest(time).ofPrecision(MINUTES).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
					return Times.times().hourBetween(23, 23).minuteBetween(59, 59);
				}

			}

			@Group
			class Seconds {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") LocalTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					LocalTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getNano() != 0);
					Assume.that(startTime.getSecond() == endTime.getSecond());

					assertThatThrownBy(
							() -> Times.times().between(startTime, endTime).ofPrecision(SECONDS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(
						@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
						@ForAll @IntRange(min = 1, max = 999_999_999) int nanos
				) {

					time = time.withNano(nanos);
					final LocalTime finalTime = time;

					assertThatThrownBy(
							() -> Times.times().atTheEarliest(finalTime).ofPrecision(SECONDS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
					return Times.times().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
				}

			}

			@Group
			class Millis {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") LocalTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					LocalTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getNano() % 1_000_000 != 0);
					Assume.that(startTime.getNano() % 1_000_000 + nanos < 1_000_000);

					assertThatThrownBy(
							() -> Times.times().between(startTime, endTime).ofPrecision(MILLIS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(
						@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
						@ForAll @IntRange(min = 999_000_001, max = 999_999_999) int nanos
				) {

					time = time.withNano(nanos);
					final LocalTime finalTime = time;

					assertThatThrownBy(
							() -> Times.times().atTheEarliest(finalTime).ofPrecision(MILLIS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
					return Times.times().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
				}

			}

			@Group
			class Micros {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") LocalTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					LocalTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getNano() % 1_000 != 0);
					Assume.that(startTime.getNano() % 1_000 + nanos < 1_000);

					assertThatThrownBy(
							() -> Times.times().between(startTime, endTime).ofPrecision(MICROS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(
						@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
						@ForAll @IntRange(min = 999_999_001, max = 999_999_999) int nanos
				) {

					time = time.withNano(nanos);
					final LocalTime finalTime = time;

					assertThatThrownBy(
							() -> Times.times().atTheEarliest(finalTime).ofPrecision(MICROS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
					return Times.times().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
				}

			}

			@Group
			class Nanos {

			}

		}

	}

}
