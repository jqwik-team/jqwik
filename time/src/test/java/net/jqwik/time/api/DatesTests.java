package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class DatesTests {

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates();
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validDayOfWeekIsGenerated(@ForAll("daysOfWeek") DayOfWeek dayOfWeek) {
			assertThat(dayOfWeek).isNotNull();
		}

		@Provide
		Arbitrary<DayOfWeek> daysOfWeek() {
			return Dates.daysOfWeek();
		}

		@Property
		void validDayOfMonthIsGenerated(@ForAll("daysOfMonth") int dayOfMonth) {
			assertThat(dayOfMonth).isBetween(1, 31);
		}

		@Provide
		Arbitrary<Integer> daysOfMonth() {
			return Dates.daysOfMonth();
		}

		@Property
		void validMonthIsGenerated(@ForAll("months") Month month) {
			assertThat(month).isNotNull();
		}

		@Provide
		Arbitrary<Month> months() {
			return Dates.months();
		}

		@Property
		void validLocalDateIsGenerated(@ForAll("dates") LocalDate localDate) {
			assertThat(localDate).isNotNull();
		}
	}

	@Group
	class SimpleAnnotations {

		@Property
		void validDayOfWeekIsGenerated(@ForAll DayOfWeek dayOfWeek) {
			assertThat(dayOfWeek).isNotNull();
		}

		@Property
		void validDayOfMonthIsGenerated(@ForAll @DayOfMonth int dayOfMonth) {
			assertThat(dayOfMonth).isBetween(1, 31);
		}

		@Property
		void validMonthIsGenerated(@ForAll Month month) {
			assertThat(month).isNotNull();
		}

		@Property
		void validLocalDateIsGenerated(@ForAll LocalDate localDate) {
			assertThat(localDate).isNotNull();
		}

		@Property
		void validCalendarIsGenerated(@ForAll Calendar calendar) {
			assertThat(calendar).isNotNull();
		}

	}

	@Group
	class CheckDateMethods {

		@Group
		class DateMethods {

			@Property
			void atTheEarliest(@ForAll("dates") LocalDate startDate, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("dates") LocalDate endDate, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date).isBeforeOrEqualTo(endDate);
					return true;
				});

			}

			@Property
			void between(@ForAll("dates") LocalDate startDate, @ForAll("dates") LocalDate endDate, @ForAll Random random) {

				Assume.that(!startDate.isAfter(endDate));

				Arbitrary<LocalDate> dates = Dates.dates().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					assertThat(date).isBeforeOrEqualTo(endDate);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("dates") LocalDate sameDate, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().between(sameDate, sameDate);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date).isEqualTo(sameDate);
					return true;
				});

			}

		}

		@Group
		class YearMethods {

			@Property
			void yearBetween(@ForAll("years") int startYear, @ForAll("years") int endYear, @ForAll Random random) {

				Assume.that(startYear <= endYear);

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(startYear, endYear);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getYear()).isGreaterThanOrEqualTo(startYear);
					assertThat(date.getYear()).isLessThanOrEqualTo(endYear);
					return true;
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year, @ForAll Random random) {

				Assume.that(year != 0);

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(year, year);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getYear()).isEqualTo(year);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> years() {
				return Arbitraries.integers().between(1, LocalDate.MAX.getYear());
			}

		}

		@Group
		class MonthMethods {

			@Property
			void monthBetween(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll Random random) {

				Assume.that(startMonth <= endMonth);

				Arbitrary<LocalDate> dates = Dates.dates().monthBetween(startMonth, endMonth);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getMonth()).isGreaterThanOrEqualTo(Month.of(startMonth));
					assertThat(date.getMonth()).isLessThanOrEqualTo(Month.of(endMonth));
					return true;
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().monthBetween(month, month);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getMonth()).isEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().onlyMonths(months.toArray(new Month[]{}));

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getMonth()).isIn(months);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> months() {
				return Arbitraries.integers().between(1, 12);
			}

		}

		@Group
		class DayOfMonthMethods {

			@Property
			void dayOfMonthBetween(
					@ForAll("dayOfMonths") int startDayOfMonth,
					@ForAll("dayOfMonths") int endDayOfMonth,
					@ForAll Random random
			) {

				Assume.that(startDayOfMonth <= endDayOfMonth);

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(startDayOfMonth);
					assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(endDayOfMonth);
					return true;
				});

			}

			@Property
			void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(dayOfMonth, dayOfMonth);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getDayOfMonth()).isEqualTo(dayOfMonth);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> dayOfMonths() {
				return Arbitraries.integers().between(1, 31);
			}

		}

		@Group
		class OnlyDaysOfWeekMethods {

			@Property
			void onlyDaysOfWeek(@ForAll @Size(min = 1) Set<DayOfWeek> dayOfWeeks, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getDayOfWeek()).isIn(dayOfWeeks);
					return true;
				});
			}

		}

		@Group
		class MapToCalendar {

			@Provide
			Arbitrary<Calendar> calendar() {
				return Dates.dates().asCalendar();
			}

			@Property
			void validCalendarGenerated(@ForAll("calendar") Calendar calendar) {
				assertThat(calendar).isNotNull();
			}

			@Property
			void timeIs0AM(@ForAll("calendar") Calendar calendar) {
				assertThat(calendar.get(Calendar.HOUR)).isEqualTo(0);
				assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(0);
				assertThat(calendar.get(Calendar.SECOND)).isEqualTo(0);
				assertThat(calendar.get(Calendar.MILLISECOND)).isEqualTo(0);
			}

			@Property
			void between(@ForAll("dates") LocalDate startDate, @ForAll("dates") LocalDate endDate, @ForAll Random random) {

				Assume.that(!startDate.isAfter(endDate));

				Arbitrary<Calendar> dates = Dates.dates().between(startDate, endDate).asCalendar();

				assertAllGenerated(dates.generator(1000), random, date -> {
					Calendar startCalendar = Calendar.getInstance();
					startCalendar.set(startDate.getYear(), startDate.getMonth().getValue(), startDate.getDayOfMonth(), 0, 0, 0);
					startCalendar.set(Calendar.MILLISECOND, 0);
					Calendar endCalendar = Calendar.getInstance();
					endCalendar.set(endDate.getYear(), endDate.getMonth().getValue(), endDate.getDayOfMonth(), 0, 0, 0);
					startCalendar.set(Calendar.MILLISECOND, 0);
					assertThat(date).isGreaterThanOrEqualTo(startCalendar);
					assertThat(date).isLessThanOrEqualTo(endCalendar);
					return true;
				});
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			DateArbitrary dates = Dates.dates();
			LocalDate value = falsifyThenShrink(dates, random);
			assertThat(value).isEqualTo(LocalDate.of(1900, Month.JANUARY, 1));
		}

		@Property
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			DateArbitrary dates = Dates.dates();
			TestingFalsifier<LocalDate> falsifier = date -> date.isBefore(LocalDate.of(2013, Month.MAY, 25));
			LocalDate value = falsifyThenShrink(dates, random, falsifier);
			assertThat(value).isEqualTo(LocalDate.of(2013, Month.MAY, 25));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
					Dates.dates()
						 .between(
								 LocalDate.of(42, Month.DECEMBER, 30),
								 LocalDate.of(43, Month.JANUARY, 2)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					LocalDate.of(42, Month.DECEMBER, 30),
					LocalDate.of(42, Month.DECEMBER, 31),
					LocalDate.of(43, Month.JANUARY, 1),
					LocalDate.of(43, Month.JANUARY, 2)
			);
		}

		@Example
		void onlyMonthsWithSameYearAndDayOfMonth() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
					Dates.dates()
						 .yearBetween(1997, 1997)
						 .dayOfMonthBetween(17, 17)
						 .onlyMonths(Month.MARCH, Month.OCTOBER, Month.DECEMBER)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(292); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
					LocalDate.of(1997, Month.MARCH, 17),
					LocalDate.of(1997, Month.OCTOBER, 17),
					LocalDate.of(1997, Month.DECEMBER, 17)
			);
		}

		@Example
		void onlyDaysOfWeekWithSameYearAndMonth() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
					Dates.dates()
						 .yearBetween(2020, 2020)
						 .monthBetween(12, 12)
						 .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(31); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
					LocalDate.of(2020, Month.DECEMBER, 3),
					LocalDate.of(2020, Month.DECEMBER, 7),
					LocalDate.of(2020, Month.DECEMBER, 10),
					LocalDate.of(2020, Month.DECEMBER, 14),
					LocalDate.of(2020, Month.DECEMBER, 17),
					LocalDate.of(2020, Month.DECEMBER, 21),
					LocalDate.of(2020, Month.DECEMBER, 24),
					LocalDate.of(2020, Month.DECEMBER, 28),
					LocalDate.of(2020, Month.DECEMBER, 31)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			DateArbitrary dates = Dates.dates();
			Set<LocalDate> edgeCases = collectEdgeCases(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					LocalDate.of(1900, 1, 1),
					LocalDate.of(1904, 2, 29),
					LocalDate.of(2500, 12, 31)
			);
		}

		@Example
		void between() {
			DateArbitrary dates =
					Dates.dates()
						 .between(LocalDate.of(100, Month.MARCH, 24), LocalDate.of(200, Month.NOVEMBER, 10));
			Set<LocalDate> edgeCases = collectEdgeCases(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					LocalDate.of(100, Month.MARCH, 24),
					LocalDate.of(104, Month.FEBRUARY, 29),
					LocalDate.of(200, Month.NOVEMBER, 10)
			);
		}

		@Example
		void betweenMonth() {
			DateArbitrary dates =
					Dates.dates()
						 .yearBetween(400, 402)
						 .monthBetween(3, 11);
			Set<LocalDate> edgeCases = collectEdgeCases(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					LocalDate.of(400, Month.MARCH, 1),
					LocalDate.of(402, Month.NOVEMBER, 30)
			);
		}

	}

	@Group
	class CheckEqualDistribution {

		@Property
		void months(@ForAll("dates") LocalDate date) {
			Statistics.label("Months")
					  .collect(date.getMonth())
					  .coverage(this::checkMonthCoverage);
		}

		@Property
		void dayOfMonths(@ForAll("dates") LocalDate date) {
			Statistics.label("Day of months")
					  .collect(date.getDayOfMonth())
					  .coverage(this::checkDayOfMonthCoverage);
		}

		@Property
		void dayOfWeeks(@ForAll("dates") LocalDate date) {
			Statistics.label("Day of weeks")
					  .collect(date.getDayOfWeek())
					  .coverage(this::checkDayOfWeekCoverage);
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
				coverage.check(dayOfWeek).percentage(p -> p >= 10);
			}
		}

	}

	@Group
	class InvalidConfigurations {

		@Example
		void minDateMustNotBeBeforeJan1_1() {
			assertThatThrownBy(
					() -> Dates.dates().between(LocalDate.of(0, 12, 31), LocalDate.of(2000, 1, 1))
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void minYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.dates().yearBetween(0, 2000)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.dates().yearBetween(-1000, 2000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.dates().yearBetween(2000, 0)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.dates().yearBetween(2000, -1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void monthsMustBeBetween1And12() {
			assertThatThrownBy(
					() -> Dates.dates().monthBetween(0, 12)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.dates().monthBetween(12, 0)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.dates().monthBetween(1, 13)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.dates().monthBetween(13, 1)
			).isInstanceOf(DateTimeException.class);
		}

		@Example
		void daysMustBeBetween1And31() {
			assertThatThrownBy(
					() -> Dates.dates().monthBetween(0, 31)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.dates().monthBetween(31, 0)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.dates().monthBetween(1, 32)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.dates().monthBetween(32, 1)
			).isInstanceOf(DateTimeException.class);
		}

	}

}
