package net.jqwik.api.statistics;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;
import org.assertj.core.data.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.statistics.StatisticsReport.StatisticsReportMode.*;

@Group
class StatisticsCoverageTests {

	@Property(tries = 50)
	@ExpectFailure("coverage check should have failed")
	void moreThanOneCoverageWorks(@ForAll int anInt) {
		Statistics.collect(anInt > 0);

		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(true).count(c -> true));
		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(false).count(c -> false));
	}

	@Property(tries = 50)
	@AddLifecycleHook(CheckCountIsExactly1.class)
	void coverageCheckersAreCalledExactlyOnce(@ForAll int anInt) {
		Statistics.collect(anInt > 0);

		Statistics.coverage(statisticsCoverage -> {
			Store<Integer> count = Store.get("countCheckTrue");
			count.update(i -> i + 1);
			statisticsCoverage.check(true).count(c -> true);
		});
		Statistics.coverage(statisticsCoverage -> {
			Store<Integer> count = Store.get("countCheckFalse");
			count.update(i -> i + 1);
			statisticsCoverage.check(false).count(c -> true);
		});
	}

	static class CheckCountIsExactly1 implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			Store<Integer> countCheckTrue = Store.create("countCheckTrue", Lifespan.PROPERTY, () -> 0);
			Store<Integer> countCheckFalse = Store.create("countCheckFalse", Lifespan.PROPERTY, () -> 0);
			try {
				return property.execute();
			} finally {
				assertThat(countCheckTrue.get()).isEqualTo(1);
				assertThat(countCheckFalse.get()).isEqualTo(1);
			}
		}

		@Override
		public int aroundPropertyProximity() {
			return -90;
		}
	}

	@Group
	class Count {
		@Property(tries = 50)
		@ExpectFailure("coverage check should have failed")
		void coverageFailsForViolatedCountCondition(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> false);
			});
		}

		@Property(tries = 50)
		@ExpectFailure("coverage check should have failed")
		void coverageWorksForLabelledCollectors(@ForAll int anInt) {
			Statistics.label("ints").collect(anInt > 0);

			Statistics.label("ints").coverage(coverage -> {
				coverage.check(true).count(c -> false);
			});
		}

		@Property(tries = 50)
		void countCheckPredicate(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> c > 0);
			});
		}

		@Property(tries = 50)
		void countCheckPredicateWithCountAll(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count((c, a) -> c <= a);
			});
		}

		int countPositive = 0;

		@Property(tries = 50)
		void countCheckAssertion(@ForAll int anInt) {
			boolean isPositive = anInt > 0;
			if (isPositive) {
				countPositive++;
			}
			Statistics.collect(isPositive);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> {
					assertThat(c).isEqualTo(countPositive);
				});
			});
		}

		@Property(tries = 50)
		void countCheckAssertionWithCountAll(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count((c, a) -> {
					assertThat(c).isLessThanOrEqualTo(a);
				});
			});
		}
	}

	@Group
	class Percentage {
		@Property(tries = 50)
		@ExpectFailure("coverage check should have failed")
		void coverageFailsForViolatedPercentageCondition(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).percentage(c -> false);
			});
		}

		@Property(tries = 50)
		@ExpectFailure("coverage check should have failed")
		void coverageWorksForLabelledCollectors(@ForAll int anInt) {
			Statistics.label("ints").collect(anInt > 0);

			Statistics.label("ints").coverage(coverage -> {
				coverage.check(true).percentage(c -> {
					Assertions.fail("");
				});
			});
		}

		@Property(tries = 50)
		void percentageCheckPredicate(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).percentage(p -> p > 0.0);
			});
		}

		@Property(tries = 50)
		void percentageCheckAssertion(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).percentage(p -> {
					assertThat(p).isGreaterThan(0.0);
					assertThat(p).isLessThan(100.0);
				});
			});
		}
	}

	@Group
	class AdHocQueries {

		@Property(generation = GenerationMode.EXHAUSTIVE)
		@StatisticsReport(OFF)
		void checkCountForQuery(@ForAll @IntRange(min = 1, max = 100) int anInt) {
			Statistics.collect(anInt);

			Statistics.coverage(coverage -> {
				Predicate<List<Integer>> query = params -> params.get(0) <= 10;
				coverage.checkQuery(query).count(c -> c == 10);
			});
		}

		@Property(generation = GenerationMode.EXHAUSTIVE)
		@StatisticsReport(OFF)
		void checkPercentageForQuery(@ForAll @IntRange(min = 1, max = 100) int anInt) {
			Statistics.collect(anInt);

			Statistics.coverage(coverage -> {
				Predicate<List<Integer>> query = params -> params.get(0) <= 50;
				coverage.checkQuery(query).percentage(p -> {
					assertThat(p).isCloseTo(50.0, Offset.offset(0.1));
				});
			});
		}

		@Property(generation = GenerationMode.EXHAUSTIVE)
		@StatisticsReport(OFF)
		void checkCountsAreAddedUp(@ForAll @IntRange(min = 1, max = 100) int anInt) {
			Statistics.label("twice").collect(anInt);
			Statistics.label("twice").collect(anInt);

			Statistics.label("twice").coverage(coverage -> {
				Predicate<List<Integer>> query = params -> params.get(0) <= 10;
				coverage.checkQuery(query).count(c -> c == 20);
			});
		}

		@Property(tries = 50)
		@StatisticsReport(OFF)
		@ExpectFailure(checkResult = CheckClassCastException.class)
		void queryWithWrongTypeFails(@ForAll int anInt) {
			Statistics.collect(anInt);

			Statistics.coverage(coverage -> {
				Predicate<List<String>> query = params -> !params.get(0).isEmpty();
				coverage.checkQuery(query).count(c -> c == 10);
			});
		}

		private class CheckClassCastException implements Consumer<PropertyExecutionResult> {
			@Override
			public void accept(PropertyExecutionResult propertyExecutionResult) {
				assertThat(propertyExecutionResult.throwable()).isPresent();
				assertThat(propertyExecutionResult.throwable().get()).isInstanceOf(ClassCastException.class);
			}
		}

	}
}
