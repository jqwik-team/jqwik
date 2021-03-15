package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.statistics.StatisticsReport.StatisticsReportMode.*;
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
		void defaultYearBetweenMinus1000And1000(@ForAll("periods") Period period) {
			assertThat(period.getYears()).isBetween(-1000, 1000);
		}

		@Property
		void defaultMonthBetweenMinus11And11(@ForAll("periods") Period period) {
			assertThat(period.getMonths()).isBetween(-11, 11);
		}

		@Property
		void defaultDaysBetweenMinus30And30(@ForAll("periods") Period period) {
			assertThat(period.getMonths()).isBetween(-30, 30);
		}

	}

	@Property
	void validPeriodIsGeneratedWithAnnotation(@ForAll Period period) {
		assertThat(period).isNotNull();
	}

	@Group
	class PeriodMethods {

		@Property
		void between(@ForAll int start, @ForAll int end, @ForAll Random random) {
			Assume.that(start <= end);

			Arbitrary<Period> periods = Dates.periods().between(Period.ofYears(start), Period.ofYears(end));

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isBetween(start, end);
			});

		}

		@Property
		void betweenStartPeriodAfterEndPeriod(@ForAll int start, @ForAll int end, @ForAll Random random) {
			Assume.that(start > end);

			Arbitrary<Period> periods = Dates.periods().between(Period.ofYears(start), Period.ofYears(end));

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isBetween(end, start);
			});

		}

		@Example
		void test() {
			onlyOnePeriodPossible(0, 0, 1, new Random(42L));
		}

		@Property
		void onlyOnePeriodPossible(
				@ForAll @IntRange(min = 0, max = Integer.MAX_VALUE) int year,
				@ForAll @IntRange(min = 0, max = 11) int month,
				@ForAll @IntRange(min = 0, max = 30) int day,
				@ForAll Random random
		) {

			Period minMax = Period.of(year, month, day);
			Arbitrary<Period> periods = Dates.periods().between(minMax, minMax);

			assertAllGenerated(periods.generator(1000), random, period -> {
				assertThat(period.getYears()).isEqualTo(year);
				assertThat(period.getMonths()).isEqualTo(month);
				assertThat(period.getDays()).isEqualTo(day);
			});

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			PeriodArbitrary periods = Dates.periods();
			Period value = falsifyThenShrink(periods, random);
			assertThat(value).isEqualTo(Period.of(0, 0, 0));
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
					Dates.periods().between(Period.of(200, 5, 20), Period.of(200, 5, 24))
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
	class EdgeCasesGeneration {

		@Example
		void defaultEdgeCases() {
			PeriodArbitrary periods = Dates.periods();
			Set<Period> edgeCases = collectEdgeCaseValues(periods.edgeCases());
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Period.of(-1000, 0, 0),
					Period.of(0, 0, 0),
					Period.of(1000, 0, 0)
			);
		}

		@Example
		void betweenEdgeCases() {
			PeriodArbitrary periods = Dates.periods().between(
					Period.ofDays(15), Period.ofMonths(3)
			);

			Set<Period> edgeCases = collectEdgeCaseValues(periods.edgeCases());
			assertThat(edgeCases).containsExactlyInAnyOrder(
					Period.of(0, 0, 15),
					Period.of(0, 3, 0)
			);
		}

	}

	@Group
	@StatisticsReport(OFF)
	@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
	class CheckDistribution {

		@Property
		void dayOfMonths(@ForAll("periods") Period period) {
			Statistics.label("Days")
					  .collect(period.getDays())
					  .coverage(this::checkDayCoverage);
		}

		@Property
		void months(@ForAll("periods") Period period) {
			Statistics.label("Months")
					  .collect(period.getMonths())
					  .coverage(this::checkMonthCoverage);
		}

		@Property(tries = 2000) // Zero is rare when edge cases are turned off
		void periodCanBeZeroAndNotZero(@ForAll Period period) {
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
			for (int dayOfMonth = -11; dayOfMonth <= 11; dayOfMonth++) {
				coverage.check(dayOfMonth).percentage(p -> p >= 1);
			}
		}

		private void checkDayCoverage(StatisticsCoverage coverage) {
			for (int dayOfMonth = -30; dayOfMonth <= 30; dayOfMonth++) {
				coverage.check(dayOfMonth).percentage(p -> p >= 0.10);
			}
		}

	}

}
