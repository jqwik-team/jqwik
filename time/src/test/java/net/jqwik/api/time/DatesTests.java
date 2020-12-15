package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
class DatesTests {

	@Property
	void validLocalDateIsGenerated(@ForAll("dates") LocalDate localDate) {
		assertThat(localDate).isNotNull();
	}

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates();
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

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(year, year);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getYear()).isEqualTo(year);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> years() {
				return Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear());
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
			void monthOnlyMonths(@ForAll("monthsOnlyMonths") Month[] months, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().onlyMonths(months);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getMonth()).isIn(months);
					return true;
				});

			}

			@Provide
			Arbitrary<Month[]> monthsOnlyMonths() {
				return MonthTests.generateMonths();
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
			void onlyDaysOfWeek(@ForAll("onlyDayOfWeeks") DayOfWeek[] dayOfWeeks, @ForAll Random random) {

				Arbitrary<LocalDate> dates = Dates.dates().onlyDaysOfWeek(dayOfWeeks);

				assertAllGenerated(dates.generator(1000), random, date -> {
					assertThat(date.getDayOfWeek()).isIn(dayOfWeeks);
					return true;
				});
			}

			@Provide
			Arbitrary<DayOfWeek[]> onlyDayOfWeeks() {
				return generateDayOfWeeks();
			}

			Arbitrary<DayOfWeek[]> generateDayOfWeeks() {
				Arbitrary<DayOfWeek> dayOfWeekArbitrary = Arbitraries
																  .of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
				Arbitrary<Integer> length = Arbitraries.integers().between(1, 7);
				Arbitrary<List<DayOfWeek>> arbitrary = length.flatMap(depth -> Arbitraries.recursive(
						() -> dayOfWeekArbitrary.map(v -> new ArrayList<>()),
						(v) -> addDayOfWeek(v, dayOfWeekArbitrary),
						depth
				));
				return arbitrary.map(v -> v.toArray(new DayOfWeek[]{}));
			}

			Arbitrary<List<DayOfWeek>> addDayOfWeek(Arbitrary<List<DayOfWeek>> listArbitrary, Arbitrary<DayOfWeek> monthArbitrary) {
				return Combinators.combine(listArbitrary, monthArbitrary).as(this::addToList);
			}

			List<DayOfWeek> addToList(List<DayOfWeek> list, DayOfWeek dayOfWeek) {
				if (!list.contains(dayOfWeek)) {
					list.add(dayOfWeek);
				}
				return list;
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			DateArbitrary dates = Dates.dates();
			LocalDate value = shrinkToMinimal(dates, random);
			assertThat(value).isEqualTo(LocalDate.of(0, Month.JANUARY, 1));
		}

		@Property
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random){
			DateArbitrary dates = Dates.dates();
			TestingFalsifier<LocalDate> falsifier = date -> date.isBefore(LocalDate.of(2013, Month.MAY, 25));
			LocalDate value = shrinkToMinimal(dates, random, falsifier);
			assertThat(value).isEqualTo(LocalDate.of(2013, Month.MAY, 25));
		}

		@Property
		void shrinksToSmallestFailingNegativeValue(@ForAll Random random){
			DateArbitrary dates = Dates.dates();
			TestingFalsifier<LocalDate> falsifier = date -> date.isAfter(LocalDate.of(-2013, Month.MAY, 25));
			LocalDate value = shrinkToMinimal(dates, random, falsifier);
			assertThat(value).isEqualTo(LocalDate.of(-2013, Month.MAY, 25));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Property(tries = 5)
		void between() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator = Dates.dates()
																			  .between(LocalDate.of(42, Month.DECEMBER, 30), LocalDate
																																	 .of(43, Month.JANUARY, 2))
																			  .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(744); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(LocalDate.of(42, Month.DECEMBER, 30), LocalDate.of(42, Month.DECEMBER, 31), LocalDate
																																	  .of(43, Month.JANUARY, 1), LocalDate
																																										 .of(43, Month.JANUARY, 2));
		}

		@Property(tries = 5)
		void onlyMonthsWithSameYearAndDayOfMonth() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator = Dates.dates().yearBetween(1997, 1997).dayOfMonthBetween(17, 17)
																			  .onlyMonths(Month.MARCH, Month.OCTOBER, Month.DECEMBER)
																			  .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(LocalDate.of(1997, Month.MARCH, 17), LocalDate.of(1997, Month.OCTOBER, 17), LocalDate
																																	  .of(1997, Month.DECEMBER, 17));
		}

		@Property(tries = 5)
		void onlyDaysOfWeekWithSameYearAndMonth() {
			Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator = Dates.dates().yearBetween(2020, 2020).monthBetween(12, 12)
																			  .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
																			  .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(372); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(LocalDate.of(2020, Month.DECEMBER, 3), LocalDate.of(2020, Month.DECEMBER, 7), LocalDate
																																		.of(2020, Month.DECEMBER, 10), LocalDate
																																											   .of(2020, Month.DECEMBER, 14), LocalDate
																																																					  .of(2020, Month.DECEMBER, 17), LocalDate
																																																															 .of(2020, Month.DECEMBER, 21), LocalDate
																																																																									.of(2020, Month.DECEMBER, 24), LocalDate
																																																																																		   .of(2020, Month.DECEMBER, 28), LocalDate
																																																																																												  .of(2020, Month.DECEMBER, 31));
		}

	}

}
