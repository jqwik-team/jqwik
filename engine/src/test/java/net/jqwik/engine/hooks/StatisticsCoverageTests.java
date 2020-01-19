package net.jqwik.engine.hooks;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

class StatisticsCoverageTests {

	@Property(tries = 10)
	void coverageIsBeingCheckedForSuccessfulProperty(@ForAll int anInt) {
		Statistics.collect(anInt > 0);

		Statistics.coverage(coverage -> {
			coverage.check(true).count(c -> c > 1000);
		});

		PropertyLifecycle.after(((executionResult, context) -> {
			Assertions.assertThat(executionResult.getStatus())
					  .describedAs("coverage check should have failed")
					  .isEqualTo(PropertyExecutionResult.Status.FAILED);
			return executionResult.withSeedSuccessful();
		}));
	}
}
