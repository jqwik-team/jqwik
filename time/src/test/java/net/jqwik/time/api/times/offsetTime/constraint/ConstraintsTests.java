package net.jqwik.time.api.times.offsetTime.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
@PropertyDefaults(tries = 100)
public class ConstraintsTests {

	@Property
	void timeRangeMin(@ForAll @TimeRange(min = "01:32:21.113943") OffsetTime time) {
		assertThat(time.toLocalTime()).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943000));
	}

	@Property
	void timeRangeMax(@ForAll @TimeRange(max = "03:49:32") OffsetTime time) {
		assertThat(time.toLocalTime()).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
	}

	@Property
	void timeRangeDefaultNotAffectDefaultPrecision(@ForAll @TimeRange OffsetTime time) {
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void hourRangeBetween(@ForAll @HourRange(min = 11, max = 13) OffsetTime time) {
		assertThat(time.getHour()).isBetween(11, 13);
	}

	@Property
	void minuteRangeBetween(@ForAll @MinuteRange(min = 11, max = 13) OffsetTime time) {
		assertThat(time.getMinute()).isBetween(11, 13);
	}

	@Property
	void secondRangeBetween(@ForAll @SecondRange(min = 11, max = 13) OffsetTime time) {
		assertThat(time.getSecond()).isBetween(11, 13);
	}

	@Property
	void offsetBetween(@ForAll @OffsetRange(min = "-09:00:00", max = "+08:00:00") OffsetTime time) {
		assertThat(time.getOffset().getTotalSeconds())
			.isGreaterThanOrEqualTo(ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0).getTotalSeconds());
		assertThat(time.getOffset().getTotalSeconds())
			.isLessThanOrEqualTo(ZoneOffset.ofHoursMinutesSeconds(8, 0, 0).getTotalSeconds());
	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll @Precision(value = HOURS) OffsetTime time) {
			assertThat(time.getMinute()).isZero();
			assertThat(time.getSecond()).isZero();
			assertThat(time.getNano()).isZero();
		}

		@Property
		void minutes(@ForAll @Precision(value = MINUTES) OffsetTime time) {
			assertThat(time.getSecond()).isZero();
			assertThat(time.getNano()).isZero();
		}

		@Property
		void seconds(@ForAll @Precision(value = SECONDS) OffsetTime time) {
			assertThat(time.getNano()).isZero();
		}

		@Property
		void millis(@ForAll @Precision(value = MILLIS) OffsetTime time) {
			assertThat(time.getNano() % 1_000_000).isZero();
		}

		@Property
		void micros(@ForAll @Precision(value = MICROS) OffsetTime time) {
			assertThat(time.getNano() % 1_000).isZero();
		}

		@Property
		void nanos(@ForAll @Precision(value = NANOS) OffsetTime time) {
			assertThat(time).isNotNull();
		}

	}

}
