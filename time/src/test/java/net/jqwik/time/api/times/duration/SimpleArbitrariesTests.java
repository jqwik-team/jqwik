package net.jqwik.time.api.times.duration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<Duration> durations() {
		return Times.durations();
	}

	@Property
	void validDurationIsGenerated(@ForAll("durations") Duration duration) {
		assertThat(duration).isNotNull();
	}

	@Group
	class WorstCases {

		@Property
		void positiveOneSecondDifferenceNanosHigh(
			@ForAll("worstCase") Duration duration,
			@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosStart,
			@ForAll @IntRange(min = 0, max = 200) int nanosEnd,
			@ForAll JqwikRandom random
		) {

			Duration start = duration.withNanos(nanosStart);
			Duration end = Duration.ofSeconds(start.getSeconds() + 1, nanosEnd);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void positiveTwoSecondDifferenceNanosHigh(
			@ForAll("worstCase") Duration duration,
			@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosStart,
			@ForAll @IntRange(min = 0, max = 200) int nanosEnd,
			@ForAll JqwikRandom random
		) {

			Duration start = duration.withNanos(nanosStart);
			Duration end = Duration.ofSeconds(start.getSeconds() + 2, nanosEnd);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void negativeOneSecondDifferenceNanosHigh(
			@ForAll("worstCaseNegative") Duration duration,
			@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosEnd,
			@ForAll @IntRange(min = 0, max = 200) int nanosStart,
			@ForAll JqwikRandom random
		) {

			Duration start = duration.withNanos(nanosEnd);
			Duration end = Duration.ofSeconds(start.getSeconds() + 1, nanosStart);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void negativeTwoSecondDifferenceNanosHigh(
			@ForAll("worstCaseNegative") Duration duration,
			@ForAll @IntRange(min = 999_999_800, max = 999_999_999) int nanosEnd,
			@ForAll @IntRange(min = 0, max = 200) int nanosStart,
			@ForAll JqwikRandom random
		) {

			Duration start = duration.withNanos(nanosEnd);
			Duration end = Duration.ofSeconds(start.getSeconds() + 2, nanosStart);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void positiveOneSecondDifferenceNanosLow(
			@ForAll("worstCase") Duration start,
			@ForAll @IntRange(min = 0, max = 200) int nanosAdd,
			@ForAll JqwikRandom random
		) {

			Assume.that(start.getNano() < 1_000_000_000 - nanosAdd);

			Duration end = Duration.ofSeconds(start.getSeconds() + 1, start.getNano() + nanosAdd);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void positiveTwoSecondDifferenceNanosLow(
			@ForAll("worstCase") Duration start,
			@ForAll @IntRange(min = 0, max = 200) int nanosAdd,
			@ForAll JqwikRandom random
		) {

			Assume.that(start.getNano() < 1_000_000_000 - nanosAdd);

			Duration end = Duration.ofSeconds(start.getSeconds() + 2, start.getNano() + nanosAdd);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void negativeOneSecondDifferenceNanosLow(
			@ForAll("worstCaseNegative") Duration start,
			@ForAll @IntRange(min = 0, max = 200) int nanosSubtract,
			@ForAll JqwikRandom random
		) {

			Assume.that(start.getNano() > nanosSubtract);

			Duration end = Duration.ofSeconds(start.getSeconds() + 1, start.getNano() - nanosSubtract);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void negativeTwoSecondsDifferenceNanosLow(
			@ForAll("worstCaseNegative") Duration start,
			@ForAll @IntRange(min = 0, max = 200) int nanosSubtract,
			@ForAll JqwikRandom random
		) {

			Assume.that(start.getNano() > nanosSubtract);

			Duration end = Duration.ofSeconds(start.getSeconds() + 2, start.getNano() - nanosSubtract);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Property
		void aroundZero(@ForAll JqwikRandom random) {

			Duration start = Duration.ofSeconds(0, -101);
			Duration end = Duration.ofSeconds(0, 100);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			checkAllGenerated(durations.generator(1000), random, durationGenerated -> {
				assertThat(durationGenerated).isNotNull();
				return true;
			});

		}

		@Provide
		Arbitrary<Duration> worstCase() {
			return Times.durations().between(Duration.ZERO, Duration.ofSeconds(Long.MAX_VALUE - 2, 999_999_999));
		}

		@Provide
		Arbitrary<Duration> worstCaseNegative() {
			return Times.durations().ofPrecision(NANOS).between(DefaultDurationArbitrary.DEFAULT_MIN, Duration.ofSeconds(-2, 0));
		}

	}

}