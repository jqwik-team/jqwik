package net.jqwik.engine.hooks;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@Group
class StatisticsCoverageTests {

	@Group
	class Count {
		@Property(tries = 10)
		void coverageFailsForViolatedCountCondition(@ForAll int anInt) {
			Statistics.collect(anInt > 0);

			Statistics.coverage(coverage -> {
				coverage.check(true).count(c -> false);
			});

			PropertyLifecycle.after(((executionResult, context) -> {
				Assertions.assertThat(executionResult.getStatus())
						  .describedAs("coverage check should have failed")
						  .isEqualTo(PropertyExecutionResult.Status.FAILED);
				return executionResult.withSeedSuccessful();
			}));
		}

		@Property(tries = 10)
		void coverageWorksForLabelledCollectors(@ForAll int anInt) {
			Statistics.label("ints").collect(anInt > 0);

			Statistics.coverageOf("ints", coverage -> {
				coverage.check(true).count(c -> false);
			});

			PropertyLifecycle.after(((executionResult, context) -> {
				Assertions.assertThat(executionResult.getStatus())
						  .describedAs("coverage check should have failed")
						  .isEqualTo(PropertyExecutionResult.Status.FAILED);
				return executionResult.withSeedSuccessful();
			}));
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
}
