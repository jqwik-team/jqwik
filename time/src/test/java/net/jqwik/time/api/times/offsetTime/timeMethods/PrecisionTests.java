package net.jqwik.time.api.times.offsetTime.timeMethods;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
@PropertyDefaults(tries = 100)
public class PrecisionTests {

	@Provide
	Arbitrary<OffsetTime> times() {
		return Times.offsetTimes();
	}

	@Provide
	Arbitrary<OffsetTime> precisionHours() {
		return Times.offsetTimes().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMinutes() {
		return Times.offsetTimes().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<OffsetTime> precisionSeconds() {
		return Times.offsetTimes().ofPrecision(SECONDS);
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
	void hours(@ForAll("precisionHours") OffsetTime time) {
		assertThat(time.getMinute()).isEqualTo(0);
		assertThat(time.getSecond()).isEqualTo(0);
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void minutes(@ForAll("precisionMinutes") OffsetTime time) {
		assertThat(time.getSecond()).isEqualTo(0);
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void seconds(@ForAll("precisionSeconds") OffsetTime time) {
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void millis(@ForAll("precisionMilliseconds") OffsetTime time) {
		assertThat(time.getNano() % 1_000_000).isEqualTo(0);
	}

	@Property
	void micros(@ForAll("precisionMicroseconds") OffsetTime time) {
		assertThat(time.getNano() % 1_000).isEqualTo(0);
	}

	@Property
	void nanos(@ForAll("precisionNanoseconds") OffsetTime time) {
		assertThat(time).isNotNull();
	}

}
