package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.Month.*;
import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class LocalDateTimeTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
	}

	@Provide
	Arbitrary<LocalDateTime> precisionHours() {
		return DateTimes.dateTimes().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMinutes() {
		return DateTimes.dateTimes().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionSeconds() {
		return DateTimes.dateTimes().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMilliseconds() {
		return DateTimes.dateTimes().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMicroseconds() {
		return DateTimes.dateTimes().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionNanoseconds() {
		return DateTimes.dateTimes().ofPrecision(NANOS);
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validLocalDateTimeTimeIsGenerated(@ForAll("dateTimes") LocalDateTime dateTime) {
			assertThat(dateTime).isNotNull();
		}

		@Property
		void onlyFewValuesPossibleAtEndOfDay(
				@ForAll LocalDate date,
				@ForAll @IntRange(min = 50, max = 59) int secondEnd,
				@ForAll @IntRange(min = 0, max = 10) int secondStart,
				@ForAll Random random
		) {

			Assume.that(!date.isEqual(LocalDate.MAX));
			LocalDateTime min = LocalDateTime.of(date, LocalTime.of(23, 59, secondEnd));
			LocalDateTime max = LocalDateTime.of(date.plusDays(1), LocalTime.of(0, 0, secondStart));

			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes().between(min, max);

			assertAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
				assertThat(dateTime).isNotNull();
				return true;
			});

		}

	}

	@Group
	class DefaultGeneration {

		@Property
		void validLocalDateTimeIsGenerated(@ForAll LocalDateTime dateTime) {
			assertThat(dateTime).isNotNull();
		}

	}

	@Group
	class CheckDateTimeMethods {

		@Group
		class DateTimeMethods {

			@Property
			void atTheEarliest(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

				Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().atTheEarliest(min);

				assertAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("dateTimes") LocalDateTime max, @ForAll Random random) {

				Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().atTheLatest(max);

				assertAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
					assertThat(dateTime).isBeforeOrEqualTo(max);
					return true;
				});

			}

			@Property
			void between(@ForAll("dateTimes") LocalDateTime min, @ForAll("dateTimes") LocalDateTime max, @ForAll Random random) {

				Assume.that(!min.isAfter(max));

				Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(min, max);

				assertAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
					assertThat(dateTime).isAfterOrEqualTo(min);
					assertThat(dateTime).isBeforeOrEqualTo(max);
					return true;
				});

			}

			@Property
			void betweenMinAfterMax(@ForAll("dateTimes") LocalDateTime min, @ForAll("dateTimes") LocalDateTime max, @ForAll Random random) {

				Assume.that(min.isAfter(max));

				Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(min, max);

				assertAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
					assertThat(dateTime).isAfterOrEqualTo(max);
					assertThat(dateTime).isBeforeOrEqualTo(min);
					return true;
				});

			}

			@Property
			void betweenSame(@ForAll("dateTimes") LocalDateTime same, @ForAll Random random) {

				Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().between(same, same);

				assertAllGenerated(dateTimes.generator(1000, true), random, dateTime -> {
					assertThat(dateTime).isEqualTo(same);
					return true;
				});

			}

		}

		@Group
		class CheckTimeMethods {

			@Group
			class PrecisionMethods {

				@Group
				class Hours {

					@Property
					void precision(@ForAll("precisionHours") LocalDateTime dateTime) {
						assertThat(dateTime.getMinute()).isEqualTo(0);
						assertThat(dateTime.getSecond()).isEqualTo(0);
						assertThat(dateTime.getNano()).isEqualTo(0);
					}

					@Property
					void precisionMinTimePrecisionMinutes(@ForAll("precisionMinutes") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(HOURS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getMinute()).isEqualTo(0);
							assertThat(dateTime.getSecond()).isEqualTo(0);
							assertThat(dateTime.getNano()).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

					@Property
					void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(HOURS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getMinute()).isEqualTo(0);
							assertThat(dateTime.getSecond()).isEqualTo(0);
							assertThat(dateTime.getNano()).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

				}

				@Group
				class Minutes {

					@Property
					void precision(@ForAll("precisionMinutes") LocalDateTime dateTime) {
						assertThat(dateTime.getSecond()).isEqualTo(0);
						assertThat(dateTime.getNano()).isEqualTo(0);
					}

					@Property
					void precisionMinTimePrecisionSeconds(@ForAll("precisionSeconds") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate()
										.isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23 || min.getMinute() != 59);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MINUTES);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getSecond()).isEqualTo(0);
							assertThat(dateTime.getNano()).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

					@Property
					void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate()
										.isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23 || min.getMinute() != 59);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MINUTES);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getSecond()).isEqualTo(0);
							assertThat(dateTime.getNano()).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

				}

				@Group
				class Seconds {

					@Property
					void precision(@ForAll("precisionSeconds") LocalDateTime dateTime) {
						assertThat(dateTime.getNano()).isEqualTo(0);
					}

					@Property
					void precisionMinTimePrecisionMillis(@ForAll("precisionMilliseconds") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
											|| min.getHour() != 23
											|| min.getMinute() != 59
											|| min.getSecond() != 59);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(SECONDS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getNano()).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

					@Property
					void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
											|| min.getHour() != 23
											|| min.getMinute() != 59
											|| min.getSecond() != 59);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(SECONDS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getNano()).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

				}

				@Group
				class Milliseconds {

					@Property
					void precision(@ForAll("precisionMilliseconds") LocalDateTime dateTime) {
						assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
					}

					@Property
					void precisionMinTimePrecisionMicros(@ForAll("precisionMicroseconds") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
											|| min.getHour() != 23
											|| min.getMinute() != 59
											|| min.getSecond() != 59
											|| min.getNano() < 999_000_001);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MILLIS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

					@Property
					void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
											|| min.getHour() != 23
											|| min.getMinute() != 59
											|| min.getSecond() != 59
											|| min.getNano() < 999_000_001);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MILLIS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

				}

				@Group
				class Microseconds {

					@Property
					void precision(@ForAll("precisionMicroseconds") LocalDateTime dateTime) {
						assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
					}

					@Property
					void precisionMinTimePrecisionNanos(@ForAll("precisionNanoseconds") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
											|| min.getHour() != 23
											|| min.getMinute() != 59
											|| min.getSecond() != 59
											|| min.getNano() < 999_999_001);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MICROS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

					@Property
					void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

						Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
											|| min.getHour() != 23
											|| min.getMinute() != 59
											|| min.getSecond() != 59
											|| min.getNano() < 999_999_001);

						Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MICROS);

						assertAllGenerated(times.generator(1000), random, dateTime -> {
							assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
							assertThat(dateTime).isAfterOrEqualTo(min);
							return true;
						});

					}

				}

				@Group
				class Nanos {

					@Property
					void precisionNanoseconds(@ForAll("precisionNanoseconds") LocalDateTime dateTime) {
						assertThat(dateTime).isNotNull();
					}

				}

			}

		}

	}

	@Group
	class Shrinking {

		@Disabled("Not working at the Moment")
		@Property
		void defaultShrinking(@ForAll Random random) {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
			LocalDateTime value = falsifyThenShrink(dateTimes, random);
			assertThat(value).isEqualTo(LocalDateTime.of(1900, JANUARY, 1, 0, 0, 0));
		}

		@Disabled("Produces StackOverFlow")
		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
			TestingFalsifier<LocalDateTime> falsifier = dateTime -> dateTime.isBefore(LocalDateTime.of(2013, MAY, 25, 13, 12, 55));
			LocalDateTime value = falsifyThenShrink(dateTimes, random, falsifier);
			assertThat(value).isAfterOrEqualTo(LocalDateTime.of(2013, MAY, 25, 13, 12, 55));
			assertThat(value).isBeforeOrEqualTo(LocalDateTime.of(2013, MAY, 26, 0, 0, 0));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<LocalDateTime>> optionalGenerator =
					DateTimes.dateTimes()
							 .between(
									 LocalDateTime.of(2013, 5, 25, 11, 22, 31, 392_211_322),
									 LocalDateTime.of(2013, 5, 25, 11, 22, 35, 392_211_325)
							 )
							 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDateTime> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
					LocalDateTime.of(2013, 5, 25, 11, 22, 32),
					LocalDateTime.of(2013, 5, 25, 11, 22, 33),
					LocalDateTime.of(2013, 5, 25, 11, 22, 34),
					LocalDateTime.of(2013, 5, 25, 11, 22, 35)
			);
		}

	}

	@Group
	class EdgeCasesTests {

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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
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
				LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
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
								 .between(
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
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
								 .between(
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2020, AUGUST, 23, 21, 15, 19, 199_321_789)
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
										 LocalDateTime.of(2013, MAY, 25, 11, 23, 21, 301_428_111),
										 LocalDateTime.of(2013, MAY, 25, 21, 15, 19, 199_321_789)
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

	@Group
	@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
	class CheckEqualDistribution {

		@Property
		void months(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Months")
					  .collect(dateTime.getMonth())
					  .coverage(this::checkMonthCoverage);
		}

		@Property
		void dayOfMonths(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Day of months")
					  .collect(dateTime.getDayOfMonth())
					  .coverage(this::checkDayOfMonthCoverage);
		}

		@Property
		void dayOfWeeks(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Day of weeks")
					  .collect(dateTime.getDayOfWeek())
					  .coverage(this::checkDayOfWeekCoverage);
		}

		@Property
		void leapYears(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Leap years")
					  .collect(new GregorianCalendar().isLeapYear(dateTime.getYear()))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p >= 20);
						  coverage.check(false).percentage(p -> p >= 65);
					  });
		}

		@Property
		void hours(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Hours")
					  .collect(dateTime.getHour())
					  .coverage(this::check24Coverage);
		}

		@Property
		void minutes(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Minutes")
					  .collect(dateTime.getMinute())
					  .coverage(this::check60Coverage);
		}

		@Property
		void seconds(@ForAll("dateTimes") LocalDateTime dateTime) {
			Statistics.label("Seconds")
					  .collect(dateTime.getSecond())
					  .coverage(this::check60Coverage);
		}

		@Property
		void milliseconds(@ForAll("precisionMilliseconds") LocalDateTime dateTime) {

			Statistics.label("Milliseconds x--")
					  .collect(dateTime.getNano() / 100_000_000)
					  .coverage(this::check10Coverage);

			Statistics.label("Milliseconds -x-")
					  .collect((dateTime.getNano() / 10_000_000) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Milliseconds --x")
					  .collect((dateTime.getNano() / 1_000_000) % 10)
					  .coverage(this::check10Coverage);

		}

		@Property
		void microseconds(@ForAll("precisionMicroseconds") LocalDateTime dateTime) {

			Statistics.label("Microseconds x--")
					  .collect((dateTime.getNano() % 1_000_000) / 100_000)
					  .coverage(this::check10Coverage);

			Statistics.label("Microseconds -x-")
					  .collect(((dateTime.getNano() % 1_000_000) / 10_000) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Microseconds --x")
					  .collect(((dateTime.getNano() % 1_000_000) / 1_000) % 10)
					  .coverage(this::check10Coverage);

		}

		@Property
		void nanoseconds(@ForAll("precisionNanoseconds") LocalDateTime dateTime) {

			Statistics.label("Nanoseconds x--")
					  .collect((dateTime.getNano() % 1_000) / 100)
					  .coverage(this::check10Coverage);

			Statistics.label("Nanoseconds -x-")
					  .collect(((dateTime.getNano() % 1_000) / 10) % 10)
					  .coverage(this::check10Coverage);

			Statistics.label("Nanoseconds --x")
					  .collect((dateTime.getNano() % 1_000) % 10)
					  .coverage(this::check10Coverage);

		}

		private void checkMonthCoverage(StatisticsCoverage coverage) {
			Month[] months = Month.class.getEnumConstants();
			for (Month m : months) {
				coverage.check(m).percentage(p -> p >= 4);
			}
		}

		private void checkDayOfMonthCoverage(StatisticsCoverage coverage) {
			for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
				coverage.check(dayOfMonth).percentage(p -> p >= 0.5);
			}
		}

		private void checkDayOfWeekCoverage(StatisticsCoverage coverage) {
			DayOfWeek[] dayOfWeeks = DayOfWeek.class.getEnumConstants();
			for (DayOfWeek dayOfWeek : dayOfWeeks) {
				coverage.check(dayOfWeek).percentage(p -> p >= 9);
			}
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

		@Property
		void atTheEarliestYearMustNotBelow1(
				@ForAll @IntRange(min = Year.MIN_VALUE, max = 0) int year,
				@ForAll @LeapYears(withLeapYears = false) LocalDate date,
				@ForAll LocalTime time
		) {
			date = date.withYear(year);
			LocalDateTime dateTime = LocalDateTime.of(date, time);
			System.out.println(dateTime);
			assertThatThrownBy(
					() -> DateTimes.dateTimes().atTheEarliest(dateTime)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void atTheEarliestMinAfterMax(@ForAll("dateTimes") LocalDateTime min, @ForAll("dateTimes") LocalDateTime max) {
			Assume.that(min.isAfter(max));
			assertThatThrownBy(
					() -> DateTimes.dateTimes().atTheLatest(max).atTheEarliest(min)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void atTheLatestYearMustNotBelow1(
				@ForAll @IntRange(min = Year.MIN_VALUE, max = 0) int year,
				@ForAll @LeapYears(withLeapYears = false) LocalDate date,
				@ForAll LocalTime time
		) {
			date = date.withYear(year);
			LocalDateTime dateTime = LocalDateTime.of(date, time);
			System.out.println(dateTime);
			assertThatThrownBy(
					() -> DateTimes.dateTimes().atTheLatest(dateTime)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void atTheLatestMinAfterMax(@ForAll("dateTimes") LocalDateTime min, @ForAll("dateTimes") LocalDateTime max) {
			Assume.that(min.isAfter(max));
			assertThatThrownBy(
					() -> DateTimes.dateTimes().atTheEarliest(min).atTheLatest(max)
			).isInstanceOf(IllegalArgumentException.class);
		}

	}

}
