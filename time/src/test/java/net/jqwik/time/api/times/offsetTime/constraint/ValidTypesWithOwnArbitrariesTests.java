package net.jqwik.time.api.times.offsetTime.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ValidTypesWithOwnArbitrariesTests {

	@Group
	class Ranges {

		@Property
		void timeRange(@ForAll("offsetTimes") @TimeRange(min = "10:30:20.113943", max = "14:34:25.113943") OffsetTime time) {
			assertThat(time.toLocalTime()).isBetween(LocalTime.of(10, 30, 20, 113943000), LocalTime.of(14, 34, 25, 113943000));
		}

		@Property
		void offsetRange(@ForAll("offsetTimes") @OffsetRange(min = "+01:00:00", max = "-01:00:00") OffsetTime time) {
			assertThat(time.getOffset())
				.isBetween(ZoneOffset.ofHoursMinutesSeconds(1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0));
		}

		@Property
		void hourRange(@ForAll("offsetTimes") @HourRange(min = 11, max = 13) OffsetTime time) {
			assertThat(time.toLocalTime().getHour()).isBetween(11, 13);
		}

		@Property
		void minuteRange(@ForAll("offsetTimes") @MinuteRange(min = 31, max = 33) OffsetTime time) {
			assertThat(time.toLocalTime().getMinute()).isBetween(31, 33);
		}

		@Property
		void secondRange(@ForAll("offsetTimes") @SecondRange(min = 22, max = 25) OffsetTime time) {
			assertThat(time.toLocalTime().getSecond()).isBetween(22, 25);
		}

		@Provide
		Arbitrary<OffsetTime> offsetTimes() {
			return of(
				OffsetTime.of(LocalTime.of(9, 29, 20), ZoneOffset.ofHours(-3)),
				OffsetTime.of(LocalTime.of(10, 30, 20), ZoneOffset.ofHours(-2)),
				OffsetTime.of(LocalTime.of(10, 30, 21), ZoneOffset.ofHours(-2)),
				OffsetTime.of(LocalTime.of(11, 31, 22), ZoneOffset.ofHours(-1)),
				OffsetTime.of(LocalTime.of(12, 32, 23), ZoneOffset.ofHours(0)),
				OffsetTime.of(LocalTime.of(13, 33, 24), ZoneOffset.ofHours(1)),
				OffsetTime.of(LocalTime.of(14, 34, 25), ZoneOffset.ofHours(2)),
				OffsetTime.of(LocalTime.of(14, 34, 26), ZoneOffset.ofHours(2)),
				OffsetTime.of(LocalTime.of(15, 35, 26), ZoneOffset.ofHours(3))
			);
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll("offsetTimes") @Precision(value = HOURS) OffsetTime time) {
			assertThat(time.getMinute()).isEqualTo(0);
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("offsetTimes") @Precision(value = MINUTES) OffsetTime time) {
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("offsetTimes") @Precision(value = SECONDS) OffsetTime time) {
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void millis(@ForAll("offsetTimes") @Precision(value = MILLIS) OffsetTime time) {
			assertThat(time.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void micros(@ForAll("offsetTimes") @Precision(value = MICROS) OffsetTime time) {
			assertThat(time.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanos(@ForAll("offsetTimes") @Precision(value = NANOS) OffsetTime time) {
			assertThat(time).isNotNull();
		}

		@Provide
		Arbitrary<OffsetTime> offsetTimes() {
			return of(
				//For Hours
				OffsetTime.of(LocalTime.of(9, 0, 3), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(10, 3, 0), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(11, 0, 0), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(12, 0, 0, 312), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(13, 0, 0, 392_291_392), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(14, 0, 0), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC),
				//For Minutes
				OffsetTime.of(LocalTime.of(9, 12, 3), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(10, 3, 0), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(11, 13, 0, 333_211), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(12, 14, 0, 312), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(13, 13, 0, 392_291_392), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(14, 44, 0), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC),
				//For Seconds
				OffsetTime.of(LocalTime.of(9, 12, 3), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(10, 3, 31), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(14, 44, 14), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC),
				//For Millis
				OffsetTime.of(LocalTime.of(9, 12, 3, 322_000_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(10, 3, 31, 321_000_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(14, 44, 14, 312_000_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC),
				//For Micros
				OffsetTime.of(LocalTime.of(9, 12, 3, 322_212_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(10, 3, 31, 321_312_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(14, 44, 14, 312_344_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC),
				//For Nanos
				OffsetTime.of(LocalTime.of(9, 12, 3, 322_212_333), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(10, 3, 31, 321_312_111), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(14, 44, 14, 312_344_000), ZoneOffset.UTC),
				OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
			);
		}

	}

}
