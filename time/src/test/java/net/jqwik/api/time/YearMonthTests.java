package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
class YearMonthTests {

	@Property
	void validYearMonthIsGenerated(@ForAll("yearMonths") YearMonth yearMonth) {
		assertThat(yearMonth).isNotNull();
	}

	@Provide
	Arbitrary<YearMonth> yearMonths() {
		return Dates.yearMonths();
	}

	@Group
	class CheckYearMonthMethods {

		@Group
		class YearMonthMethods {

			@Property
			void atTheEarliest(@ForAll("yearMonths") YearMonth yearMonth, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheEarliest(yearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isGreaterThanOrEqualTo(yearMonth);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("yearMonths") YearMonth yearMonth, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheLatest(yearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isLessThanOrEqualTo(yearMonth);
					return true;
				});

			}

			@Property
			void between(
					@ForAll("yearMonths") YearMonth startYearMonth,
					@ForAll("yearMonths") YearMonth endYearMonth,
					@ForAll Random random
			) {

				Assume.that(!startYearMonth.isAfter(endYearMonth));

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().between(startYearMonth, endYearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isGreaterThanOrEqualTo(startYearMonth);
					assertThat(ym).isLessThanOrEqualTo(endYearMonth);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("yearMonths") YearMonth yearMonth, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().between(yearMonth, yearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isEqualTo(yearMonth);
					return true;
				});

			}

		}

		@Group
		class YearMethods {

			@Property
			void yearBetween(@ForAll("years") int startYear, @ForAll("years") int endYear, @ForAll Random random) {

				Assume.that(startYear <= endYear);

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearBetween(startYear, endYear);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getYear()).isGreaterThanOrEqualTo(startYear);
					assertThat(ym.getYear()).isLessThanOrEqualTo(endYear);
					return true;
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year, @ForAll Random random) {

				Assume.that(year != 0);

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearBetween(year, year);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getYear()).isEqualTo(year);
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

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(startMonth, endMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isGreaterThanOrEqualTo(Month.of(startMonth));
					assertThat(ym.getMonth()).isLessThanOrEqualTo(Month.of(endMonth));
					return true;
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(month, month);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().onlyMonths(months.toArray(new Month[]{}));

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isIn(months);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> months() {
				return Arbitraries.integers().between(1, 12);
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			YearMonthArbitrary yearMonths = Dates.yearMonths();
			YearMonth value = shrinkToMinimal(yearMonths, random);
			assertThat(value).isEqualTo(YearMonth.of(1900, Month.JANUARY));
		}

		@Property
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			YearMonthArbitrary yearMonths = Dates.yearMonths();
			TestingFalsifier<YearMonth> falsifier = ym -> ym.isBefore(YearMonth.of(2013, Month.MAY));
			YearMonth value = shrinkToMinimal(yearMonths, random, falsifier);
			assertThat(value).isEqualTo(YearMonth.of(2013, Month.MAY));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<YearMonth>> optionalGenerator =
					Dates.yearMonths()
						 .between(YearMonth.of(41, Month.OCTOBER), YearMonth.of(42, Month.FEBRUARY))
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<YearMonth> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(5); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					YearMonth.of(41, Month.OCTOBER),
					YearMonth.of(41, Month.NOVEMBER),
					YearMonth.of(41, Month.DECEMBER),
					YearMonth.of(42, Month.JANUARY),
					YearMonth.of(42, Month.FEBRUARY)
			);
		}

		@Example
		void onlyMonthsWithSameYear() {
			Optional<ExhaustiveGenerator<YearMonth>> optionalGenerator = Dates.yearMonths().yearBetween(42, 42)
																			  .onlyMonths(Month.FEBRUARY, Month.MARCH, Month.SEPTEMBER)
																			  .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<YearMonth> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12); // Cannot know the number of filtered elements in advance
			assertThat(generator).containsExactly(
					YearMonth.of(42, Month.FEBRUARY),
					YearMonth.of(42, Month.MARCH),
					YearMonth.of(42, Month.SEPTEMBER)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			YearMonthArbitrary yearMonths = Dates.yearMonths();
			Set<YearMonth> edgeCases = collectEdgeCases(yearMonths.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					YearMonth.of(1900, Month.JANUARY),
					YearMonth.of(2500, Month.DECEMBER)
			);
		}

		@Example
		void between() {

			YearMonthArbitrary yearMonths =
					Dates.yearMonths().between(YearMonth.of(100, Month.MARCH), YearMonth.of(200, Month.OCTOBER));
			Set<YearMonth> edgeCases = collectEdgeCases(yearMonths.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					YearMonth.of(100, Month.MARCH),
					YearMonth.of(200, Month.OCTOBER)
			);

		}
	}

	@Group
	class InvalidConfigurations {

		@Example
		void minYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.yearMonths().yearBetween(0, 2000)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.yearMonths().yearBetween(-1000, 2000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxYearMustNotBeBelow1() {
			assertThatThrownBy(
					() -> Dates.yearMonths().yearBetween(2000, 0)
			).isInstanceOf(IllegalArgumentException.class);

			assertThatThrownBy(
					() -> Dates.yearMonths().yearBetween(2000, -1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

	}
}
