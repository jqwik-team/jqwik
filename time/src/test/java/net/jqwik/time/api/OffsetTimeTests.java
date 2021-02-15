package net.jqwik.time.api;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class OffsetTimeTests {

	@Provide
	Arbitrary<OffsetTime> times() {
		return Times.offsetTimes();
	}

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Provide
	Arbitrary<LocalTime> localTimes() {
		return Times.times();
	}

	@Provide
	Arbitrary<OffsetTime> precisionHours() {
		return Times.offsetTimes().constrainPrecision(HOURS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMinutes() {
		return Times.offsetTimes().constrainPrecision(MINUTES);
	}

	@Provide
	Arbitrary<OffsetTime> precisionSeconds() {
		return Times.offsetTimes().constrainPrecision(SECONDS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMilliseconds() {
		return Times.offsetTimes().constrainPrecision(MILLIS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMicroseconds() {
		return Times.offsetTimes().constrainPrecision(MICROS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionNanoseconds() {
		return Times.offsetTimes().constrainPrecision(NANOS);
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validOffsetTimeIsGenerated(@ForAll("times") OffsetTime time) {
			assertThat(time).isNotNull();
		}

	}

	@Property
	@Disabled("Not available at the moment")
	void validOffsetTimeIsGeneratedWithAnnotation(@ForAll OffsetTime time) {
		assertThat(time).isNotNull();
	}

	@Group
	@Disabled("Working on it")
	class CheckTimeMethods {

		@Group
		class TimeMethods {

			@Property
			void atTheEarliest(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(startTime);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("times") OffsetTime endTime, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().atTheLatest(endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isBeforeOrEqualTo(endTime);
					return true;
				});

			}

			@Property
			void between(@ForAll("times") OffsetTime startTime, @ForAll("times") OffsetTime endTime, @ForAll Random random) {

				Assume.that(!startTime.isAfter(endTime));

				Arbitrary<OffsetTime> times = Times.offsetTimes().between(startTime, endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(startTime);
					assertThat(time).isBeforeOrEqualTo(endTime);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("times") OffsetTime sameTime, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().between(sameTime, sameTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isEqualTo(sameTime);
					return true;
				});

			}

		}

		@Group
		class TimeMethod {

			@Property
			void timeBetween(@ForAll("localTimes") LocalTime startTime, @ForAll("localTimes") LocalTime endTime, @ForAll Random random) {

				Assume.that(!startTime.isAfter(endTime));

				Arbitrary<OffsetTime> times = Times.offsetTimes().timeBetween(startTime, endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(OffsetTime.of(startTime, time.getOffset()));
					assertThat(time).isBeforeOrEqualTo(OffsetTime.of(endTime, time.getOffset()));
					return true;
				});

			}

			@Property
			void timeBetweenSame(@ForAll("localTimes") LocalTime localTime, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().timeBetween(localTime, localTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isEqualTo(OffsetTime.of(localTime, time.getOffset()));
					return true;
				});

			}

		}

		@Group
		class HourMethods {

			@Property
			void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll Random random) {

				Assume.that(startHour <= endHour);

				Arbitrary<OffsetTime> times = Times.offsetTimes().hourBetween(startHour, endHour);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getHour()).isGreaterThanOrEqualTo(startHour);
					assertThat(time.getHour()).isLessThanOrEqualTo(endHour);
					return true;
				});

			}

			@Property
			void hourBetweenSame(@ForAll("hours") int hour, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().hourBetween(hour, hour);

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

				Arbitrary<OffsetTime> times = Times.offsetTimes().minuteBetween(startMinute, endMinute);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getMinute()).isGreaterThanOrEqualTo(startMinute);
					assertThat(time.getMinute()).isLessThanOrEqualTo(endMinute);
					return true;
				});

			}

			@Property
			void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().minuteBetween(minute, minute);

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

				Arbitrary<OffsetTime> times = Times.offsetTimes().secondBetween(startSecond, endSecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getSecond()).isGreaterThanOrEqualTo(startSecond);
					assertThat(time.getSecond()).isLessThanOrEqualTo(endSecond);
					return true;
				});

			}

			@Property
			void secondBetweenSame(@ForAll("seconds") int second, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().secondBetween(second, second);

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
		class OffsetMethod {

			@Property
			void between(@ForAll("offsets") ZoneOffset startOffset, @ForAll("offsets") ZoneOffset endOffset, @ForAll Random random) {

				Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

				Arbitrary<OffsetTime> times = Times.offsetTimes().offsetBetween(startOffset, endOffset);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getOffset().getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
					assertThat(time.getOffset().getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
					return true;
				});

			}

			@Property
			void betweenSame(@ForAll("offsets") ZoneOffset offset, @ForAll Random random) {

				Arbitrary<OffsetTime> times = Times.offsetTimes().offsetBetween(offset, offset);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getOffset()).isEqualTo(offset);
					return true;
				});

			}

		}

		@Group
		class PrecisionMethods {

			@Group
			class Hours {

				@Property
				void precision(@ForAll("precisionHours") OffsetTime time) {
					assertThat(time.getMinute()).isEqualTo(0);
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionMinutes") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(HOURS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getMinute()).isEqualTo(0);
						assertThat(time.getSecond()).isEqualTo(0);
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(HOURS);

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
				void precision(@ForAll("precisionMinutes") OffsetTime time) {
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionSeconds") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(MINUTES);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getSecond()).isEqualTo(0);
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(MINUTES);

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
				void precision(@ForAll("precisionSeconds") OffsetTime time) {
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionMilliseconds") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(SECONDS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano()).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(SECONDS);

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
				void precision(@ForAll("precisionMilliseconds") OffsetTime time) {
					assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("precisionMicroseconds") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																																   .getNano() < 999_000_001);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(MILLIS);

					assertAllGenerated(times.generator(1000), random, time -> {
						assertThat(time.getNano() % 1_000_000).isEqualTo(0);
						assertThat(time).isAfterOrEqualTo(startTime);
						return true;
					});

				}

				@Property
				void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																																   .getNano() < 999_000_001);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(MILLIS);

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
				void precision(@ForAll("precisionMicroseconds") OffsetTime time) {
					assertThat(time.getNano() % 1_000).isEqualTo(0);
				}

				@Property
				void precisionMinTime(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

					Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																																   .getNano() < 999_999_001);

					Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime).constrainPrecision(MICROS);

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
				void precision(@ForAll("precisionNanoseconds") OffsetTime time) {
					assertThat(time).isNotNull();
				}

			}

		}

	}
	/*  //TODO
	@Group
	class Shrinking {

		/*@Property
		void defaultShrinking(@ForAll Random random) {
			OffsetTimeArbitrary times = Times.offsetTimes();
			OffsetTime value = falsifyThenShrink(times, random);
			assertThat(value).isEqualTo(null); //TODO
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			OffsetTimeArbitrary times = Times.offsetTimes().constrainPrecision(SECONDS);
			TestingFalsifier<OffsetTime> falsifier = time -> time.isBefore(LocalTime.of(9, 13, 42, 143_921_111));
			OffsetTime value = falsifyThenShrink(times, random, falsifier);
			assertThat(value).isEqualTo(LocalTime.of(9, 13, 43, 0)); //TODO
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
					Times.offsetTimes()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 33, 392_211_325)
						 )
						 .constrainPrecision(NANOS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
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
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
					Times.offsetTimes()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 33, 392_214_325)
						 )
						 .constrainPrecision(MICROS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 33, 392_212_000),
					LocalTime.of(11, 22, 33, 392_213_000),
					LocalTime.of(11, 22, 33, 392_214_000)
			);
		}

		@Example
		void precisionMillis() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
					Times.offsetTimes()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 33, 395_214_325)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 33, 393_000_000),
					LocalTime.of(11, 22, 33, 394_000_000),
					LocalTime.of(11, 22, 33, 395_000_000)
			);
		}

		@Example
		void precisionSeconds() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
					Times.offsetTimes()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 22, 36, 395_214_325)
						 )
						 .constrainPrecision(SECONDS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 22, 34, 0),
					LocalTime.of(11, 22, 35, 0),
					LocalTime.of(11, 22, 36, 0)
			);
		}

		@Example
		void precisionMinutes() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
					Times.offsetTimes()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(11, 25, 36, 395_214_325)
						 )
						 .constrainPrecision(MINUTES)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					LocalTime.of(11, 23, 0, 0),
					LocalTime.of(11, 24, 0, 0),
					LocalTime.of(11, 25, 0, 0)
			);
		}

		@Example
		void precisionHours() {
			Optional<ExhaustiveGenerator<OffsetTime>> optionalGenerator =
					Times.offsetTimes()
						 .between(
								 LocalTime.of(11, 22, 33, 392_211_322),
								 LocalTime.of(14, 25, 36, 395_214_325)
						 )
						 .constrainPrecision(HOURS)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<OffsetTime> generator = optionalGenerator.get();
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
				OffsetTimeArbitrary times = Times.offsetTimes().constrainPrecision(HOURS);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(0, 0, 0, 0),
						LocalTime.of(23, 0, 0, 0)
				);
			}

			@Example
			void between() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(HOURS)
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(12, 0, 0, 0),
						LocalTime.of(21, 0, 0, 0)
				);
			}

			@Example
			void betweenHour() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(HOURS)
							 .hourBetween(11, 12);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
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
				OffsetTimeArbitrary times = Times.offsetTimes().constrainPrecision(MINUTES);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(0, 0, 0, 0),
						LocalTime.of(23, 59, 0, 0)
				);
			}

			@Example
			void between() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(MINUTES)
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(11, 24, 0, 0),
						LocalTime.of(21, 15, 0, 0)
				);
			}

			@Example
			void betweenMinute() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(MINUTES)
							 .hourBetween(11, 12)
							 .minuteBetween(23, 31);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
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
				OffsetTimeArbitrary times = Times.offsetTimes().constrainPrecision(SECONDS);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(0, 0, 0, 0),
						LocalTime.of(23, 59, 59, 0)
				);
			}

			@Example
			void between() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(SECONDS)
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(11, 23, 22, 0),
						LocalTime.of(21, 15, 19, 0)
				);
			}

			@Example
			void betweenSecond() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(SECONDS)
							 .hourBetween(11, 12)
							 .minuteBetween(23, 31)
							 .secondBetween(5, 10);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
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
				OffsetTimeArbitrary times = Times.offsetTimes();
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(0, 0, 0, 0),
						LocalTime.of(23, 59, 59, 999_000_000)
				);
			}

			@Example
			void between() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(11, 23, 21, 302_000_000),
						LocalTime.of(21, 15, 19, 199_000_000)
				);
			}

			@Example
			void betweenSecond() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .hourBetween(11, 12)
							 .minuteBetween(23, 31)
							 .secondBetween(5, 10);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
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
				OffsetTimeArbitrary times = Times.offsetTimes().constrainPrecision(MICROS);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(0, 0, 0, 0),
						LocalTime.of(23, 59, 59, 999_999_000)
				);
			}

			@Example
			void between() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(MICROS)
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(11, 23, 21, 301_429_000),
						LocalTime.of(21, 15, 19, 199_321_000)
				);
			}

			@Example
			void betweenSecond() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(MICROS)
							 .hourBetween(11, 12)
							 .minuteBetween(23, 31)
							 .secondBetween(5, 10);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
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
				OffsetTimeArbitrary times = Times.offsetTimes().constrainPrecision(NANOS);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(0, 0, 0, 0),
						LocalTime.of(23, 59, 59, 999_999_999)
				);
			}

			@Example
			void between() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(NANOS)
							 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(11, 23, 21, 301_428_111),
						LocalTime.of(21, 15, 19, 199_321_789)
				);
			}

			@Example
			void betweenSecond() {
				OffsetTimeArbitrary times =
						Times.offsetTimes()
							 .constrainPrecision(NANOS)
							 .hourBetween(11, 12)
							 .minuteBetween(23, 31)
							 .secondBetween(5, 10);
				Set<OffsetTime> edgeCases = collectEdgeCases(times.edgeCases());
				assertThat(edgeCases).hasSize(2);
				assertThat(edgeCases).containsExactlyInAnyOrder(
						LocalTime.of(11, 23, 5, 0),
						LocalTime.of(12, 31, 10, 999_999_999)
				);
			}

		}

	}*/

	@Group
	class CheckEqualDistribution {

		@Property
		void hours(@ForAll("times") OffsetTime time) {
			Statistics.label("Hours")
					  .collect(time.getHour())
					  .coverage(this::check24Coverage);
		}

		@Property
		void minutes(@ForAll("times") OffsetTime time) {
			Statistics.label("Minutes")
					  .collect(time.getMinute())
					  .coverage(this::check60Coverage);
		}

		@Property
		void seconds(@ForAll("times") OffsetTime time) {
			Statistics.label("Seconds")
					  .collect(time.getSecond())
					  .coverage(this::check60Coverage);
		}

		@Property
		void milliseconds(@ForAll("precisionMilliseconds") OffsetTime time) {

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
		void microseconds(@ForAll("precisionMicroseconds") OffsetTime time) {

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
		void nanoseconds(@ForAll("precisionNanoseconds") OffsetTime time) {

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

		@Property
		void negativeAndPositiveOffsetValuesAreGenerated(@ForAll("times") OffsetTime time) {
			ZoneOffset offset = time.getOffset();
			int totalSeconds = offset.getTotalSeconds();
			Assume.that(totalSeconds != 0);
			Statistics.label("Negative value")
					  .collect(totalSeconds < 0)
					  .coverage(this::check5050BooleanCoverage);
		}

		@Property
		void offsetValueZeroIsGenerated(@ForAll("times") OffsetTime time) {
			ZoneOffset offset = time.getOffset();
			Statistics.label("00:00:00 is possible")
					  .collect(offset.getTotalSeconds() == 0)
					  .coverage(coverage -> {
						  coverage.check(true).count(c -> c >= 1);
					  });
		}

		@Property
		void minusAndPlusOffsetIsPossibleWhenHourIsZero(@ForAll("offsetsNear0") OffsetTime time) {
			ZoneOffset offset = time.getOffset();
			int totalSeconds = offset.getTotalSeconds();
			Assume.that(totalSeconds > -3600 && totalSeconds < 3600 && totalSeconds != 0);
			Statistics.label("Negative value with Hour is zero")
					  .collect(totalSeconds < 0)
					  .coverage(this::check5050BooleanCoverage);
		}

		@Property
		void offsetHours(@ForAll("times") OffsetTime time) {
			ZoneOffset offset = time.getOffset();
			Statistics.label("Hours")
					  .collect(offset.getTotalSeconds() / 3600)
					  .coverage(this::checkOffsetHourCoverage);
		}

		@Property
		void offsetMinutes(@ForAll("times") OffsetTime time) {
			ZoneOffset offset = time.getOffset();
			Statistics.label("Minutes")
					  .collect(Math.abs((offset.getTotalSeconds() % 3600) / 60))
					  .coverage(this::checkOffsetMinuteCoverage);
		}

		@Provide
		Arbitrary<OffsetTime> offsetsNear0() {
			return Times.offsetTimes().offsetBetween(ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(1, 0, 0));
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

		private void check5050BooleanCoverage(StatisticsCoverage coverage) {
			coverage.check(true).percentage(p -> p >= 35);
			coverage.check(false).percentage(p -> p >= 35);
		}

		private void checkOffsetHourCoverage(StatisticsCoverage coverage) {
			for (int value = -12; value <= 14; value++) {
				coverage.check(value).percentage(p -> p >= 1.5);
			}
		}

		private void checkOffsetMinuteCoverage(StatisticsCoverage coverage) {
			for (int value = 0; value < 60; value += 15) {
				coverage.check(value).percentage(p -> p >= 12);
			}
		}

	}

	@Group
	@Disabled("Working on it")
	class InvalidConfigurations {

		@Group
		class InvalidValues {

			@Property
			void minTimeAfterMaxTime(@ForAll("times") OffsetTime minTime, @ForAll("times") OffsetTime maxTime) {

				Assume.that(minTime.isAfter(maxTime));

				assertThatThrownBy(
						() -> Times.offsetTimes().atTheLatest(maxTime).atTheEarliest(minTime)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void maxTimeBeforeMinTime(@ForAll("times") OffsetTime minTime, @ForAll("times") OffsetTime maxTime) {

				Assume.that(maxTime.isBefore(minTime));

				assertThatThrownBy(
						() -> Times.offsetTimes().atTheEarliest(minTime).atTheLatest(maxTime)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void minMaxSecond(@ForAll int minSecond, @ForAll int maxSecond) {

				Assume.that(minSecond < 0 || minSecond > 59 || maxSecond < 0 || maxSecond > 59);

				assertThatThrownBy(
						() -> Times.offsetTimes().hourBetween(minSecond, maxSecond)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void minMaxMinute(@ForAll int minMinute, @ForAll int maxMinute) {

				Assume.that(minMinute < 0 || minMinute > 59 || maxMinute < 0 || maxMinute > 59);

				assertThatThrownBy(
						() -> Times.offsetTimes().hourBetween(minMinute, maxMinute)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void minMaxHour(@ForAll int minHour, @ForAll int maxHour) {

				Assume.that(minHour < 0 || minHour > 23 || maxHour < 0 || maxHour > 23);

				assertThatThrownBy(
						() -> Times.offsetTimes().hourBetween(minHour, maxHour)
				).isInstanceOf(IllegalArgumentException.class);

			}

		}

		@Group
		class TimeGenerationPrecision {

			@Group
			class Generally {

				@Property
				void minMaxHour(@ForAll ChronoUnit chronoUnit) {

					Assume.that(!chronoUnit.equals(NANOS));
					Assume.that(!chronoUnit.equals(MICROS));
					Assume.that(!chronoUnit.equals(MILLIS));
					Assume.that(!chronoUnit.equals(SECONDS));
					Assume.that(!chronoUnit.equals(MINUTES));
					Assume.that(!chronoUnit.equals(HOURS));

					assertThatThrownBy(
							() -> Times.offsetTimes().constrainPrecision(chronoUnit)
					).isInstanceOf(IllegalArgumentException.class);

				}

			}

			@Group
			class Hours {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") OffsetTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					OffsetTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getMinute() != 0 && startTime.getSecond() != 0 && startTime.getNano() != 0);
					Assume.that(startTime.getHour() == endTime.getHour());

					assertThatThrownBy(
							() -> Times.offsetTimes().between(startTime, endTime).constrainPrecision(HOURS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") OffsetTime time) {

					Assume.that(time.getMinute() != 0 || time.getSecond() != 0 || time.getNano() != 0);

					assertThatThrownBy(
							() -> Times.offsetTimes().atTheEarliest(time).constrainPrecision(HOURS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<OffsetTime> precisionMinTimeTooLateProvide() {
					return Times.offsetTimes().hourBetween(23, 23);
				}

			}

			@Group
			class Minutes {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") OffsetTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					OffsetTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getSecond() != 0 && startTime.getNano() != 0);
					Assume.that(startTime.getMinute() == endTime.getMinute());

					assertThatThrownBy(
							() -> Times.offsetTimes().between(startTime, endTime).constrainPrecision(MINUTES).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") OffsetTime time) {

					Assume.that(time.getSecond() != 0 || time.getNano() != 0);

					assertThatThrownBy(
							() -> Times.offsetTimes().atTheEarliest(time).constrainPrecision(MINUTES).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<OffsetTime> precisionMinTimeTooLateProvide() {
					return Times.offsetTimes().hourBetween(23, 23).minuteBetween(59, 59);
				}

			}

			@Group
			class Seconds {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") OffsetTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					OffsetTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getNano() != 0);
					Assume.that(startTime.getSecond() == endTime.getSecond());

					assertThatThrownBy(
							() -> Times.offsetTimes().between(startTime, endTime).constrainPrecision(SECONDS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(
						@ForAll("precisionMinTimeTooLateProvide") OffsetTime time,
						@ForAll @IntRange(min = 1, max = 999_999_999) int nanos
				) {

					time = time.withNano(nanos);
					final OffsetTime finalTime = time;

					assertThatThrownBy(
							() -> Times.offsetTimes().atTheEarliest(finalTime).constrainPrecision(SECONDS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<OffsetTime> precisionMinTimeTooLateProvide() {
					return Times.offsetTimes().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
				}

			}

			@Group
			class Millis {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") OffsetTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					OffsetTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getNano() % 1_000_000 != 0);
					Assume.that(startTime.getNano() % 1_000_000 + nanos < 1_000_000);

					assertThatThrownBy(
							() -> Times.offsetTimes().between(startTime, endTime).constrainPrecision(MILLIS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(
						@ForAll("precisionMinTimeTooLateProvide") OffsetTime time,
						@ForAll @IntRange(min = 999_000_001, max = 999_999_999) int nanos
				) {

					time = time.withNano(nanos);
					final OffsetTime finalTime = time;

					assertThatThrownBy(
							() -> Times.offsetTimes().atTheEarliest(finalTime).constrainPrecision(MILLIS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<OffsetTime> precisionMinTimeTooLateProvide() {
					return Times.offsetTimes().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
				}

			}

			@Group
			class Micros {

				@Property
				void precisionMaxTimeSoonAfterMinTime(
						@ForAll("precisionNanoseconds") OffsetTime startTime,
						@ForAll @IntRange(min = 1, max = 200) int nanos
				) {

					OffsetTime endTime = startTime.plusNanos(nanos);

					Assume.that(endTime.isAfter(startTime));
					Assume.that(startTime.getNano() % 1_000 != 0);
					Assume.that(startTime.getNano() % 1_000 + nanos < 1_000);

					assertThatThrownBy(
							() -> Times.offsetTimes().between(startTime, endTime).constrainPrecision(MICROS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Property
				void precisionMinTimeTooLate(
						@ForAll("precisionMinTimeTooLateProvide") OffsetTime time,
						@ForAll @IntRange(min = 999_999_001, max = 999_999_999) int nanos
				) {

					time = time.withNano(nanos);
					final OffsetTime finalTime = time;

					assertThatThrownBy(
							() -> Times.offsetTimes().atTheEarliest(finalTime).constrainPrecision(MICROS).generator(1000)
					).isInstanceOf(IllegalArgumentException.class);

				}

				@Provide
				Arbitrary<OffsetTime> precisionMinTimeTooLateProvide() {
					return Times.offsetTimes().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
				}

			}

			@Group
			class Nanos {

			}

		}

	}

}
