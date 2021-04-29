package net.jqwik.time.api.times.localTime.constraint;

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
		void timeRange(@ForAll("times") @TimeRange(min = "09:29:20.113943", max = "14:34:24.113943") LocalTime time) {
			assertThat(time).isBetween(LocalTime.of(9, 29, 20, 113943000), LocalTime.of(14, 34, 24, 113943000));
		}

		@Property
		void hourRange(@ForAll("times") @HourRange(min = 11, max = 13) LocalTime time) {
			assertThat(time.getHour()).isBetween(11, 13);
		}

		@Property
		void minuteRange(@ForAll("times") @MinuteRange(min = 31, max = 33) LocalTime time) {
			assertThat(time.getMinute()).isBetween(31, 33);
		}

		@Property
		void secondRange(@ForAll("times") @SecondRange(min = 22, max = 25) LocalTime time) {
			assertThat(time.getSecond()).isBetween(22, 25);
		}

		@Provide
		Arbitrary<LocalTime> times() {
			return of(
				LocalTime.of(9, 29, 20),
				LocalTime.of(9, 29, 21),
				LocalTime.of(10, 30, 21),
				LocalTime.of(11, 31, 22),
				LocalTime.of(12, 32, 23),
				LocalTime.of(13, 33, 24),
				LocalTime.of(14, 34, 24),
				LocalTime.of(14, 34, 25),
				LocalTime.of(15, 35, 26)
			);
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll("times") @Precision(value = HOURS) LocalTime time) {
			assertThat(time.getMinute()).isEqualTo(0);
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("times") @Precision(value = MINUTES) LocalTime time) {
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("times") @Precision(value = SECONDS) LocalTime time) {
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void millis(@ForAll("times") @Precision(value = MILLIS) LocalTime time) {
			assertThat(time.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void micros(@ForAll("times") @Precision(value = MICROS) LocalTime time) {
			assertThat(time.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanos(@ForAll("times") @Precision(value = NANOS) LocalTime time) {
			assertThat(time).isNotNull();
		}

		@Provide
		Arbitrary<LocalTime> times() {
			return of(
				//For Hours
				LocalTime.of(9, 0, 3),
				LocalTime.of(10, 3, 0),
				LocalTime.of(11, 0, 0),
				LocalTime.of(12, 0, 0, 312),
				LocalTime.of(13, 0, 0, 392_291_392),
				LocalTime.of(14, 0, 0),
				LocalTime.of(15, 1, 1, 111_111_111),
				//For Minutes
				LocalTime.of(9, 12, 3),
				LocalTime.of(10, 3, 0),
				LocalTime.of(11, 13, 0, 333_211),
				LocalTime.of(12, 14, 0, 312),
				LocalTime.of(13, 13, 0, 392_291_392),
				LocalTime.of(14, 44, 0),
				LocalTime.of(15, 1, 1, 111_111_111),
				//For Seconds
				LocalTime.of(9, 12, 3),
				LocalTime.of(10, 3, 31),
				LocalTime.of(11, 13, 32, 333_211),
				LocalTime.of(12, 14, 11, 312),
				LocalTime.of(13, 13, 33, 392_291_392),
				LocalTime.of(14, 44, 14),
				LocalTime.of(15, 1, 1, 111_111_111),
				//For Millis
				LocalTime.of(9, 12, 3, 322_000_000),
				LocalTime.of(10, 3, 31, 321_000_000),
				LocalTime.of(11, 13, 32, 333_211),
				LocalTime.of(12, 14, 11, 312),
				LocalTime.of(13, 13, 33, 392_291_392),
				LocalTime.of(14, 44, 14, 312_000_000),
				LocalTime.of(15, 1, 1, 111_111_111),
				//For Micros
				LocalTime.of(9, 12, 3, 322_212_000),
				LocalTime.of(10, 3, 31, 321_312_000),
				LocalTime.of(11, 13, 32, 333_211),
				LocalTime.of(12, 14, 11, 312),
				LocalTime.of(13, 13, 33, 392_291_392),
				LocalTime.of(14, 44, 14, 312_344_000),
				LocalTime.of(15, 1, 1, 111_111_111),
				//For Nanos
				LocalTime.of(9, 12, 3, 322_212_333),
				LocalTime.of(10, 3, 31, 321_312_111),
				LocalTime.of(11, 13, 32, 333_211),
				LocalTime.of(12, 14, 11, 312),
				LocalTime.of(13, 13, 33, 392_291_392),
				LocalTime.of(14, 44, 14, 312_344_000),
				LocalTime.of(15, 1, 1, 111_111_111)
			);
		}

	}

}
