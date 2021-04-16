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

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class CalendarTests {

	@Provide
	Arbitrary<Calendar> dates() {
		return Dates.datesAsCalendar();
	}

	@Property
	void validCalendarIsGenerated(@ForAll("dates") Calendar calendar) {
		assertThat(calendar).isNotNull();
	}

	@Group
	class SimpleAnnotations {

		@Disabled("Disabled when default generation was removed. Will be enabled when default generation becomes available again.")
		//TODO: Enable when it is available again.
		@Property
		void validCalendarIsGenerated(@ForAll Calendar calendar) {
			assertThat(calendar).isNotNull();
		}

	}

	@Group
	class CheckCalendarMethods {

		@Group
		class CalendarMethods {

			@Property
			void atTheEarliest(@ForAll("dates") Calendar startDate, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isGreaterThanOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void atTheEarliestAtTheLatestMinAfterMax(
				@ForAll("dates") Calendar startDate,
				@ForAll("dates") Calendar endDate,
				@ForAll Random random
			) {

				Assume.that(startDate.after(endDate));

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheEarliest(startDate).atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isGreaterThanOrEqualTo(endDate);
					assertThat(date).isLessThanOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("dates") Calendar endDate, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isLessThanOrEqualTo(endDate);
					return true;
				});

			}

			@Property
			void atTheLatestAtTheEarliestMinAfterMax(
				@ForAll("dates") Calendar startDate,
				@ForAll("dates") Calendar endDate,
				@ForAll Random random
			) {

				Assume.that(startDate.after(endDate));

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheLatest(endDate).atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isGreaterThanOrEqualTo(endDate);
					assertThat(date).isLessThanOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void between(@ForAll("dates") Calendar startDate, @ForAll("dates") Calendar endDate, @ForAll Random random) {

				Assume.that(!startDate.after(endDate));

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isGreaterThanOrEqualTo(startDate);
					assertThat(date).isLessThanOrEqualTo(endDate);
					return true;
				});
			}

			@Property
			void betweenEndDateAfterStartDate(
					@ForAll("dates") Calendar startDate,
					@ForAll("dates") Calendar endDate,
					@ForAll Random random
			) {

				Assume.that(startDate.after(endDate));

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isGreaterThanOrEqualTo(endDate);
					assertThat(date).isLessThanOrEqualTo(startDate);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("dates") Calendar sameDate, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().between(sameDate, sameDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
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

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().yearBetween(startYear, endYear);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.get(Calendar.YEAR)).isGreaterThanOrEqualTo(startYear);
					assertThat(date.get(Calendar.YEAR)).isLessThanOrEqualTo(endYear);
					return true;
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().yearBetween(year, year);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.get(Calendar.YEAR)).isEqualTo(year);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> years() {
				return Arbitraries.integers().between(1, 292_278_993); //Maximum Calendar.YEAR value is 292_278_993
			}

		}

		@Group
		class MonthMethods {

			@Property
			void monthBetween(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll Random random) {

				Assume.that(startMonth <= endMonth);

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().monthBetween(startMonth, endMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isGreaterThanOrEqualTo(Month.of(startMonth));
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isLessThanOrEqualTo(Month.of(endMonth));
					return true;
				});

			}

			@Property
			void monthBetweenMinAfterMax(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll Random random) {

				Assume.that(startMonth > endMonth);

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().monthBetween(startMonth, endMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isGreaterThanOrEqualTo(Month.of(endMonth));
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isLessThanOrEqualTo(Month.of(startMonth));
					return true;
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().monthBetween(month, month);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().onlyMonths(months.toArray(new Month[]{}));

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isIn(months);
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

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(startDayOfMonth);
					assertThat(date.get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(endDayOfMonth);
					return true;
				});

			}

			@Property
			void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll Random random) {

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().dayOfMonthBetween(dayOfMonth, dayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.get(Calendar.DAY_OF_MONTH)).isEqualTo(dayOfMonth);
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

				Arbitrary<Calendar> dates = Dates.datesAsCalendar().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(date)).isIn(dayOfWeeks);
					return true;
				});
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			CalendarArbitrary dates = Dates.datesAsCalendar();
			Calendar value = falsifyThenShrink(dates, random);
			assertThat(value.get(Calendar.YEAR)).isEqualTo(1900);
			assertThat(value.get(Calendar.MONTH)).isEqualTo(Calendar.JANUARY);
			assertThat(value.get(Calendar.DAY_OF_MONTH)).isEqualTo(1);
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			CalendarArbitrary dates = Dates.datesAsCalendar();
			Calendar calendar = getCalendar(2013, Calendar.MAY, 24);
			TestingFalsifier<Calendar> falsifier = date -> !date.after(calendar);
			Calendar value = falsifyThenShrink(dates, random, falsifier);
			assertThat(value.get(Calendar.YEAR)).isEqualTo(2013);
			assertThat(value.get(Calendar.MONTH)).isEqualTo(Calendar.MAY);
			assertThat(value.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<Calendar>> optionalGenerator =
					Dates.datesAsCalendar()
						 .between(
								 getCalendar(42, Calendar.DECEMBER, 30),
								 getCalendar(43, Calendar.JANUARY, 2)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Calendar> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					getCalendar(42, Calendar.DECEMBER, 30),
					getCalendar(42, Calendar.DECEMBER, 31),
					getCalendar(43, Calendar.JANUARY, 1),
					getCalendar(43, Calendar.JANUARY, 2)
			);
		}

		@Example
		void onlyMonthsWithSameYearAndDayOfMonth() {
			Optional<ExhaustiveGenerator<Calendar>> optionalGenerator =
				Dates.datesAsCalendar()
					 .yearBetween(1997, 1997)
					 .dayOfMonthBetween(17, 17)
					 .onlyMonths(Month.MARCH, Month.OCTOBER, Month.DECEMBER)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Calendar> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(276); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
				getCalendar(1997, Calendar.MARCH, 17),
				getCalendar(1997, Calendar.OCTOBER, 17),
				getCalendar(1997, Calendar.DECEMBER, 17)
			);
		}

		@Example
		void onlyDaysOfWeekWithSameYearAndMonth() {
			Optional<ExhaustiveGenerator<Calendar>> optionalGenerator =
					Dates.datesAsCalendar()
						 .yearBetween(2020, 2020)
						 .monthBetween(12, 12)
						 .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Calendar> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(31); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
					getCalendar(2020, Calendar.DECEMBER, 3),
					getCalendar(2020, Calendar.DECEMBER, 7),
					getCalendar(2020, Calendar.DECEMBER, 10),
					getCalendar(2020, Calendar.DECEMBER, 14),
					getCalendar(2020, Calendar.DECEMBER, 17),
					getCalendar(2020, Calendar.DECEMBER, 21),
					getCalendar(2020, Calendar.DECEMBER, 24),
					getCalendar(2020, Calendar.DECEMBER, 28),
					getCalendar(2020, Calendar.DECEMBER, 31)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			CalendarArbitrary dates = Dates.datesAsCalendar();
			Set<Calendar> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					getCalendar(1900, Calendar.JANUARY, 1),
					getCalendar(1904, Calendar.FEBRUARY, 29),
					getCalendar(2500, Calendar.DECEMBER, 31)
			);
		}

		@Example
		void between() {
			CalendarArbitrary dates =
					Dates.datesAsCalendar()
						 .between(getCalendar(100, Calendar.MARCH, 24), getCalendar(200, Calendar.NOVEMBER, 10));
			Set<Calendar> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					getCalendar(100, Calendar.MARCH, 24),
					getCalendar(104, Calendar.FEBRUARY, 29),
					getCalendar(200, Calendar.NOVEMBER, 10)
			);
		}

		@Example
		void betweenMonth() {
			CalendarArbitrary dates =
					Dates.datesAsCalendar()
						 .yearBetween(400, 402)
						 .monthBetween(3, 11);
			Set<Calendar> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					getCalendar(400, Calendar.MARCH, 1),
					getCalendar(402, Calendar.NOVEMBER, 30)
			);
		}

	}

	@Group
	@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
	class CheckEqualDistribution {

		@Property
		void months(@ForAll("dates") Calendar date) {
			Statistics.label("Months")
					  .collect(date.get(Calendar.MONTH))
					  .coverage(this::checkMonthCoverage);
		}

		@Property
		void dayOfMonths(@ForAll("dates") Calendar date) {
			Statistics.label("Day of months")
					  .collect(date.get(Calendar.DAY_OF_MONTH))
					  .coverage(this::checkDayOfMonthCoverage);
		}

		@Property
		void dayOfWeeks(@ForAll("dates") Calendar date) {
			Statistics.label("Day of weeks")
					  .collect(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(date))
					  .coverage(this::checkDayOfWeekCoverage);
		}

		@Property
		void leapYears(@ForAll("dates") Calendar date) {
			Statistics.label("Leap years")
					  .collect(new GregorianCalendar().isLeapYear(date.get(Calendar.YEAR)))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p >= 20);
						  coverage.check(false).percentage(p -> p >= 65);
					  });
		}

		private void checkMonthCoverage(StatisticsCoverage coverage) {
			Month[] months = Month.class.getEnumConstants();
			for (Month m : months) {
				coverage.check(DefaultCalendarArbitrary.monthToCalendarMonth(m)).percentage(p -> p >= 4);
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

	}

	@Group
	class InvalidConfigurations {

		@Property
		void atTheEarliestYearMustNotBeBelow1(
				@ForAll @IntRange(min = -100_000, max = 0) int year,
				@ForAll Month month,
				@ForAll @DayOfMonthRange int day
		) {
			Calendar calendar = new Calendar.Builder().setDate(year, DefaultCalendarArbitrary.monthToCalendarMonth(month), day).build();
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().atTheEarliest(calendar)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void atTheEarliestYearTooHigh() {
			Calendar calendar = new Calendar.Builder().setDate(292_278_994, 1, 1).build();
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().atTheEarliest(calendar)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void atTheLatestYearMustNotBeBelow1(
				@ForAll @IntRange(min = -100_000, max = 0) int year,
				@ForAll Month month,
				@ForAll @DayOfMonthRange int day
		) {
			Calendar calendar = new Calendar.Builder().setDate(year, DefaultCalendarArbitrary.monthToCalendarMonth(month), day).build();
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().atTheLatest(calendar)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void atTheLatestYearTooHigh() {
			Calendar calendar = new Calendar.Builder().setDate(292_278_994, 1, 1).build();
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().atTheLatest(calendar)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void minYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(0, 2000)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(-1000, 2000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void minYearMustNotBeOver292278993() {
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(292_278_994, 2000)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(Year.MAX_VALUE, 2000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(2000, 0)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(2000, -1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxYearMustNotBeOver292278993() {
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(2000, 292_278_994)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().yearBetween(2000, Year.MAX_VALUE)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void monthsMustBeBetween1And12() {
			assertThatThrownBy(
					() -> Dates.datesAsCalendar().monthBetween(0, 12)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().monthBetween(12, 0)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().monthBetween(1, 13)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsCalendar().monthBetween(13, 1)
			).isInstanceOf(DateTimeException.class);
		}

	}

	public static Calendar getCalendar(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

}
