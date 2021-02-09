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
class DateTests {

	@Provide
	Arbitrary<Date> dates() {
		return Dates.datesAsDate();
	}

	@Property
	void validDateIsGenerated(@ForAll("dates") Date date) {
		assertThat(date).isNotNull();
	}

	private Calendar dateToCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	@Group
	class SimpleAnnotations {

		@Disabled("Disabled when default generation was removed. Will be enabled when default generation becomes available again.")
		//TODO: Enable when it is available again.
		@Property
		void validDateIsGenerated(@ForAll Date date) {
			assertThat(date).isNotNull();
		}

		@Property
		void noLeapYearIsGenerated(@ForAll("dates") @LeapYears(withLeapYears = false) Date date) {
			assertThat(new GregorianCalendar().isLeapYear(dateToCalendar(date).get(Calendar.YEAR))).isFalse();
		}

	}

	@Group
	class CheckCalendarMethods {

		@Group
		class CalendarMethods {

			@Property
			void atTheEarliest(@ForAll("dates") Date startDate, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("dates") Date endDate, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isBeforeOrEqualTo(endDate);
					return true;
				});

			}

			@Property
			void between(@ForAll("dates") Date startDate, @ForAll("dates") Date endDate, @ForAll Random random) {

				Assume.that(!startDate.after(endDate));

				Arbitrary<Date> dates = Dates.datesAsDate().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					assertThat(date).isBeforeOrEqualTo(endDate);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("dates") Date sameDate, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().between(sameDate, sameDate);

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

				Arbitrary<Date> dates = Dates.datesAsDate().yearBetween(startYear, endYear);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(dateToCalendar(date).get(Calendar.YEAR)).isGreaterThanOrEqualTo(startYear);
					assertThat(dateToCalendar(date).get(Calendar.YEAR)).isLessThanOrEqualTo(endYear);
					return true;
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().yearBetween(year, year);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(dateToCalendar(date).get(Calendar.YEAR)).isEqualTo(year);
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

				Arbitrary<Date> dates = Dates.datesAsDate().monthBetween(startMonth, endMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date)))
							.isGreaterThanOrEqualTo(Month.of(startMonth));
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.of(endMonth));
					return true;
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().monthBetween(month, month);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().onlyMonths(months.toArray(new Month[]{}));

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isIn(months);
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

				Arbitrary<Date> dates = Dates.datesAsDate().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(startDayOfMonth);
					assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(endDayOfMonth);
					return true;
				});

			}

			@Property
			void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll Random random) {

				Arbitrary<Date> dates = Dates.datesAsDate().dayOfMonthBetween(dayOfMonth, dayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isEqualTo(dayOfMonth);
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

				Arbitrary<Date> dates = Dates.datesAsDate().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isIn(dayOfWeeks);
					return true;
				});
			}

		}

		@Group
		class LeapYearMethod {

			@Provide
			DateArbitrary noLeapYears() {
				return Dates.datesAsDate().leapYears(false);
			}

			@Property
			void yearIsNotALeapYear(@ForAll("noLeapYears") Date date) {
				assertThat(new GregorianCalendar().isLeapYear(dateToCalendar(date).get(Calendar.YEAR))).isFalse();
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			DateArbitrary dates = Dates.datesAsDate();
			Calendar value = dateToCalendar(falsifyThenShrink(dates, random));
			assertThat(value.get(Calendar.YEAR)).isEqualTo(1900);
			assertThat(value.get(Calendar.MONTH)).isEqualTo(Calendar.JANUARY);
			assertThat(value.get(Calendar.DAY_OF_MONTH)).isEqualTo(1);
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			DateArbitrary dates = Dates.datesAsDate();
			Calendar calendar = getCalendar(2013, Calendar.MAY, 24);
			TestingFalsifier<Date> falsifier = date -> !dateToCalendar(date).after(calendar);
			Calendar value = dateToCalendar(falsifyThenShrink(dates, random, falsifier));
			assertThat(value.get(Calendar.YEAR)).isEqualTo(2013);
			assertThat(value.get(Calendar.MONTH)).isEqualTo(Calendar.MAY);
			assertThat(value.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<Date>> optionalGenerator =
					Dates.datesAsDate()
						 .between(
								 getDate(42, Calendar.DECEMBER, 30),
								 getDate(43, Calendar.JANUARY, 2)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Date> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					getDate(42, Calendar.DECEMBER, 30),
					getDate(42, Calendar.DECEMBER, 31),
					getDate(43, Calendar.JANUARY, 1),
					getDate(43, Calendar.JANUARY, 2)
			);
		}

		@Example
		void onlyMonthsWithSameYearAndDayOfMonth() {
			Optional<ExhaustiveGenerator<Date>> optionalGenerator =
					Dates.datesAsDate()
						 .yearBetween(1997, 1997)
						 .dayOfMonthBetween(17, 17)
						 .onlyMonths(Month.MARCH, Month.OCTOBER, Month.DECEMBER)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Date> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(292); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
					getDate(1997, Calendar.MARCH, 17),
					getDate(1997, Calendar.OCTOBER, 17),
					getDate(1997, Calendar.DECEMBER, 17)
			);
		}

		@Example
		void onlyDaysOfWeekWithSameYearAndMonth() {
			Optional<ExhaustiveGenerator<Date>> optionalGenerator =
					Dates.datesAsDate()
						 .yearBetween(2020, 2020)
						 .monthBetween(12, 12)
						 .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Date> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(31); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
					getDate(2020, Calendar.DECEMBER, 3),
					getDate(2020, Calendar.DECEMBER, 7),
					getDate(2020, Calendar.DECEMBER, 10),
					getDate(2020, Calendar.DECEMBER, 14),
					getDate(2020, Calendar.DECEMBER, 17),
					getDate(2020, Calendar.DECEMBER, 21),
					getDate(2020, Calendar.DECEMBER, 24),
					getDate(2020, Calendar.DECEMBER, 28),
					getDate(2020, Calendar.DECEMBER, 31)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			DateArbitrary dates = Dates.datesAsDate();
			Set<Date> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					getDate(1900, Calendar.JANUARY, 1),
					getDate(1904, Calendar.FEBRUARY, 29),
					getDate(2500, Calendar.DECEMBER, 31)
			);
		}

		@Example
		void between() {
			DateArbitrary dates =
					Dates.datesAsDate()
						 .between(getDate(100, Calendar.MARCH, 24), getDate(200, Calendar.NOVEMBER, 10));
			Set<Date> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					getDate(100, Calendar.MARCH, 24),
					getDate(104, Calendar.FEBRUARY, 29),
					getDate(200, Calendar.NOVEMBER, 10)
			);
		}

		@Example
		void betweenMonth() {
			DateArbitrary dates =
					Dates.datesAsDate()
						 .yearBetween(400, 402)
						 .monthBetween(3, 11);
			Set<Date> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					getDate(400, Calendar.MARCH, 1),
					getDate(402, Calendar.NOVEMBER, 30)
			);
		}

	}

	@Group
	@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
	class CheckEqualDistribution {

		@Property
		void months(@ForAll("dates") Date date) {
			Statistics.label("Months")
					  .collect(dateToCalendar(date).get(Calendar.MONTH))
					  .coverage(this::checkMonthCoverage);
		}

		@Property
		void dayOfMonths(@ForAll("dates") Date date) {
			Statistics.label("Day of months")
					  .collect(dateToCalendar(date).get(Calendar.DAY_OF_MONTH))
					  .coverage(this::checkDayOfMonthCoverage);
		}

		@Property
		void dayOfWeeks(@ForAll("dates") Date date) {
			Statistics.label("Day of weeks")
					  .collect(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date)))
					  .coverage(this::checkDayOfWeekCoverage);
		}

		@Property
		void leapYears(@ForAll("dates") Date date) {
			Statistics.label("Leap years")
					  .collect(new GregorianCalendar().isLeapYear(dateToCalendar(date).get(Calendar.YEAR)))
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

		@Example
		void minYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(0, 2000)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(-1000, 2000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void minYearMustNotBeOver292278993() {
			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(292_278_994, 2000)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(Year.MAX_VALUE, 2000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(2000, 0)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(2000, -1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxYearMustNotBeOver292278993() {
			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(2000, 292_278_994)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().yearBetween(2000, Year.MAX_VALUE)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void monthsMustBeBetween1And12() {
			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(0, 12)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(12, 0)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(1, 13)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(13, 1)
			).isInstanceOf(DateTimeException.class);
		}

		@Example
		void daysMustBeBetween1And31() {
			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(0, 31)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(31, 0)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(1, 32)
			).isInstanceOf(DateTimeException.class);

			assertThatThrownBy(
					() -> Dates.datesAsDate().monthBetween(32, 1)
			).isInstanceOf(DateTimeException.class);
		}

	}

	private Calendar getCalendar(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	public static Date getDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
