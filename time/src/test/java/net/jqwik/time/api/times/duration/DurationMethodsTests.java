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
		void between(@ForAll("durations") Duration start, @ForAll("durations") Duration end, @ForAll Random random) {

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
			@ForAll Random random
		) {

			Assume.that(start.compareTo(end) > 0);

			Arbitrary<Duration> durations = Times.durations().between(start, end);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration.compareTo(end)).isGreaterThanOrEqualTo(0);
				assertThat(duration.compareTo(start)).isLessThanOrEqualTo(0);
			});

		}

		@Property
		void betweenSame(@ForAll("durations") Duration durationSame, @ForAll Random random) {

			Arbitrary<Duration> durations = Times.durations().between(durationSame, durationSame);

			assertAllGenerated(durations.generator(1000), random, duration -> {
				assertThat(duration).isEqualTo(durationSame);
			});

		}

	}

	@Group
	class PrecisionMethods {

		@Group
		class Hours {

			@Property
			void precision(@ForAll("precisionHours") Duration duration) {
				assertThat(getMinute(duration)).isEqualTo(0);
				assertThat(getSecond(duration)).isEqualTo(0);
				assertThat(duration.getNano()).isEqualTo(0);
			}

			@Property
			void precisionBetween(
				@ForAll("durations") Duration startDuration,
				@ForAll("durations") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7 - 30 * 60);
				Assume.that(getHour(startDuration) != getHour(endDuration));

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(HOURS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(getMinute(duration)).isEqualTo(0);
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

			@Property
			void precisionBetweenWithPrecisionMinutes(
				@ForAll("precisionMinutes") Duration startDuration,
				@ForAll("precisionMinutes") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7 - 30 * 60);
				Assume.that(getHour(startDuration) != getHour(endDuration));

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(HOURS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(getMinute(duration)).isEqualTo(0);
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

		}

		@Group
		class Minutes {

			@Property
			void precision(@ForAll("precisionMinutes") Duration duration) {
				assertThat(getSecond(duration)).isEqualTo(0);
				assertThat(duration.getNano()).isEqualTo(0);
			}

			@Property
			void precisionBetween(
				@ForAll("durations") Duration startDuration,
				@ForAll("durations") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7);
				Assume.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration));

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

			@Property
			void precisionBetweenWithPrecisionSeconds(
				@ForAll("precisionSeconds") Duration startDuration,
				@ForAll("precisionSeconds") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(startDuration.getSeconds() < Long.MAX_VALUE - 7);
				Assume.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration));

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

		}

		@Group
		class Seconds {

			@Property
			void precision(@ForAll("precisionSeconds") Duration duration) {
				assertThat(duration.getNano()).isEqualTo(0);
			}

			@Property
			void precisionBetween(
				@ForAll("durations") Duration startDuration,
				@ForAll("durations") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume
					.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration) || getSecond(startDuration) != getSecond(endDuration));

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.getNano()).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

			@Property
			void precisionBetweenWithPrecisionMillis(
				@ForAll("precisionMilliseconds") Duration startDuration,
				@ForAll("precisionMilliseconds") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume
					.that(getHour(startDuration) != getHour(endDuration) || getMinute(startDuration) != getMinute(endDuration) || getSecond(startDuration) != getSecond(endDuration));

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.getNano()).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

		}

		@Group
		class Millis {

			@Property
			void precision(@ForAll("precisionMilliseconds") Duration duration) {
				assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
			}

			@Property
			void precisionBetween(
				@ForAll("durations") Duration startDuration,
				@ForAll("durations") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(getHour(startDuration) != getHour(endDuration)
								|| getMinute(startDuration) != getMinute(endDuration)
								|| getSecond(startDuration) != getSecond(endDuration)
								|| startDuration.getNano() / 1_000_000 != endDuration.getNano() / 1_000_000);

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

			@Property
			void precisionBetweenWithPrecisionMicros(
				@ForAll("precisionMicroseconds") Duration startDuration,
				@ForAll("precisionMicroseconds") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(getHour(startDuration) != getHour(endDuration)
								|| getMinute(startDuration) != getMinute(endDuration)
								|| getSecond(startDuration) != getSecond(endDuration)
								|| startDuration.getNano() / 1_000_000 != endDuration.getNano() / 1_000_000);

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

		}

		@Group
		class Micros {

			@Property
			void precision(@ForAll("precisionMicroseconds") Duration duration) {
				assertThat(duration.getNano() % 1_000).isEqualTo(0);
			}

			@Property
			void precisionBetween(
				@ForAll("durations") Duration startDuration,
				@ForAll("durations") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(getHour(startDuration) != getHour(endDuration)
								|| getMinute(startDuration) != getMinute(endDuration)
								|| getSecond(startDuration) != getSecond(endDuration)
								|| startDuration.getNano() / 1_000 != endDuration.getNano() / 1_000);

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MICROS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.getNano() % 1_000).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

			@Property
			void precisionBetweenWithPrecisionNanos(
				@ForAll("precisionNanoseconds") Duration startDuration,
				@ForAll("precisionNanoseconds") Duration endDuration,
				@ForAll Random random
			) {

				Assume.that(startDuration.compareTo(endDuration) <= 0);
				Assume.that(getHour(startDuration) != getHour(endDuration)
								|| getMinute(startDuration) != getMinute(endDuration)
								|| getSecond(startDuration) != getSecond(endDuration)
								|| startDuration.getNano() / 1_000 != endDuration.getNano() / 1_000);

				Arbitrary<Duration> durations = Times.durations().between(startDuration, endDuration).ofPrecision(MICROS);

				assertAllGenerated(durations.generator(1000), random, duration -> {
					assertThat(duration.getNano() % 1_000).isEqualTo(0);
					assertThat(duration.compareTo(startDuration)).isGreaterThanOrEqualTo(0);
					assertThat(duration.compareTo(endDuration)).isLessThanOrEqualTo(0);
					return true;
				});

			}

		}

		@Group
		class Nanos {

			@Property
			void precision(@ForAll("precisionNanoseconds") Duration duration) {
				assertThat(duration).isNotNull();
			}

		}

	}
}
