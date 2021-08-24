package net.jqwik.time.api.times.duration;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;

@StatisticsReport(onFailureOnly = true)
public class EqualDistributionTests {

	@Provide
	Arbitrary<Duration> durations() {
		return Times.durations();
	}

	@Provide
	Arbitrary<Duration> precisionHours() {
		return Times.durations().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<Duration> precisionMinutes() {
		return Times.durations().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<Duration> precisionSeconds() {
		return Times.durations().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<Duration> precisionMilliseconds() {
		return Times.durations().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<Duration> precisionMicroseconds() {
		return Times.durations().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<Duration> precisionNanoseconds() {
		return Times.durations().ofPrecision(NANOS);
	}

	@Provide
	Arbitrary<Duration> durationsNear0() {
		return Times.durations().ofPrecision(NANOS).between(Duration.ofSeconds(-10, 0), Duration.ofSeconds(10, 999_999_999));
	}

	@Provide
	Arbitrary<Duration> durationsNear0_2() {
		return Times.durations().ofPrecision(NANOS).between(Duration.ofSeconds(0, -999_999_999), Duration.ofSeconds(0, 999_999_999));
	}

	@Property
	void negativeAndPositive0ValuesArePossible(@ForAll("durationsNear0_2") Duration duration) {
		Assume.that(!duration.isZero());
		Statistics.label("Seconds")
				  .collect(duration.isNegative())
				  .coverage(this::check5050BooleanCoverage);
	}

	@Property
	void seconds(@ForAll("durationsNear0") Duration duration) {
		Statistics.label("Seconds")
				  .collect(duration.getSeconds())
				  .coverage(this::checkSecondsCoverage);
	}

	@Property
	void milliseconds(@ForAll("precisionMilliseconds") Duration duration) {

		Statistics.label("Milliseconds x--")
				  .collect(duration.getNano() / 100_000_000)
				  .coverage(this::check10Coverage);

		Statistics.label("Milliseconds -x-")
				  .collect((duration.getNano() / 10_000_000) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Milliseconds --x")
				  .collect((duration.getNano() / 1_000_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void microseconds(@ForAll("precisionMicroseconds") Duration duration) {

		Statistics.label("Microseconds x--")
				  .collect((duration.getNano() % 1_000_000) / 100_000)
				  .coverage(this::check10Coverage);

		Statistics.label("Microseconds -x-")
				  .collect(((duration.getNano() % 1_000_000) / 10_000) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Microseconds --x")
				  .collect(((duration.getNano() % 1_000_000) / 1_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void nanoseconds(@ForAll("precisionNanoseconds") Duration duration) {

		Statistics.label("Nanoseconds x--")
				  .collect((duration.getNano() % 1_000) / 100)
				  .coverage(this::check10Coverage);

		Statistics.label("Nanoseconds -x-")
				  .collect(((duration.getNano() % 1_000) / 10) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Nanoseconds --x")
				  .collect((duration.getNano() % 1_000) % 10)
				  .coverage(this::check10Coverage);

	}

	private void check10Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 10; value++) {
			coverage.check(value).percentage(p -> p >= 5);
		}
	}

	private void checkSecondsCoverage(StatisticsCoverage coverage) {
		for (long value = -10; value <= 10; value++) {
			coverage.check(value).percentage(p -> p >= 2.0);
		}
	}

	private void check5050BooleanCoverage(StatisticsCoverage coverage) {
		coverage.check(true).percentage(p -> p >= 35);
		coverage.check(false).percentage(p -> p >= 35);
	}

}
