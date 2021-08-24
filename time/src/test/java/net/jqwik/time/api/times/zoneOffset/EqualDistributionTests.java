package net.jqwik.time.api.times.zoneOffset;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;

@StatisticsReport(onFailureOnly = true)
public class EqualDistributionTests {

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Property
	void negativeAndPositiveValuesAreGenerated(@ForAll("offsets") ZoneOffset offset) {
		int totalSeconds = offset.getTotalSeconds();
		Assume.that(totalSeconds != 0);
		Statistics.label("Negative value")
				  .collect(totalSeconds < 0)
				  .coverage(this::check5050BooleanCoverage);
	}

	@Property
	void valueZeroIsGenerated(@ForAll("offsets") ZoneOffset offset) {
		Statistics.label("00:00:00 is possible")
				  .collect(offset.getTotalSeconds() == 0)
				  .coverage(coverage -> {
					  coverage.check(true).count(c -> c >= 1);
				  });
	}

	@Property
	void minusAndPlusIsPossibleWhenHourIsZero(@ForAll("offsetsNear0") ZoneOffset offset) {
		int totalSeconds = offset.getTotalSeconds();
		Assume.that(totalSeconds > -3600 && totalSeconds < 3600 && totalSeconds != 0);
		Statistics.label("Negative value with Hour is zero")
				  .collect(totalSeconds < 0)
				  .coverage(this::check5050BooleanCoverage);
	}

	@Property
	void hours(@ForAll("offsets") ZoneOffset offset) {
		Statistics.label("Hours")
				  .collect(offset.getTotalSeconds() / 3600)
				  .coverage(this::checkHourCoverage);
	}

	@Property
	void minutes(@ForAll("offsets") ZoneOffset offset) {
		Statistics.label("Minutes")
				  .collect(Math.abs((offset.getTotalSeconds() % 3600) / 60))
				  .coverage(this::checkMinuteCoverage);
	}

	@Provide
	Arbitrary<ZoneOffset> offsetsNear0() {
		return Times.zoneOffsets().between(ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(1, 0, 0));
	}

	private void check5050BooleanCoverage(StatisticsCoverage coverage) {
		coverage.check(true).percentage(p -> p >= 35);
		coverage.check(false).percentage(p -> p >= 35);
	}

	private void checkHourCoverage(StatisticsCoverage coverage) {
		coverage.check(-12).percentage(p -> p >= 0.8);
		for (int value = -11; value <= 13; value++) {
			coverage.check(value).percentage(p -> p >= 3);
		}
		coverage.check(14).percentage(p -> p >= 0.8);
	}

	private void checkMinuteCoverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 60; value += 15) {
			coverage.check(value).percentage(p -> p >= 20);
		}
	}
}
