package net.jqwik.time.api.times.offsetTime;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;

@StatisticsReport(onFailureOnly = true)
public class EqualDistributionTests {

	@Provide
	Arbitrary<OffsetTime> times() {
		return Times.offsetTimes();
	}

	@Provide
	Arbitrary<OffsetTime> precisionMilliseconds() {
		return Times.offsetTimes().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMicroseconds() {
		return Times.offsetTimes().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionNanoseconds() {
		return Times.offsetTimes().ofPrecision(NANOS);
	}

	@Property
	void hours(@ForAll("times") OffsetTime time) {
		Statistics.label("Hours")
				  .collect(time.getHour())
				  .coverage(this::check24Coverage);
	}

	@Property
	void minutes(@ForAll("times") OffsetTime time) {
		Statistics.label("Minutes")
				  .collect(time.getMinute())
				  .coverage(this::check60Coverage);
	}

	@Property
	void seconds(@ForAll("times") OffsetTime time) {
		Statistics.label("Seconds")
				  .collect(time.getSecond())
				  .coverage(this::check60Coverage);
	}

	@Property
	void milliseconds(@ForAll("precisionMilliseconds") OffsetTime time) {

		Statistics.label("Milliseconds x--")
				  .collect(time.getNano() / 100_000_000)
				  .coverage(this::check10Coverage);

		Statistics.label("Milliseconds -x-")
				  .collect((time.getNano() / 10_000_000) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Milliseconds --x")
				  .collect((time.getNano() / 1_000_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void microseconds(@ForAll("precisionMicroseconds") OffsetTime time) {

		Statistics.label("Microseconds x--")
				  .collect((time.getNano() % 1_000_000) / 100_000)
				  .coverage(this::check10Coverage);

		Statistics.label("Microseconds -x-")
				  .collect(((time.getNano() % 1_000_000) / 10_000) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Microseconds --x")
				  .collect(((time.getNano() % 1_000_000) / 1_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void nanoseconds(@ForAll("precisionNanoseconds") OffsetTime time) {

		Statistics.label("Nanoseconds x--")
				  .collect((time.getNano() % 1_000) / 100)
				  .coverage(this::check10Coverage);

		Statistics.label("Nanoseconds -x-")
				  .collect(((time.getNano() % 1_000) / 10) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Nanoseconds --x")
				  .collect((time.getNano() % 1_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void negativeAndPositiveOffsetValuesAreGenerated(@ForAll("times") OffsetTime time) {
		ZoneOffset offset = time.getOffset();
		int totalSeconds = offset.getTotalSeconds();
		Assume.that(totalSeconds != 0);
		Statistics.label("Negative value")
				  .collect(totalSeconds < 0)
				  .coverage(this::check5050BooleanCoverage);
	}

	@Property
	void offsetValueZeroIsGenerated(@ForAll("times") OffsetTime time) {
		ZoneOffset offset = time.getOffset();
		Statistics.label("00:00:00 is possible")
				  .collect(offset.getTotalSeconds() == 0)
				  .coverage(coverage -> {
					  coverage.check(true).count(c -> c >= 1);
				  });
	}

	@Property
	void minusAndPlusOffsetIsPossibleWhenHourIsZero(@ForAll("offsetsNear0") OffsetTime time) {
		ZoneOffset offset = time.getOffset();
		int totalSeconds = offset.getTotalSeconds();
		Assume.that(totalSeconds > -3600 && totalSeconds < 3600 && totalSeconds != 0);
		Statistics.label("Negative value with Hour is zero")
				  .collect(totalSeconds < 0)
				  .coverage(this::check5050BooleanCoverage);
	}

	@Property
	void offsetHours(@ForAll("times") OffsetTime time) {
		ZoneOffset offset = time.getOffset();
		Statistics.label("Hours")
				  .collect(offset.getTotalSeconds() / 3600)
				  .coverage(this::checkOffsetHourCoverage);
	}

	@Property
	void offsetMinutes(@ForAll("times") OffsetTime time) {
		ZoneOffset offset = time.getOffset();
		Statistics.label("Minutes")
				  .collect(Math.abs((offset.getTotalSeconds() % 3600) / 60))
				  .coverage(this::checkOffsetMinuteCoverage);
	}

	@Provide
	Arbitrary<OffsetTime> offsetsNear0() {
		return Times.offsetTimes().offsetBetween(ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(1, 0, 0));
	}

	private void check10Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 10; value++) {
			coverage.check(value).percentage(p -> p >= 5);
		}
	}

	private void check24Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 24; value++) {
			coverage.check(value).percentage(p -> p >= 1.5);
		}
	}

	private void check60Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 60; value++) {
			coverage.check(value).percentage(p -> p >= 0.15);
		}
	}

	private void check5050BooleanCoverage(StatisticsCoverage coverage) {
		coverage.check(true).percentage(p -> p >= 35);
		coverage.check(false).percentage(p -> p >= 35);
	}

	private void checkOffsetHourCoverage(StatisticsCoverage coverage) {
		for (int value = -12; value <= 14; value++) {
			coverage.check(value).percentage(p -> p >= 1);
		}
	}

	private void checkOffsetMinuteCoverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 60; value += 15) {
			coverage.check(value).percentage(p -> p >= 12);
		}
	}

}
