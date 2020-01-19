package net.jqwik.engine.hooks;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

class StatisticsCoverageTests {

	@Property(tries = 10)
	@Disabled
	void coverageIsBeingCheckedForSuccessfulProperty(@ForAll int anInt) {
		Assertions.fail("Fails approx 1 out of 3 times");

		Statistics.collect(anInt > 0);

		Statistics.coverage(coverage -> {
			System.out.println("###### 1111111");
			coverage.check(true).count(c -> c > 1000);
		});

		PropertyLifecycle.after(((executionResult, context) -> {
			System.out.println("###### 2222222");
			Assertions.assertThat(executionResult.getStatus())
					  .describedAs("coverage check should have failed")
					  .isEqualTo(PropertyExecutionResult.Status.FAILED);
			return executionResult.withSeedSuccessful();
		}));
	}
}
