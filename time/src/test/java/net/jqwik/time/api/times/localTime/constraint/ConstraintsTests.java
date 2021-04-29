package net.jqwik.time.api.times.localTime.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
public class ConstraintsTests {

	@Property
	void timeRangeMin(@ForAll @TimeRange(min = "01:32:21.113943") LocalTime time) {
		assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943000));
	}

	@Property
	void timeRangeMax(@ForAll @TimeRange(max = "03:49:32") LocalTime time) {
		assertThat(time).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
	}

	@Property
	void timeRangeDefaultNotAffectDefaultPrecision(@ForAll @TimeRange LocalTime time) {
		assertThat(time.getNano()).isEqualTo(0);
	}

	@Property
	void hourRangeBetween(@ForAll @HourRange(min = 11, max = 13) LocalTime time) {
		assertThat(time.getHour()).isBetween(11, 13);
	}

	@Property
	void minuteRangeBetween(@ForAll @MinuteRange(min = 11, max = 13) LocalTime time) {
		assertThat(time.getMinute()).isBetween(11, 13);
	}

	@Property
	void secondRangeBetween(@ForAll @SecondRange(min = 11, max = 13) LocalTime time) {
		assertThat(time.getSecond()).isBetween(11, 13);
	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll @Precision(value = HOURS) LocalTime time) {
			assertThat(time.getMinute()).isZero();
			assertThat(time.getSecond()).isZero();
			assertThat(time.getNano()).isZero();
		}

		@Property
		void minutes(@ForAll @Precision(value = MINUTES) LocalTime time) {
			assertThat(time.getSecond()).isZero();
			assertThat(time.getNano()).isZero();
		}

		@Property
		void seconds(@ForAll @Precision(value = SECONDS) LocalTime time) {
			assertThat(time.getNano()).isZero();
		}

		@Property
		void millis(@ForAll @Precision(value = MILLIS) LocalTime time) {
			assertThat(time.getNano() % 1_000_000).isZero();
		}

		@Property
		void micros(@ForAll @Precision(value = MICROS) LocalTime time) {
			assertThat(time.getNano() % 1_000).isZero();
		}

		@Property
		void nanos(@ForAll @Precision(value = NANOS) LocalTime time) {
			assertThat(time).isNotNull();
		}

	}

}
