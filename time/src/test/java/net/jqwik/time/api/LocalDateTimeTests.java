package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class LocalDateTimeTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
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
	class SimpleAnnotations {

		@Disabled("Not available at the moment.")
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

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
			LocalDateTime value = falsifyThenShrink(dateTimes, random);
			assertThat(value).isEqualTo(LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0, 0));
		}

		@Disabled("Not working at the moment.")
		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
			TestingFalsifier<LocalDateTime> falsifier = dateTime -> dateTime.isBefore(LocalDateTime.of(2013, Month.MAY, 25, 13, 12, 55));
			LocalDateTime value = falsifyThenShrink(dateTimes, random, falsifier);
			assertThat(value).isEqualTo(LocalDateTime.of(2013, Month.MAY, 25, 13, 12, 55));
		}

	}

	@Group
	class ExhaustiveGeneration {

		//TODO

	}

	@Group
	class EdgeCasesTests {

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

		//TODO

	}

}
