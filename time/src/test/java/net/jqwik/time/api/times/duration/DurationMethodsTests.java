package net.jqwik.time.api.times.duration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;
import static net.jqwik.time.api.testingSupport.ForDuration.*;

@Group
public class DurationMethodsTests {

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

	@Group
	class DurationMethods {

		@Property
		void between(@ForAll("durations") Duration start, @ForAll("durations") Duration end, @ForAll JqwikRandom random) {

			Assume.that(start.compareTo(end) <= 0);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration.compareTo(start)).isGreaterThanOrEqualTo(0);
				assertThat(duration.compareTo(end)).isLessThanOrEqualTo(0);
			});

		}

		@Property
		void betweenEndDurationAfterStartDuration(
			@ForAll("durations") Duration start,
			@ForAll("durations") Duration end,
			@ForAll JqwikRandom random
		) {

			Assume.that(start.compareTo(end) > 0);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration.compareTo(end)).isGreaterThanOrEqualTo(0);
				assertThat(duration.compareTo(start)).isLessThanOrEqualTo(0);
			});

		}

		@Property
		void betweenSame(@ForAll("durations") Duration durationSame, @ForAll JqwikRandom random) {

			Arbitrary<Duration> durations = Times.durations().between(durationSame, durationSame);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration).isEqualTo(durationSame);
			});

		}

	}

	@Group
	class PrecisionMethods {

		@Property
		void hours(@ForAll("precisionHours") Duration duration) {
			assertThat(getMinute(duration)).isEqualTo(0);
			assertThat(getSecond(duration)).isEqualTo(0);
			assertThat(duration.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("precisionMinutes") Duration duration) {
			assertThat(getSecond(duration)).isEqualTo(0);
			assertThat(duration.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("precisionSeconds") Duration duration) {
			assertThat(duration.getNano()).isEqualTo(0);
		}

		@Property
		void millis(@ForAll("precisionMilliseconds") Duration duration) {
			assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void micros(@ForAll("precisionMicroseconds") Duration duration) {
			assertThat(duration.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanos(@ForAll("precisionNanoseconds") Duration duration) {
			assertThat(duration).isNotNull();
		}

	}

}
