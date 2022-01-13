package net.jqwik.time.api.times.duration;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.time.api.testingSupport.ForDuration.*;

@PropertyDefaults(tries = 100)
public class InvalidConfigurationTests {

	@Provide
	Arbitrary<Duration> precisionNanoseconds() {
		return Times.durations().ofPrecision(NANOS);
	}

	@Provide
	Arbitrary<Duration> lateDurations() {
		return Times.durations()
					.ofPrecision(NANOS)
					.between(Duration.ofSeconds(Long.MAX_VALUE, 999_999_001), DefaultDurationArbitrary.DEFAULT_MAX);
	}

	@Provide
	Arbitrary<Duration> earlyDurations() {
		return Times.durations()
					.ofPrecision(NANOS)
					.between(DefaultDurationArbitrary.DEFAULT_MIN, Duration.ofSeconds(Long.MIN_VALUE, 999));
	}

	@Property
	void ofPrecision(@ForAll ChronoUnit chronoUnit) {

		Assume.that(!chronoUnit.equals(NANOS));
		Assume.that(!chronoUnit.equals(MICROS));
		Assume.that(!chronoUnit.equals(MILLIS));
		Assume.that(!chronoUnit.equals(SECONDS));
		Assume.that(!chronoUnit.equals(MINUTES));
		Assume.that(!chronoUnit.equals(HOURS));

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(chronoUnit)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionHoursMaxDurationSoonAfterMinDuration(
		@ForAll("precisionNanoseconds") Duration startDuration,
		@ForAll @IntRange(min = 1, max = 200) int nanos
	) {

		Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

		Duration endDuration = startDuration.plusNanos(nanos);

		Assume.that(getMinute(startDuration) != 0 && getSecond(startDuration) != 0 && startDuration.getNano() != 0);
		Assume.that(getHour(startDuration) == getHour(endDuration));

		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(HOURS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMinutesMaxDurationSoonAfterMinDuration(
		@ForAll("precisionNanoseconds") Duration startDuration,
		@ForAll @IntRange(min = 1, max = 200) int nanos
	) {

		Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

		Duration endDuration = startDuration.plusNanos(nanos);

		Assume.that(getSecond(startDuration) != 0 && startDuration.getNano() != 0);
		Assume.that(getMinute(startDuration) == getMinute(endDuration));

		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionSecondsMaxDurationSoonAfterMinDuration(
		@ForAll("precisionNanoseconds") Duration startDuration,
		@ForAll @IntRange(min = 1, max = 200) int nanos
	) {

		Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

		Duration endDuration = startDuration.plusNanos(nanos);

		Assume.that(startDuration.getNano() != 0);
		Assume.that(getSecond(startDuration) == getSecond(endDuration));

		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMillisMaxDurationSoonAfterMinDuration(
		@ForAll("precisionNanoseconds") Duration startDuration,
		@ForAll @IntRange(min = 1, max = 200) int nanos
	) {

		Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

		Duration endDuration = startDuration.plusNanos(nanos);

		Assume.that(startDuration.getNano() % 1_000_000 != 0);
		Assume.that(startDuration.getNano() % 1_000_000 + nanos < 1_000_000);

		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMicrosMaxDurationSoonAfterMinDuration(
		@ForAll("precisionNanoseconds") Duration startDuration,
		@ForAll @IntRange(min = 1, max = 200) int nanos
	) {

		Assume.that(startDuration.getSeconds() != Long.MAX_VALUE || startDuration.getNano() + nanos < 1_000_000_000);

		Duration endDuration = startDuration.plusNanos(nanos);

		Assume.that(startDuration.getNano() % 1_000 != 0);
		Assume.that(startDuration.getNano() % 1_000 + nanos < 1_000);

		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MICROS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionHoursMinDurationTooLate(
		@ForAll("lateDurations") Duration startDuration,
		@ForAll("lateDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(HOURS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionMinutesMinDurationTooLate(
		@ForAll("lateDurations") Duration startDuration,
		@ForAll("lateDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionSecondsMinDurationTooLate(
		@ForAll("lateDurations") Duration startDuration,
		@ForAll("lateDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(SECONDS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionMillisMinDurationTooLate(
		@ForAll("lateDurations") Duration startDuration,
		@ForAll("lateDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MILLIS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionMicrosMinDurationTooLate(
		@ForAll("lateDurations") Duration startDuration,
		@ForAll("lateDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MICROS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionHoursMaxDurationTooEarly(
		@ForAll("earlyDurations") Duration startDuration,
		@ForAll("earlyDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(HOURS).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionMinutesMaxDurationTooEarly(
		@ForAll("earlyDurations") Duration startDuration,
		@ForAll("earlyDurations") Duration endDuration
	) {
		Assume.that(startDuration.compareTo(endDuration) <= 0);
		assertThatThrownBy(
			() -> Times.durations().between(startDuration, endDuration).ofPrecision(MINUTES).generator(1000)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void precisionHoursIllegalMinTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getSeconds() % (60 * 60) != 0 || duration.getNano() != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(HOURS).between(duration, DefaultDurationArbitrary.DEFAULT_MAX_PRECISION_HOURS).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionHoursIllegalMaxTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getSeconds() % (60 * 60) != 0 || duration.getNano() != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(HOURS).between(DefaultDurationArbitrary.DEFAULT_MIN_PRECISION_HOURS, duration).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMinutesIllegalMinTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getSeconds() % 60 != 0 || duration.getNano() != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(MINUTES).between(duration, DefaultDurationArbitrary.DEFAULT_MAX_PRECISION_HOURS)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMinutesIllegalMaxTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getSeconds() % 60 != 0 || duration.getNano() != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(MINUTES).between(DefaultDurationArbitrary.DEFAULT_MIN_PRECISION_HOURS, duration)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionSecondsIllegalMinTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getNano() != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(SECONDS).between(duration, DefaultDurationArbitrary.DEFAULT_MAX_PRECISION_HOURS)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionSecondsIllegalMaxTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getNano() != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(SECONDS).between(DefaultDurationArbitrary.DEFAULT_MIN_PRECISION_HOURS, duration)
					   .generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMillisIllegalMinTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getNano() % 1_000_000 != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(MILLIS).between(duration, DefaultDurationArbitrary.DEFAULT_MAX_PRECISION_HOURS).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMillisIllegalMaxTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getNano() % 1_000_000 != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(MILLIS).between(DefaultDurationArbitrary.DEFAULT_MIN_PRECISION_HOURS, duration).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMicrosIllegalMinTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getNano() % 1_000 != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(MICROS).between(duration, DefaultDurationArbitrary.DEFAULT_MAX_PRECISION_HOURS).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

	@Property
	void precisionMicrosIllegalMaxTime(@ForAll("precisionNanoseconds") Duration duration) {

		Assume.that(duration.getNano() % 1_000 != 0);

		assertThatThrownBy(
			() -> Times.durations().ofPrecision(MICROS).between(DefaultDurationArbitrary.DEFAULT_MIN_PRECISION_HOURS, duration).generator(1)
		).isInstanceOf(IllegalArgumentException.class);

	}

}
