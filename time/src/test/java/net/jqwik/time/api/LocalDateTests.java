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
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class LocalDateTests {

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
		void validDayOfMonthIsGenerated(@ForAll @DayOfMonthRange int dayOfMonth) {
			assertThat(dayOfMonth).isBetween(1, 31);
		}

		@Property
		void validDayOfMonthIsGeneratedInteger(@ForAll @DayOfMonthRange Integer dayOfMonth) {
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

	}

	@Group
	class CheckDateMethods {

		@Group
		class DateMethods {

			@Property
			void atTheEarliest(@ForAll("dates") LocalDate startDate, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void atTheEarliestAtTheLatestMinAfterMax(
				@ForAll("dates") LocalDate startDate,
				@ForAll("dates") LocalDate endDate,
				@ForAll Random random
			) {

				Assume.that(startDate.isAfter(endDate));

				Arbitrary<LocalDate> dates = Dates.dates().atTheEarliest(startDate).atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(endDate);
					assertThat(date).isBeforeOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("dates") LocalDate endDate, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isBeforeOrEqualTo(endDate);
					return true;
				});

			}

			@Property
			void atTheLatestAtTheEarliestMinAfterMax(
				@ForAll("dates") LocalDate startDate,
				@ForAll("dates") LocalDate endDate,
				@ForAll Random random
			) {

				Assume.that(startDate.isAfter(endDate));

				Arbitrary<LocalDate> dates = Dates.dates().atTheLatest(endDate).atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(endDate);
					assertThat(date).isBeforeOrEqualTo(startDate);
					return true;
				});

			}

			@Property
			void between(@ForAll("dates") LocalDate startDate, @ForAll("dates") LocalDate endDate, @ForAll Random random) {

				Assume.that(!startDate.isAfter(endDate));

				Arbitrary<LocalDate> dates = Dates.dates().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					assertThat(date).isBeforeOrEqualTo(endDate);
					return true;
				});
			}

			@Property
			void betweenEndDateBeforeStartDate(
				@ForAll("dates") LocalDate startDate,
				@ForAll("dates") LocalDate endDate,
				@ForAll Random random
			) {

				Assume.that(startDate.isAfter(endDate));

				Arbitrary<LocalDate> dates = Dates.dates().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isAfterOrEqualTo(endDate);
					assertThat(date).isBeforeOrEqualTo(startDate);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("dates") LocalDate sameDate, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().between(sameDate, sameDate);

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

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(startYear, endYear);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getYear()).isGreaterThanOrEqualTo(startYear);
					assertThat(date.getYear()).isLessThanOrEqualTo(endYear);
					return true;
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(year, year);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
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

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getMonth()).isGreaterThanOrEqualTo(of(startMonth));
					assertThat(date.getMonth()).isLessThanOrEqualTo(of(endMonth));
					return true;
				});

			}

			@Property
			void monthBetweenMinAfterMax(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll Random random) {

				Assume.that(startMonth > endMonth);

				Arbitrary<LocalDate> dates = Dates.dates().monthBetween(startMonth, endMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getMonth()).isGreaterThanOrEqualTo(of(endMonth));
					assertThat(date.getMonth()).isLessThanOrEqualTo(of(startMonth));
					return true;
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().monthBetween(month, month);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getMonth()).isEqualTo(of(month));
					return true;
				});

			}

			@Property
			void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().onlyMonths(months.toArray(new Month[]{}));

				assertAllGenerated(dates.generator(1000, true), random, date -> {
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

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(startDayOfMonth);
					assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(endDayOfMonth);
					return true;
				});

			}

			@Property
			void dayOfMonthBetweenStartAfterEnd(
				@ForAll("dayOfMonths") int startDayOfMonth,
				@ForAll("dayOfMonths") int endDayOfMonth,
				@ForAll Random random
			) {

				Assume.that(startDayOfMonth > endDayOfMonth);

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(endDayOfMonth);
					assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(startDayOfMonth);
					return true;
				});

			}

			@Property
			void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(dayOfMonth, dayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
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

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date.getDayOfWeek()).isIn(dayOfWeeks);
					return true;
				});
			}

		}

		@Group
		class MultipleCombinations {

			@Property
			void onlyFebruary29MoreThan8YearsBetween(
				@ForAll LocalDate min,
				@ForAll LocalDate max,
				@ForAll @IntRange(max = 2) int offset,
				@ForAll Random random
			) {
				Assume.that(max.getYear() - min.getYear() >= 8);

				int minDayOfMonth = 29;
				int maxDayOfMonth = minDayOfMonth + offset;

				Arbitrary<LocalDate> dates = Dates.dates()
												  .between(min, max)
												  .onlyMonths(FEBRUARY)
												  .dayOfMonthBetween(minDayOfMonth, maxDayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isBetween(min, max);
					assertThat(date.getMonth()).isEqualTo(FEBRUARY);
					assertThat(date.getDayOfMonth()).isEqualTo(29);
					return true;
				});

			}

			@Property
			void onlyFebruary29With1To7YearsBetween(
				@ForAll LocalDate min,
				@ForAll MonthDay monthDay,
				@ForAll @IntRange(max = 7) int yearOffset,
				@ForAll @IntRange(max = 2) int dayOfMonthOffset,
				@ForAll Random random
			) {

				Assume.that(
					yearOffset > 0
						|| min.getMonth().compareTo(monthDay.getMonth()) >= 0
						|| min.getDayOfMonth() <= monthDay.getDayOfMonth()
				);
				Assume.that(
					DefaultLocalDateArbitrary.isLeapYear(min.getYear() + yearOffset)
						|| !monthDay.equals(MonthDay.of(FEBRUARY, 29))
				);
				Assume.that(DefaultLocalDateArbitrary.leapYearPossible(min.getYear(), min.getYear() + yearOffset));
				Assume.that(!DefaultLocalDateArbitrary.isLeapYear(min.getYear()) || min.getMonth().compareTo(FEBRUARY) <= 0);
				Assume.that(
					!DefaultLocalDateArbitrary.isLeapYear(min.getYear() + yearOffset)
						|| monthDay.compareTo(MonthDay.of(FEBRUARY, 29)) >= 0
				);

				LocalDate max = LocalDate.of(min.getYear() + yearOffset, monthDay.getMonth(), monthDay.getDayOfMonth());

				int minDayOfMonth = 29;
				int maxDayOfMonth = minDayOfMonth + dayOfMonthOffset;

				Arbitrary<LocalDate> dates = Dates.dates()
												  .between(min, max)
												  .onlyMonths(FEBRUARY)
												  .dayOfMonthBetween(minDayOfMonth, maxDayOfMonth);

				assertAllGenerated(dates.generator(1000, true), random, date -> {
					assertThat(date).isBetween(min, max);
					assertThat(date.getMonth()).isEqualTo(FEBRUARY);
					assertThat(date.getDayOfMonth()).isEqualTo(29);
					return true;
				});

			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			LocalDateArbitrary dates = Dates.dates();
			LocalDate value = falsifyThenShrink(dates, random);
			assertThat(value).isEqualTo(LocalDate.of(1900, JANUARY, 1));
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			LocalDateArbitrary dates = Dates.dates();
			TestingFalsifier<LocalDate> falsifier = date -> date.isBefore(LocalDate.of(2013, MAY, 25));
			LocalDate value = falsifyThenShrink(dates, random, falsifier);
			assertThat(value).isEqualTo(LocalDate.of(2013, MAY, 25));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(42, DECEMBER, 30),
						 LocalDate.of(43, JANUARY, 2)
					 )
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
				LocalDate.of(42, DECEMBER, 30),
				LocalDate.of(42, DECEMBER, 31),
				LocalDate.of(43, JANUARY, 1),
				LocalDate.of(43, JANUARY, 2)
			);
		}

		@Example
		void onlyMonthsWithSameYearAndDayOfMonth() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .yearBetween(1997, 1997)
					 .dayOfMonthBetween(17, 17)
					 .onlyMonths(MARCH, OCTOBER, DECEMBER)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(276); // Cannot know the exact number of filtered elements in advance
			assertThat(generator).containsExactly(
				LocalDate.of(1997, MARCH, 17),
				LocalDate.of(1997, OCTOBER, 17),
				LocalDate.of(1997, DECEMBER, 17)
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
				LocalDate.of(2020, DECEMBER, 3),
				LocalDate.of(2020, DECEMBER, 7),
				LocalDate.of(2020, DECEMBER, 10),
				LocalDate.of(2020, DECEMBER, 14),
				LocalDate.of(2020, DECEMBER, 17),
				LocalDate.of(2020, DECEMBER, 21),
				LocalDate.of(2020, DECEMBER, 24),
				LocalDate.of(2020, DECEMBER, 28),
				LocalDate.of(2020, DECEMBER, 31)
			);
		}

		@Example
		void dayOfMonthBetweenAndBetweenGreater() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .onlyMonths(DECEMBER)
					 .dayOfMonthBetween(27, 28)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(367);
			assertThat(generator).containsExactly(
				LocalDate.of(2010, DECEMBER, 27),
				LocalDate.of(2010, DECEMBER, 28),
				LocalDate.of(2011, DECEMBER, 27),
				LocalDate.of(2011, DECEMBER, 28)
			);
		}

		@Example
		void dayOfMonthBetweenAndBetween() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(2011, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .onlyMonths(JUNE)
					 .dayOfMonthBetween(21, 22)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(368);
			assertThat(generator).containsExactly(
				LocalDate.of(2011, JUNE, 21),
				LocalDate.of(2011, JUNE, 22),
				LocalDate.of(2012, JUNE, 21),
				LocalDate.of(2012, JUNE, 22)
			);
		}

		@Example
		void dayOfMonthBetweenAndBetweenLess() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .onlyMonths(FEBRUARY)
					 .dayOfMonthBetween(12, 13)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(367);
			assertThat(generator).containsExactly(
				LocalDate.of(2011, FEBRUARY, 12),
				LocalDate.of(2011, FEBRUARY, 13),
				LocalDate.of(2012, FEBRUARY, 12),
				LocalDate.of(2012, FEBRUARY, 13)
			);
		}

		@Example
		void monthBetweenAndBetweenGreater() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, SEPTEMBER, 25)
					 )
					 .monthBetween(OCTOBER, NOVEMBER)
					 .dayOfMonthBetween(21, 21)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(397);
			assertThat(generator).containsExactly(
				LocalDate.of(2010, OCTOBER, 21),
				LocalDate.of(2010, NOVEMBER, 21),
				LocalDate.of(2011, OCTOBER, 21),
				LocalDate.of(2011, NOVEMBER, 21)
			);
		}

		@Example
		void monthBetweenAndBetween() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(2011, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .monthBetween(JUNE, JULY)
					 .dayOfMonthBetween(21, 21)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(397);
			assertThat(generator).containsExactly(
				LocalDate.of(2011, JUNE, 21),
				LocalDate.of(2011, JULY, 21),
				LocalDate.of(2012, JUNE, 21),
				LocalDate.of(2012, JULY, 21)
			);
		}

		@Example
		void monthBetweenAndBetweenLess() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .monthBetween(JANUARY, FEBRUARY)
					 .dayOfMonthBetween(20, 20)
					 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(397);
			assertThat(generator).containsExactly(
				LocalDate.of(2011, JANUARY, 20),
				LocalDate.of(2011, FEBRUARY, 20),
				LocalDate.of(2012, JANUARY, 20),
				LocalDate.of(2012, FEBRUARY, 20)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			LocalDateArbitrary dates = Dates.dates();
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(1900, 1, 1),
				LocalDate.of(1904, 2, 29),
				LocalDate.of(2500, 12, 31)
			);
		}

		@Example
		void between() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(LocalDate.of(100, MARCH, 24), LocalDate.of(200, NOVEMBER, 10));
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(100, MARCH, 24),
				LocalDate.of(104, FEBRUARY, 29),
				LocalDate.of(200, NOVEMBER, 10)
			);
		}

		@Example
		void betweenMonth() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .yearBetween(400, 402)
					 .monthBetween(3, 11);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(400, MARCH, 1),
				LocalDate.of(402, NOVEMBER, 30)
			);
		}

		@Example
		void dayOfMonthBetweenAndBetweenGreater() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .onlyMonths(DECEMBER)
					 .dayOfMonthBetween(27, 28);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(2010, DECEMBER, 27),
				LocalDate.of(2011, DECEMBER, 28)
			);
		}

		@Example
		void dayOfMonthBetweenAndBetween() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(
						 LocalDate.of(2011, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .onlyMonths(JUNE)
					 .dayOfMonthBetween(21, 22);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(2011, JUNE, 21),
				LocalDate.of(2012, JUNE, 22)
			);
		}

		@Example
		void dayOfMonthBetweenAndBetweenLess() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .onlyMonths(FEBRUARY)
					 .dayOfMonthBetween(12, 13);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(2011, FEBRUARY, 12),
				LocalDate.of(2012, FEBRUARY, 13)
			);
		}

		@Example
		void monthBetweenAndBetweenGreater() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, SEPTEMBER, 25)
					 )
					 .monthBetween(OCTOBER, NOVEMBER)
					 .dayOfMonthBetween(21, 21);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(2010, OCTOBER, 21),
				LocalDate.of(2011, NOVEMBER, 21)
			);
		}

		@Example
		void monthBetweenAndBetween() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(
						 LocalDate.of(2011, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .monthBetween(JUNE, JULY)
					 .dayOfMonthBetween(21, 21);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(2011, JUNE, 21),
				LocalDate.of(2012, JULY, 21)
			);
		}

		@Example
		void monthBetweenAndBetweenLess() {
			LocalDateArbitrary dates =
				Dates.dates()
					 .between(
						 LocalDate.of(2010, MAY, 19),
						 LocalDate.of(2012, NOVEMBER, 25)
					 )
					 .monthBetween(JANUARY, FEBRUARY)
					 .dayOfMonthBetween(20, 20);
			Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
				LocalDate.of(2011, JANUARY, 20),
				LocalDate.of(2012, FEBRUARY, 20)
			);
		}

	}

	@Group
	@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
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

		@Property
		void leapYears(@ForAll("dates") LocalDate date) {
			Statistics.label("Leap years")
					  .collect(new GregorianCalendar().isLeapYear(date.getYear()))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p >= 20);
						  coverage.check(false).percentage(p -> p >= 65);
					  });
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

		@Property
		void atTheEarliestYearMustNotBelow1(
			@ForAll @IntRange(min = -999_999_999, max = 0) int year,
			@ForAll Month month,
			@ForAll @DayOfMonthRange int day
		) {
			try {
				LocalDate date = LocalDate.of(year, month, day);
				assertThatThrownBy(
					() -> Dates.dates().atTheEarliest(date)
				).isInstanceOf(IllegalArgumentException.class);
			} catch (DateTimeException e) {
				//do nothing
			}
		}

		@Property
		void atTheLatestYearMustNotBelow1(
			@ForAll @IntRange(min = -999_999_999, max = 0) int year,
			@ForAll Month month,
			@ForAll @DayOfMonthRange int day
		) {
			try {
				LocalDate date = LocalDate.of(year, month, day);
				assertThatThrownBy(
					() -> Dates.dates().atTheLatest(date)
				).isInstanceOf(IllegalArgumentException.class);
			} catch (DateTimeException e) {
				//do nothing
			}
		}

		@Example
		void invalidCombinationOfMinMaxValues() {
			assertThatThrownBy(
				() -> Dates.dates()
						   .between(LocalDate.of(2011, 5, 1), LocalDate.of(2012, 2, 1))
						   .monthBetween(3, 4)
						   .dayOfMonthBetween(11, 13)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void monthWithout31DaysButDayOfMonth31(@ForAll("monthWithout31Days") @Size(min = 1) Set<Month> months) {
			assertThatThrownBy(
				() -> Dates.dates()
						   .onlyMonths(months.toArray(new Month[]{}))
						   .dayOfMonthBetween(31, 31)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void februaryButDayOfMonth30Or31(@ForAll @IntRange(max = 1) int offset, @ForAll boolean different) {
			Assume.that(offset == 0 || !different);

			int min = 30 + offset;
			int max = different ? min + 1 : min;

			assertThatThrownBy(
				() -> Dates.dates()
						   .onlyMonths(FEBRUARY)
						   .dayOfMonthBetween(min, max)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property(tries = 2_000, maxDiscardRatio = 10)
		void februaryDayOfMonth29ButNoLeapYear(@ForAll @YearRange Year year, @ForAll @IntRange(max = 2) int offset) {
			Assume.that(year.getValue() % 4 == 1);
			LocalDate minDate = LocalDate.of(year.getValue(), JANUARY, 1);
			LocalDate maxDate = LocalDate.of(year.getValue() + 2, DECEMBER, 31);

			int min = 29;
			int max = min + offset;

			assertThatThrownBy(
				() -> Dates.dates()
						   .between(minDate, maxDate)
						   .onlyMonths(FEBRUARY)
						   .dayOfMonthBetween(min, max)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void betweenAndMonthWithout31DaysButDayOfMonth31(
			@ForAll LocalDate minDate,
			@ForAll MonthDay monthDay,
			@ForAll("monthWithout31Days") @Size(min = 1) Set<Month> months
		) {
			Assume.that(!monthDay.equals(MonthDay.of(FEBRUARY, 29)) || new GregorianCalendar().isLeapYear(minDate.getYear() + 1));

			LocalDate maxDate = LocalDate.of(minDate.getYear() + 1, monthDay.getMonth(), monthDay.getDayOfMonth());

			assertThatThrownBy(
				() -> Dates.dates()
						   .between(minDate, maxDate)
						   .onlyMonths(months.toArray(new Month[]{}))
						   .dayOfMonthBetween(31, 31)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void betweenAndFebruaryButDayOfMonth30Or31(
			@ForAll LocalDate minDate,
			@ForAll MonthDay monthDay,
			@ForAll @IntRange(max = 1) int offset,
			@ForAll boolean different
		) {
			Assume.that(offset == 0 || !different);
			Assume.that(!monthDay.equals(MonthDay.of(FEBRUARY, 29)) || new GregorianCalendar().isLeapYear(minDate.getYear() + 1));

			LocalDate maxDate = LocalDate.of(minDate.getYear() + 1, monthDay.getMonth(), monthDay.getDayOfMonth());
			int min = 30 + offset;
			int max = different ? min + 1 : min;

			assertThatThrownBy(
				() -> Dates.dates()
						   .between(minDate, maxDate)
						   .onlyMonths(FEBRUARY)
						   .dayOfMonthBetween(min, max)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property(tries = 2_000, maxDiscardRatio = 10)
		void betweenAndFebruaryDayOfMonth29ButNoLeapYear(
			@ForAll LocalDate minDate,
			@ForAll MonthDay monthDay,
			@ForAll @IntRange(max = 2) int offset
		) {
			Assume.that(minDate.getYear() % 4 == 1 || minDate.getYear() % 4 == 2);
			Assume.that(!monthDay.equals(MonthDay.of(FEBRUARY, 29)) || new GregorianCalendar().isLeapYear(minDate.getYear() + 1));

			LocalDate maxDate = LocalDate.of(minDate.getYear() + 1, monthDay.getMonth(), monthDay.getDayOfMonth());
			int min = 29;
			int max = min + offset;

			assertThatThrownBy(
				() -> Dates.dates()
						   .between(minDate, maxDate)
						   .onlyMonths(FEBRUARY)
						   .dayOfMonthBetween(min, max)
						   .generator(1)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Provide
		Arbitrary<Set<Month>> monthWithout31Days() {
			Arbitrary<Month> months = Arbitraries.of(FEBRUARY, APRIL, JUNE, SEPTEMBER, NOVEMBER);
			return months.set();
		}

	}

}
