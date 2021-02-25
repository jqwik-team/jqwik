package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
public class TimesConstraintTests {

	@Group
	class LocalTimeConstraints {

		@Group
		class Constraints {

			@Property
			void timeRangeBetween(@ForAll @TimeRange(min = "01:32:21.113943", max = "03:49:32") LocalTime time) {
				assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943));
				assertThat(time).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
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
				void hours(@ForAll @Precision(ofPrecision = HOURS) LocalTime time) {
					assertThat(time.getMinute()).isZero();
					assertThat(time.getSecond()).isZero();
					assertThat(time.getNano()).isZero();
				}

				@Property
				void minutes(@ForAll @Precision(ofPrecision = MINUTES) LocalTime time) {
					assertThat(time.getSecond()).isZero();
					assertThat(time.getNano()).isZero();
				}

				@Property
				void seconds(@ForAll @Precision(ofPrecision = SECONDS) LocalTime time) {
					assertThat(time.getNano()).isZero();
				}

				@Property
				void millis(@ForAll @Precision(ofPrecision = MILLIS) LocalTime time) {
					assertThat(time.getNano() % 1_000_000).isZero();
				}

				@Property
				void micros(@ForAll @Precision(ofPrecision = MICROS) LocalTime time) {
					assertThat(time.getNano() % 1_000).isZero();
				}

				@Property
				void nanos(@ForAll @Precision(ofPrecision = NANOS) LocalTime time) {
					assertThat(time).isNotNull();
				}

			}

		}

		@Group
		class InvalidConfigurations {

			@Group
			class TimeRangeConstraint {

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionWrongFormat(@ForAll @TimeRange(min = "1:3:5") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionWrongFormat(@ForAll @TimeRange(max = "1:3:5") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionIllegalString(@ForAll @TimeRange(min = "foo") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionIllegalString(@ForAll @TimeRange(max = "foo") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionOnly1Part(@ForAll @TimeRange(min = "09") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionOnly1Part(@ForAll @TimeRange(max = "09") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionPicoseconds(@ForAll @TimeRange(min = "09:21:30.9992123437") LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionPicoseconds(@ForAll @TimeRange(max = "09:21:30.9992123437") LocalTime time) {
					//do nothing
				}

			}

			@Group
			class HourRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooEarly(@ForAll @HourRange(min = -1) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooEarly(@ForAll @HourRange(max = -1) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooLate(@ForAll @HourRange(min = 24) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooLate(@ForAll @HourRange(max = 24) LocalTime time) {
					//do nothing
				}

			}

			@Group
			class MinuteRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooEarly(@ForAll @MinuteRange(min = -1) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooEarly(@ForAll @MinuteRange(max = -1) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooLate(@ForAll @MinuteRange(min = 60) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooLate(@ForAll @MinuteRange(max = 60) LocalTime time) {
					//do nothing
				}

			}

			@Group
			class SecondRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooEarly(@ForAll @SecondRange(min = -1) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooEarly(@ForAll @SecondRange(max = -1) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooLate(@ForAll @SecondRange(min = 60) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooLate(@ForAll @SecondRange(max = 60) LocalTime time) {
					//do nothing
				}

			}

			@Group
			class Precisions {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void centuries(@ForAll @Precision(ofPrecision = CENTURIES) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void days(@ForAll @Precision(ofPrecision = DAYS) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void decades(@ForAll @Precision(ofPrecision = DECADES) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void eras(@ForAll @Precision(ofPrecision = ERAS) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void forever(@ForAll @Precision(ofPrecision = FOREVER) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void halfDays(@ForAll @Precision(ofPrecision = HALF_DAYS) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void millennia(@ForAll @Precision(ofPrecision = MILLENNIA) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void months(@ForAll @Precision(ofPrecision = MONTHS) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void weeks(@ForAll @Precision(ofPrecision = WEEKS) LocalTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void years(@ForAll @Precision(ofPrecision = YEARS) LocalTime time) {
					//do nothing
				}

			}

		}

	}

	@Group
	class OffsetTimeConstraints {

		@Group
		class Constraints {

			@Property
			void timeRangeBetween(@ForAll @TimeRange(min = "01:32:21.113943", max = "03:49:32") OffsetTime time) {
				assertThat(time.toLocalTime()).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943));
				assertThat(time.toLocalTime()).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
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

			@Group
			class Precisions {

				@Property
				void hours(@ForAll @Precision(ofPrecision = HOURS) OffsetTime time) {
					assertThat(time.getMinute()).isZero();
					assertThat(time.getSecond()).isZero();
					assertThat(time.getNano()).isZero();
				}

				@Property
				void minutes(@ForAll @Precision(ofPrecision = MINUTES) OffsetTime time) {
					assertThat(time.getSecond()).isZero();
					assertThat(time.getNano()).isZero();
				}

				@Property
				void seconds(@ForAll @Precision(ofPrecision = SECONDS) OffsetTime time) {
					assertThat(time.getNano()).isZero();
				}

				@Property
				void millis(@ForAll @Precision(ofPrecision = MILLIS) OffsetTime time) {
					assertThat(time.getNano() % 1_000_000).isZero();
				}

				@Property
				void micros(@ForAll @Precision(ofPrecision = MICROS) OffsetTime time) {
					assertThat(time.getNano() % 1_000).isZero();
				}

				@Property
				void nanos(@ForAll @Precision(ofPrecision = NANOS) OffsetTime time) {
					assertThat(time).isNotNull();
				}

			}

		}

		@Group
		class InvalidConfigurations {

			@Group
			class TimeRangeAnnotation {

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionWrongFormat(@ForAll @TimeRange(min = "1:3:5") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionWrongFormat(@ForAll @TimeRange(max = "1:3:5") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionIllegalString(@ForAll @TimeRange(min = "foo") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionIllegalString(@ForAll @TimeRange(max = "foo") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionOnly1Part(@ForAll @TimeRange(min = "09") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionOnly1Part(@ForAll @TimeRange(max = "09") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void minThrowsExceptionPicoseconds(@ForAll @TimeRange(min = "09:21:30.9992123437") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeParseException.class)
				void maxThrowsExceptionPicoseconds(@ForAll @TimeRange(max = "09:21:30.9992123437") OffsetTime time) {
					//do nothing
				}

			}

			@Group
			class HourRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooEarly(@ForAll @HourRange(min = -1) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooEarly(@ForAll @HourRange(max = -1) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooLate(@ForAll @HourRange(min = 24) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooLate(@ForAll @HourRange(max = 24) OffsetTime time) {
					//do nothing
				}

			}

			@Group
			class MinuteRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooEarly(@ForAll @MinuteRange(min = -1) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooEarly(@ForAll @MinuteRange(max = -1) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooLate(@ForAll @MinuteRange(min = 60) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooLate(@ForAll @MinuteRange(max = 60) OffsetTime time) {
					//do nothing
				}

			}

			@Group
			class SecondRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooEarly(@ForAll @SecondRange(min = -1) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooEarly(@ForAll @SecondRange(max = -1) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minTooLate(@ForAll @SecondRange(min = 60) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxTooLate(@ForAll @SecondRange(max = 60) OffsetTime time) {
					//do nothing
				}

			}

			@Group
			class Precisions {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void centuries(@ForAll @Precision(ofPrecision = CENTURIES) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void days(@ForAll @Precision(ofPrecision = DAYS) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void decades(@ForAll @Precision(ofPrecision = DECADES) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void eras(@ForAll @Precision(ofPrecision = ERAS) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void forever(@ForAll @Precision(ofPrecision = FOREVER) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void halfDays(@ForAll @Precision(ofPrecision = HALF_DAYS) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void millennia(@ForAll @Precision(ofPrecision = MILLENNIA) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void months(@ForAll @Precision(ofPrecision = MONTHS) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void weeks(@ForAll @Precision(ofPrecision = WEEKS) OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void years(@ForAll @Precision(ofPrecision = YEARS) OffsetTime time) {
					//do nothing
				}

			}

		}

	}

}
