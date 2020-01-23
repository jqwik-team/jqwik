package net.jqwik.engine.hooks;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.engine.*;

@Group
class StatisticsCoverageTests {

	@Property(tries = 10)
	@ExpectFailure("coverage check should have failed")
	void moreThanOneCoverageWorks(@ForAll int anInt) {
		Statistics.collect(anInt > 0);

		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(true).count(c -> true));
		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(false).count(c -> false));
	}

	@Group
	class Count {
		@Property(tries = 10)
		@ExpectFailure("coverage check should have failed")
		void coverageFailsForViolatedCountCondition(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> false);
			});
		}

		@Property(tries = 10)
		@ExpectFailure("coverage check should have failed")
		void coverageWorksForLabelledCollectors(@ForAll int anInt) {
			Statistics.label("ints").collect(anInt > 0);

			Statistics.coverageOf("ints", coverage -> {
				coverage.check(true).count(c -> false);
			});
		}

		@Property(tries = 10)
		void countCheckPredicate(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> c > 0);
			});
		}

		@Property(tries = 10)
		void countCheckPredicateWithCountAll(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count((c, a) -> c <= a);
			});
		}

		@Property(tries = 10)
		void countCheckAssertion(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> {
					Assertions.assertThat(c).isGreaterThan(0);
					Assertions.assertThat(c).isLessThan(11);
				});
			});
		}

		@Property(tries = 10)
		void countCheckAssertionWithCountAll(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count((c, a) -> {
					Assertions.assertThat(c).isLessThanOrEqualTo(a);
				});
			});
		}
	}

	@Group
	class Percentage {
		@Property(tries = 10)
		@ExpectFailure("coverage check should have failed")
		void coverageFailsForViolatedPercentageCondition(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).percentage(c -> false);
			});
		}

		@Property(tries = 10)
		@ExpectFailure("coverage check should have failed")
		void coverageWorksForLabelledCollectors(@ForAll int anInt) {
			Statistics.label("ints").collect(anInt > 0);

			Statistics.coverageOf("ints", coverage -> {
				coverage.check(true).percentage(c -> {
					Assertions.fail("");
				});
			});
		}

		@Property(tries = 10)
		void percentageCheckPredicate(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).percentage(p -> p > 0.0);
			});
		}

		@Property(tries = 10)
		void percentageCheckAssertion(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).percentage(p -> {
					Assertions.assertThat(p).isGreaterThan(0.0);
					Assertions.assertThat(p).isLessThan(100.0);
				});
			});
		}

	}
}
