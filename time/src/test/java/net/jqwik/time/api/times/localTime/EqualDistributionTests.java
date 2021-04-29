package net.jqwik.time.api.times.localTime;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;

public class EqualDistributionTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Provide
	Arbitrary<LocalTime> precisionMilliseconds() {
		return Times.times().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMicroseconds() {
		return Times.times().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<LocalTime> precisionNanoseconds() {
		return Times.times().ofPrecision(NANOS);
	}

	@Property
	void hours(@ForAll("times") LocalTime time) {
		Statistics.label("Hours")
				  .collect(time.getHour())
				  .coverage(this::check24Coverage);
	}

	@Property
	void minutes(@ForAll("times") LocalTime time) {
		Statistics.label("Minutes")
				  .collect(time.getMinute())
				  .coverage(this::check60Coverage);
	}

	@Property
	void seconds(@ForAll("times") LocalTime time) {
		Statistics.label("Seconds")
				  .collect(time.getSecond())
				  .coverage(this::check60Coverage);
	}

	@Property
	void milliseconds(@ForAll("precisionMilliseconds") LocalTime time) {

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
	void microseconds(@ForAll("precisionMicroseconds") LocalTime time) {

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
	void nanoseconds(@ForAll("precisionNanoseconds") LocalTime time) {

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
			coverage.check(value).count(c -> c > 0);
		}
	}

}
