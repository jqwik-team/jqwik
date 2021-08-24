package net.jqwik.time.api.dates.period;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;

@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
@StatisticsReport(onFailureOnly = true)
public class EqualDistributionTests {

	@Provide
	Arbitrary<Period> periods() {
		return Dates.periods();
	}

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

	@Property(edgeCases = EdgeCasesMode.MIXIN)
		// Zero is rare when edge cases are turned off
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
