package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class PeriodTests {

	@Provide
	Arbitrary<Period> periods() {
		return Dates.periods();
	}

	@Group
	class DefaultGeneration {

		@Property
		void validPeriodIsGenerated(@ForAll("periods") Period period) {
			assertThat(period).isNotNull();
		}

		@Property
		void defaultMonthBetween0And11(@ForAll("periods") Period period) {
			assertThat(period.getMonths()).isBetween(0, 11);
		}

		@Property
		void defaultDaysBetween0And30(@ForAll("periods") Period period) {
			assertThat(period.getMonths()).isBetween(0, 30);
		}

	}

	@Property
	void validPeriodIsGeneratedWithAnnotation(@ForAll Period period) {
		assertThat(period).isNotNull();
	}

	@Group
	class PeriodMethods {

		@Property
		void yearsBetween(@ForAll int start, @ForAll int end, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().yearsBetween(start, end);

			final int start2, end2;
			if (start <= end) {
				start2 = start;
				end2 = end;
			} else {
				start2 = end;
				end2 = start;
			}

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isBetween(start2, end2);
			});

		}

		@Property
		void yearsBetweenSame(@ForAll int start, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().yearsBetween(start, start);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isEqualTo(start);
			});

		}

		@Property
		void monthsBetween(@ForAll int start, @ForAll int end, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().monthsBetween(start, end);

			final int start2, end2;
			if (start <= end) {
				start2 = start;
				end2 = end;
			} else {
				start2 = end;
				end2 = start;
			}

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getMonths()).isBetween(start2, end2);
			});

		}

		@Property
		void monthsBetweenSame(@ForAll int start, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().monthsBetween(start, start);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getMonths()).isEqualTo(start);
			});

		}

		@Property
		void daysBetween(@ForAll int start, @ForAll int end, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().daysBetween(start, end);

			final int start2, end2;
			if (start <= end) {
				start2 = start;
				end2 = end;
			} else {
				start2 = end;
				end2 = start;
			}

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getDays()).isBetween(start2, end2);
			});

		}

		@Property
		void daysBetweenSame(@ForAll int start, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().daysBetween(start, start);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getDays()).isEqualTo(start);
			});

		}

		@Property
		void onlyOnePeriodPossible(@ForAll int year, @ForAll int month, @ForAll int day, @ForAll Random random) {

			Arbitrary<Period> periods = Dates.periods().yearsBetween(year, year).monthsBetween(month, month).daysBetween(day, day);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isEqualTo(year);
				assertThat(period.getMonths()).isEqualTo(month);
				assertThat(period.getDays()).isEqualTo(day);
			});

		}

		@Property
		void periodsWithMaxValueRange(@ForAll("periodsWithMaxValueRangeProvider") Period period) {
			assertThat(period.getYears()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
			assertThat(period.getMonths()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
			assertThat(period.getDays()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
		}

		@Provide
		Arbitrary<Period> periodsWithMaxValueRangeProvider() {
			PeriodArbitrary periodArbitrary = Dates.periods();
			periodArbitrary = periodArbitrary.yearsBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
			periodArbitrary = periodArbitrary.monthsBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
			periodArbitrary = periodArbitrary.daysBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
			return periodArbitrary;
		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			PeriodArbitrary periods = Dates.periods();
			Period value = falsifyThenShrink(periods, random);
			assertThat(value).isEqualTo(Period.of(Integer.MIN_VALUE, 0, 0));
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			PeriodArbitrary periods = Dates.periods();
			TestingFalsifier<Period> falsifier = period -> comparePeriodsWithMax30DaysAnd11Months(period, Period.of(200, 3, 5)) < 0;
			Period value = falsifyThenShrink(periods, random, falsifier);
			assertThat(value).isEqualTo(Period.of(200, 3, 5));
		}

		private int comparePeriodsWithMax30DaysAnd11Months(Period period1, Period period2) {
			if (period1.getYears() < period2.getYears()) {
				return -1;
			} else if (period2.getYears() < period1.getYears()) {
				return 1;
			}
			if (period1.getMonths() < period2.getMonths()) {
				return -1;
			} else if (period2.getMonths() < period1.getMonths()) {
				return 1;
			}
			if (period1.getDays() < period2.getDays()) {
				return -1;
			} else if (period2.getDays() < period1.getDays()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<Period>> optionalGenerator =
					Dates.periods()
						 .yearsBetween(200, 200)
						 .monthsBetween(5, 5)
						 .daysBetween(20, 24)
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Period> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(5);
			assertThat(generator).containsExactly(
					Period.of(200, 5, 20),
					Period.of(200, 5, 21),
					Period.of(200, 5, 22),
					Period.of(200, 5, 23),
					Period.of(200, 5, 24)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			PeriodArbitrary periods = Dates.periods();
			Set<Period> edgeCases = collectEdgeCases(periods.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Period.of(Integer.MIN_VALUE, 0, 0),
					Period.of(0, 0, 0),
					Period.of(Integer.MAX_VALUE, 11, 30)
			);
		}

		@Example
		void betweenMethods() {
			PeriodArbitrary periods = Dates.periods()
										   .yearsBetween(5, 10)
										   .monthsBetween(Integer.MIN_VALUE, 500)
										   .daysBetween(-5000, Integer.MAX_VALUE);
			Set<Period> edgeCases = collectEdgeCases(periods.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Period.of(5, Integer.MIN_VALUE, -5000),
					Period.of(10, 500, Integer.MAX_VALUE)
			);
		}

		@Example
		void periodsWithMaxValueRange() {
			PeriodArbitrary periods = Dates.periods()
										   .yearsBetween(Integer.MIN_VALUE, Integer.MAX_VALUE)
										   .monthsBetween(Integer.MIN_VALUE, Integer.MAX_VALUE)
										   .daysBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
			Set<Period> edgeCases = collectEdgeCases(periods.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Period.of(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
					Period.of(0, 0, 0),
					Period.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
			);
		}

	}

	@Group
	class CheckEqualDistribution {

		@Property
		void months(@ForAll("periods") Period period) {
			Statistics.label("Months")
					  .collect(period.getMonths())
					  .coverage(this::checkMonthCoverage);
		}

		@Property
		void dayOfMonths(@ForAll("periods") Period period) {
			Statistics.label("Days")
					  .collect(period.getDays())
					  .coverage(this::checkDayCoverage);
		}

		@Property
		void periodCanBeZeroAndNot(@ForAll Period period) {
			Statistics.label("Period is zero")
					  .collect(period.isZero())
					  .coverage(coverage -> {
						  coverage.check(true).count(c -> c >= 1);
						  coverage.check(false).count(c -> c >= 900);
					  });
		}

		@Property
		void periodCanBePositiveAndNegative(@ForAll Period period) {
			Statistics.label("Period is negative")
					  .collect(period.isNegative())
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p >= 40);
						  coverage.check(false).percentage(p -> p >= 40);
					  });
		}

		private void checkMonthCoverage(StatisticsCoverage coverage) {
			for (int dayOfMonth = 0; dayOfMonth <= 11; dayOfMonth++) {
				coverage.check(dayOfMonth).percentage(p -> p >= 4);
			}
		}

		private void checkDayCoverage(StatisticsCoverage coverage) {
			for (int dayOfMonth = 0; dayOfMonth <= 30; dayOfMonth++) {
				coverage.check(dayOfMonth).percentage(p -> p >= 0.5);
			}
		}

	}

}
