package net.jqwik.time.api.times.localTime.timeMethods;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
@PropertyDefaults(tries = 100)
public class PrecisionTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Provide
	Arbitrary<LocalTime> precisionHours() {
		return Times.times().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMinutes() {
		return Times.times().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<LocalTime> precisionSeconds() {
		return Times.times().ofPrecision(SECONDS);
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
	void hours(@ForAll("precisionHours") LocalTime time) {
		assertThat(time.getMinute()).isEqualTo(0);
		assertThat(time.getSecond()).isEqualTo(0);
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void minutes(@ForAll("precisionMinutes") LocalTime time) {
		assertThat(time.getSecond()).isEqualTo(0);
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void seconds(@ForAll("precisionSeconds") LocalTime time) {
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void milliseconds(@ForAll("precisionMilliseconds") LocalTime time) {
		assertThat(time.getNano() % 1_000_000).isEqualTo(0);
	}

	@Property
	void microseconds(@ForAll("precisionMicroseconds") LocalTime time) {
		assertThat(time.getNano() % 1_000).isEqualTo(0);
	}

	@Property
	void nanoseconds(@ForAll("precisionNanoseconds") LocalTime time) {
		assertThat(time).isNotNull();
	}

}
