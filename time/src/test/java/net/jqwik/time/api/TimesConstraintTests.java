package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class TimesConstraintTests {

	@Group
	class LocalTimeConstraints {

		@Group
		class Constraints {

			@Property
			void timeRangeMin(@ForAll @TimeRange(min = "01:32:21.113943") LocalTime time) {
				assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943000));
			}

			@Property
			void timeRangeMax(@ForAll @TimeRange(max = "03:49:32") LocalTime time) {
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
			void timeRangeMin(@ForAll @TimeRange(min = "01:32:21.113943") OffsetTime time) {
				assertThat(time.toLocalTime()).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943000));
			}

			@Property
			void timeRangeMax(@ForAll @TimeRange(max = "03:49:32") OffsetTime time) {
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
			class TimeRangeConstraint {

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
			class OffsetRangeConstraint {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minZoneOffsetTooEarly(@ForAll @OffsetRange(min = "-12:00:01") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxZoneOffsetTooEarly(@ForAll @OffsetRange(max = "-12:00:01") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void minZoneOffsetTooLate(@ForAll @OffsetRange(min = "+14:00:01") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void maxZoneOffsetTooLate(@ForAll @OffsetRange(max = "+14:00:01") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minZoneOffsetUnsigned(@ForAll @OffsetRange(min = "07:00:00") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxZoneOffsetUnsigned(@ForAll @OffsetRange(max = "07:00:00") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minZoneOffsetIllegalString(@ForAll @OffsetRange(min = "foo") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxZoneOffsetIllegalString(@ForAll @OffsetRange(max = "foo") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minZoneOffsetIllegalIllegalFormat(@ForAll @OffsetRange(min = "+2:3:2") OffsetTime time) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxZoneOffsetIllegalIllegalFormat(@ForAll @OffsetRange(max = "+2:3:2") OffsetTime time) {
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

	@Group
	class ZoneOffsetConstraints {

		@Property
		void zoneOffsetMin(@ForAll @OffsetRange(min = "-09:00:00") ZoneOffset offset) {
			assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0).getTotalSeconds());
		}

		@Property
		void zoneOffsetMax(@ForAll @OffsetRange(max = "+08:00:00") ZoneOffset offset) {
			assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(ZoneOffset.ofHoursMinutesSeconds(8, 0, 0).getTotalSeconds());
		}

		@Group
		class InvalidConfiguration {

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void minZoneOffsetTooEarly(@ForAll @OffsetRange(min = "-12:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void maxZoneOffsetTooEarly(@ForAll @OffsetRange(max = "-12:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void minZoneOffsetTooLate(@ForAll @OffsetRange(min = "+14:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void maxZoneOffsetTooLate(@ForAll @OffsetRange(max = "+14:00:01") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void minZoneOffsetUnsigned(@ForAll @OffsetRange(min = "07:00:00") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void maxZoneOffsetUnsigned(@ForAll @OffsetRange(max = "07:00:00") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void minZoneOffsetIllegalString(@ForAll @OffsetRange(min = "foo") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void maxZoneOffsetIllegalString(@ForAll @OffsetRange(max = "foo") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void minZoneOffsetIllegalIllegalFormat(@ForAll @OffsetRange(min = "+2:3:2") ZoneOffset offset) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeException.class)
			void maxZoneOffsetIllegalIllegalFormat(@ForAll @OffsetRange(max = "+2:3:2") ZoneOffset offset) {
				//do nothing
			}

		}

	}

	@Group
	class DurationConstraints {

		@Property
		void zoneOffsetMin(@ForAll @DurationRange(min = "PT-3000H-39M-22.123111444S") Duration duration) {
			Duration start = Duration.ofSeconds(-3000 * 60 * 60 - 39 * 60 - 22, -123111444);
			assertThat(duration.compareTo(start)).isGreaterThanOrEqualTo(0);
		}

		@Property
		void zoneOffsetMax(@ForAll @DurationRange(max = "PT1999H22M11S") Duration duration) {
			Duration end = Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 11);
			assertThat(duration.compareTo(end)).isLessThanOrEqualTo(0);
		}

		@Group
		class Precisions {

			@Property
			void hours(@ForAll @Precision(ofPrecision = HOURS) Duration duration) {
				assertThat(getMinute(duration)).isZero();
				assertThat(getSecond(duration)).isZero();
				assertThat(duration.getNano()).isZero();
			}

			@Property
			void minutes(@ForAll @Precision(ofPrecision = MINUTES) Duration duration) {
				assertThat(getSecond(duration)).isZero();
				assertThat(duration.getNano()).isZero();
			}

			@Property
			void seconds(@ForAll @Precision(ofPrecision = SECONDS) Duration duration) {
				assertThat(duration.getNano()).isZero();
			}

			@Property
			void millis(@ForAll @Precision(ofPrecision = MILLIS) Duration duration) {
				assertThat(duration.getNano() % 1_000_000).isZero();
			}

			@Property
			void micros(@ForAll @Precision(ofPrecision = MICROS) Duration duration) {
				assertThat(duration.getNano() % 1_000).isZero();
			}

			@Property
			void nanos(@ForAll @Precision(ofPrecision = NANOS) Duration duration) {
				assertThat(duration).isNotNull();
			}

		}

		@Group
		class InvalidConfiguration {

			@Group
			class DurationRangeConstraint {

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minTooEarly(@ForAll @DurationRange(min = "PT-2562047788015215H-30M-8.000000001S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxTooEarly(@ForAll @DurationRange(max = "PT-2562047788015215H-30M-8.000000001S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minTooLate(@ForAll @DurationRange(min = "PT2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxTooLate(@ForAll @DurationRange(max = "PT2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minIllegalString(@ForAll @DurationRange(min = "foo") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxIllegalString(@ForAll @DurationRange(max = "foo") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minWrongFormat(@ForAll @DurationRange(min = "2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxWrongFormat(@ForAll @DurationRange(max = "2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

			}

			@Group
			class Precisions {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void centuries(@ForAll @Precision(ofPrecision = CENTURIES) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void days(@ForAll @Precision(ofPrecision = DAYS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void decades(@ForAll @Precision(ofPrecision = DECADES) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void eras(@ForAll @Precision(ofPrecision = ERAS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void forever(@ForAll @Precision(ofPrecision = FOREVER) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void halfDays(@ForAll @Precision(ofPrecision = HALF_DAYS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void millennia(@ForAll @Precision(ofPrecision = MILLENNIA) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void months(@ForAll @Precision(ofPrecision = MONTHS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void weeks(@ForAll @Precision(ofPrecision = WEEKS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void years(@ForAll @Precision(ofPrecision = YEARS) Duration duration) {
					//do nothing
				}

			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void timeRange(@ForAll @TimeRange(min = "01:32:21.113943") Byte b) {
			assertThat(b).isNotNull();
		}

		@Property
		void offsetRange(@ForAll @OffsetRange(min = "-09:00:00", max = "+08:00:00") Long l) {
			assertThat(l).isNotNull();
		}

		@Property
		void hourRange(@ForAll @HourRange(min = 11, max = 13) Integer i) {
			assertThat(i).isNotNull();
		}

		@Property
		void minuteRange(@ForAll @MinuteRange(min = 11, max = 13) Random random) {
			assertThat(random).isNotNull();
		}

		@Property
		void secondRange(@ForAll @SecondRange(min = 11, max = 13) Boolean b) {
			assertThat(b).isNotNull();
		}

		@Property
		void precision(@ForAll @Precision(ofPrecision = HOURS) char c) {
			assertThat(c).isNotNull();
		}

		@Property
		void durationRange(@ForAll @DurationRange(max = "PT1999H22M11S") String string) {
			assertThat(string).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Group
		class TimeRangeConstraint {

			@Property
			void localTime(@ForAll("times") @TimeRange(min = "01:32:21.113943", max = "01:32:24.113943") LocalTime time) {
				assertThat(time).isBetween(LocalTime.of(1, 32, 21, 113943000), LocalTime.of(1, 32, 24, 113943000));
			}

			@Property
			void offsetTime(@ForAll("offsetTimes") @TimeRange(min = "01:32:21.113943", max = "01:32:24.113943") OffsetTime time) {
				assertThat(time.toLocalTime()).isBetween(LocalTime.of(1, 32, 21, 113943000), LocalTime.of(1, 32, 24, 113943000));
			}

			@Provide
			Arbitrary<LocalTime> times() {
				return of(
						LocalTime.of(1, 32, 20),
						LocalTime.of(1, 32, 21),
						LocalTime.of(1, 32, 22),
						LocalTime.of(1, 32, 23),
						LocalTime.of(1, 32, 24),
						LocalTime.of(1, 32, 25),
						LocalTime.of(1, 32, 26)
				);
			}

			@Provide
			Arbitrary<OffsetTime> offsetTimes() {
				return of(
						OffsetTime.of(LocalTime.of(1, 32, 20), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(1, 32, 21), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(1, 32, 22), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(1, 32, 23), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(1, 32, 24), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(1, 32, 25), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(1, 32, 26), ZoneOffset.UTC)
				);
			}

		}

		@Group
		class OffsetRangeConstraint {

			@Property
			void zoneOffsets(@ForAll("offsets") @OffsetRange(min = "+01:00:00", max = "-01:00:00") ZoneOffset offset) {
				assertThat(offset).isBetween(ZoneOffset.ofHoursMinutesSeconds(1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0));
			}

			@Property
			void offsetTime(@ForAll("offsetTimes") @OffsetRange(min = "+01:00:00", max = "-01:00:00") OffsetTime time) {
				assertThat(time.getOffset())
						.isBetween(ZoneOffset.ofHoursMinutesSeconds(1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0));
			}

			@Provide
			Arbitrary<ZoneOffset> offsets() {
				return of(
						ZoneOffset.ofHours(-3),
						ZoneOffset.ofHours(-2),
						ZoneOffset.ofHours(-1),
						ZoneOffset.ofHours(0),
						ZoneOffset.ofHours(1),
						ZoneOffset.ofHours(2),
						ZoneOffset.ofHours(3)
				);
			}

			@Provide
			Arbitrary<OffsetTime> offsetTimes() {
				return of(
						OffsetTime.of(LocalTime.of(1, 32, 20), ZoneOffset.ofHours(-3)),
						OffsetTime.of(LocalTime.of(1, 32, 21), ZoneOffset.ofHours(-2)),
						OffsetTime.of(LocalTime.of(1, 32, 22), ZoneOffset.ofHours(-1)),
						OffsetTime.of(LocalTime.of(1, 32, 23), ZoneOffset.ofHours(0)),
						OffsetTime.of(LocalTime.of(1, 32, 24), ZoneOffset.ofHours(1)),
						OffsetTime.of(LocalTime.of(1, 32, 25), ZoneOffset.ofHours(2)),
						OffsetTime.of(LocalTime.of(1, 32, 26), ZoneOffset.ofHours(3))
				);
			}

		}

		@Group
		class HourRangeConstraint {

			@Property
			void localTime(@ForAll("times") @HourRange(min = 11, max = 13) LocalTime time) {
				assertThat(time.getHour()).isBetween(11, 13);
			}

			@Property
			void offsetTime(@ForAll("offsetTimes") @HourRange(min = 11, max = 13) OffsetTime time) {
				assertThat(time.toLocalTime().getHour()).isBetween(11, 13);
			}

			@Provide
			Arbitrary<LocalTime> times() {
				return of(
						LocalTime.of(9, 32, 20),
						LocalTime.of(10, 32, 21),
						LocalTime.of(11, 32, 22),
						LocalTime.of(12, 32, 23),
						LocalTime.of(13, 32, 24),
						LocalTime.of(14, 32, 25),
						LocalTime.of(15, 32, 26)
				);
			}

			@Provide
			Arbitrary<OffsetTime> offsetTimes() {
				return of(
						OffsetTime.of(LocalTime.of(9, 32, 20), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(10, 32, 21), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(11, 32, 22), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(12, 32, 23), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(13, 32, 24), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(14, 32, 25), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(15, 32, 26), ZoneOffset.UTC)
				);
			}

		}

		@Group
		class MinuteRangeConstraint {

			@Property
			void localTime(@ForAll("times") @MinuteRange(min = 31, max = 33) LocalTime time) {
				assertThat(time.getMinute()).isBetween(31, 33);
			}

			@Property
			void offsetTime(@ForAll("offsetTimes") @MinuteRange(min = 31, max = 33) OffsetTime time) {
				assertThat(time.toLocalTime().getMinute()).isBetween(31, 33);
			}

			@Provide
			Arbitrary<LocalTime> times() {
				return of(
						LocalTime.of(9, 29, 20),
						LocalTime.of(10, 30, 21),
						LocalTime.of(11, 31, 22),
						LocalTime.of(12, 32, 23),
						LocalTime.of(13, 33, 24),
						LocalTime.of(14, 34, 25),
						LocalTime.of(15, 35, 26)
				);
			}

			@Provide
			Arbitrary<OffsetTime> offsetTimes() {
				return of(
						OffsetTime.of(LocalTime.of(9, 29, 20), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(10, 30, 21), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(11, 31, 22), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(12, 32, 23), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(13, 33, 24), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(14, 34, 25), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(15, 35, 26), ZoneOffset.UTC)
				);
			}

		}

		@Group
		class SecondRangeConstraint {

			@Property
			void localTime(@ForAll("times") @SecondRange(min = 22, max = 25) LocalTime time) {
				assertThat(time.getSecond()).isBetween(22, 25);
			}

			@Property
			void offsetTime(@ForAll("offsetTimes") @SecondRange(min = 22, max = 25) OffsetTime time) {
				assertThat(time.toLocalTime().getSecond()).isBetween(22, 25);
			}

			@Provide
			Arbitrary<LocalTime> times() {
				return of(
						LocalTime.of(9, 32, 20),
						LocalTime.of(10, 32, 21),
						LocalTime.of(11, 32, 22),
						LocalTime.of(12, 32, 23),
						LocalTime.of(13, 32, 24),
						LocalTime.of(14, 32, 25),
						LocalTime.of(15, 32, 26)
				);
			}

			@Provide
			Arbitrary<OffsetTime> offsetTimes() {
				return of(
						OffsetTime.of(LocalTime.of(9, 32, 20), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(10, 32, 21), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(11, 32, 22), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(12, 32, 23), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(13, 32, 24), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(14, 32, 25), ZoneOffset.UTC),
						OffsetTime.of(LocalTime.of(15, 32, 26), ZoneOffset.UTC)
				);
			}

		}

		@Group
		class PrecisionConstraint {

			@Group
			class Hours {

				@Property
				void localTime(@ForAll("times") @Precision(ofPrecision = HOURS) LocalTime time) {
					assertThat(time.getMinute()).isEqualTo(0);
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void offsetTime(@ForAll("offsetTimes") @Precision(ofPrecision = HOURS) OffsetTime time) {
					assertThat(time.getMinute()).isEqualTo(0);
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void duration(@ForAll("durations") @Precision(ofPrecision = HOURS) Duration duration) {
					assertThat(getMinute(duration)).isEqualTo(0);
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalTime> times() {
					return of(
							LocalTime.of(9, 0, 3),
							LocalTime.of(10, 3, 0),
							LocalTime.of(11, 0, 0),
							LocalTime.of(12, 0, 0, 312),
							LocalTime.of(13, 0, 0, 392_291_392),
							LocalTime.of(14, 0, 0),
							LocalTime.of(15, 1, 1, 111_111_111)
					);
				}

				@Provide
				Arbitrary<OffsetTime> offsetTimes() {
					return of(
							OffsetTime.of(LocalTime.of(9, 0, 3), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(10, 3, 0), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(11, 0, 0), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(12, 0, 0, 312), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(13, 0, 0, 392_291_392), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(14, 0, 0), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
					);
				}

				@Provide
				Arbitrary<Duration> durations() {
					return of(
							Duration.ofSeconds(1999 * 60 * 60 + 0 * 60 + 0),
							Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 0),
							Duration.ofSeconds(1312 * 60 * 60 + 0 * 60 + 33),
							Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
							Duration.ofSeconds(3432 * 60 * 60 + 0 * 60 + 0),
							Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 0),
							Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
					);
				}

			}

			@Group
			class Minutes {

				@Property
				void localTime(@ForAll("times") @Precision(ofPrecision = MINUTES) LocalTime time) {
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void offsetTime(@ForAll("offsetTimes") @Precision(ofPrecision = MINUTES) OffsetTime time) {
					assertThat(time.getSecond()).isEqualTo(0);
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void duration(@ForAll("durations") @Precision(ofPrecision = MINUTES) Duration duration) {
					assertThat(getSecond(duration)).isEqualTo(0);
					assertThat(duration.getNano()).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalTime> times() {
					return of(
							LocalTime.of(9, 12, 3),
							LocalTime.of(10, 3, 0),
							LocalTime.of(11, 13, 0, 333_211),
							LocalTime.of(12, 14, 0, 312),
							LocalTime.of(13, 13, 0, 392_291_392),
							LocalTime.of(14, 44, 0),
							LocalTime.of(15, 1, 1, 111_111_111)
					);
				}

				@Provide
				Arbitrary<OffsetTime> offsetTimes() {
					return of(
							OffsetTime.of(LocalTime.of(9, 12, 3), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(10, 3, 0), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(11, 13, 0, 333_211), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(12, 14, 0, 312), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(13, 13, 0, 392_291_392), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(14, 44, 0), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
					);
				}

				@Provide
				Arbitrary<Duration> durations() {
					return of(
							Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 0),
							Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 0, 111_203),
							Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 33),
							Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
							Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 0),
							Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 0),
							Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
					);
				}

			}

			@Group
			class Seconds {

				@Property
				void localTime(@ForAll("times") @Precision(ofPrecision = SECONDS) LocalTime time) {
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void offsetTime(@ForAll("offsetTimes") @Precision(ofPrecision = SECONDS) OffsetTime time) {
					assertThat(time.getNano()).isEqualTo(0);
				}

				@Property
				void duration(@ForAll("durations") @Precision(ofPrecision = SECONDS) Duration duration) {
					assertThat(duration.getNano()).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalTime> times() {
					return of(
							LocalTime.of(9, 12, 3),
							LocalTime.of(10, 3, 31),
							LocalTime.of(11, 13, 32, 333_211),
							LocalTime.of(12, 14, 11, 312),
							LocalTime.of(13, 13, 33, 392_291_392),
							LocalTime.of(14, 44, 14),
							LocalTime.of(15, 1, 1, 111_111_111)
					);
				}

				@Provide
				Arbitrary<OffsetTime> offsetTimes() {
					return of(
							OffsetTime.of(LocalTime.of(9, 12, 3), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(10, 3, 31), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(14, 44, 14), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
					);
				}

				@Provide
				Arbitrary<Duration> durations() {
					return of(
							Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
							Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
							Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44),
							Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
							Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31),
							Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21),
							Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
					);
				}

			}

			@Group
			class Millis {

				@Property
				void localTime(@ForAll("times") @Precision(ofPrecision = MILLIS) LocalTime time) {
					assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				}

				@Property
				void offsetTime(@ForAll("offsetTimes") @Precision(ofPrecision = MILLIS) OffsetTime time) {
					assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				}

				@Property
				void duration(@ForAll("durations") @Precision(ofPrecision = MILLIS) Duration duration) {
					assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalTime> times() {
					return of(
							LocalTime.of(9, 12, 3, 322_000_000),
							LocalTime.of(10, 3, 31, 321_000_000),
							LocalTime.of(11, 13, 32, 333_211),
							LocalTime.of(12, 14, 11, 312),
							LocalTime.of(13, 13, 33, 392_291_392),
							LocalTime.of(14, 44, 14, 312_000_000),
							LocalTime.of(15, 1, 1, 111_111_111)
					);
				}

				@Provide
				Arbitrary<OffsetTime> offsetTimes() {
					return of(
							OffsetTime.of(LocalTime.of(9, 12, 3, 322_000_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(10, 3, 31, 321_000_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(14, 44, 14, 312_000_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
					);
				}

				@Provide
				Arbitrary<Duration> durations() {
					return of(
							Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
							Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
							Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44, 391_000_000),
							Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
							Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31, 9_000_000),
							Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21, 103_000_000),
							Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
					);
				}

			}

			@Group
			class Micros {

				@Property
				void localTime(@ForAll("times") @Precision(ofPrecision = MICROS) LocalTime time) {
					assertThat(time.getNano() % 1_000).isEqualTo(0);
				}

				@Property
				void offsetTime(@ForAll("offsetTimes") @Precision(ofPrecision = MICROS) OffsetTime time) {
					assertThat(time.getNano() % 1_000).isEqualTo(0);
				}

				@Property
				void duration(@ForAll("durations") @Precision(ofPrecision = MICROS) Duration duration) {
					assertThat(duration.getNano() % 1_000).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalTime> times() {
					return of(
							LocalTime.of(9, 12, 3, 322_212_000),
							LocalTime.of(10, 3, 31, 321_312_000),
							LocalTime.of(11, 13, 32, 333_211),
							LocalTime.of(12, 14, 11, 312),
							LocalTime.of(13, 13, 33, 392_291_392),
							LocalTime.of(14, 44, 14, 312_344_000),
							LocalTime.of(15, 1, 1, 111_111_111)
					);
				}

				@Provide
				Arbitrary<OffsetTime> offsetTimes() {
					return of(
							OffsetTime.of(LocalTime.of(9, 12, 3, 322_212_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(10, 3, 31, 321_312_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(14, 44, 14, 312_344_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
					);
				}

				@Provide
				Arbitrary<Duration> durations() {
					return of(
							Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
							Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
							Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44, 391_312_000),
							Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
							Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31, 9_324_000),
							Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21, 103_232_000),
							Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
					);
				}

			}

			@Group
			class Nanos {

				@Property
				void localTime(@ForAll("times") @Precision(ofPrecision = NANOS) LocalTime time) {
					assertThat(time).isNotNull();
				}

				@Property
				void offsetTime(@ForAll("offsetTimes") @Precision(ofPrecision = NANOS) OffsetTime time) {
					assertThat(time).isNotNull();
				}

				@Property
				void duration(@ForAll("durations") @Precision(ofPrecision = NANOS) Duration duration) {
					assertThat(duration).isNotNull();
				}

				@Provide
				Arbitrary<LocalTime> times() {
					return of(
							LocalTime.of(9, 12, 3, 322_212_333),
							LocalTime.of(10, 3, 31, 321_312_111),
							LocalTime.of(11, 13, 32, 333_211),
							LocalTime.of(12, 14, 11, 312),
							LocalTime.of(13, 13, 33, 392_291_392),
							LocalTime.of(14, 44, 14, 312_344_000),
							LocalTime.of(15, 1, 1, 111_111_111)
					);
				}

				@Provide
				Arbitrary<OffsetTime> offsetTimes() {
					return of(
							OffsetTime.of(LocalTime.of(9, 12, 3, 322_212_333), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(10, 3, 31, 321_312_111), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(11, 13, 32, 333_211), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(12, 14, 11, 312), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(13, 13, 33, 392_291_392), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(14, 44, 14, 312_344_000), ZoneOffset.UTC),
							OffsetTime.of(LocalTime.of(15, 1, 1, 111_111_111), ZoneOffset.UTC)
					);
				}

				@Provide
				Arbitrary<Duration> durations() {
					return of(
							Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
							Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
							Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44, 391_312_312),
							Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
							Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31, 9_324_422),
							Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21, 103_232_321),
							Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
					);
				}

			}

		}

		@Group
		class DurationRangeConstraint {

			@Property
			void duration(@ForAll("durations") @DurationRange(min = "PT1999H22M8S", max = "PT1999H22M11S") Duration duration) {
				assertThat(duration)
						.isBetween(Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 8), Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 11));
			}

			@Provide
			Arbitrary<Duration> durations() {
				return of(
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 6),
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 7),
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 8),
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 9),
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 10),
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 11),
						Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 12)
				);
			}

		}

	}

	int getSecond(Duration d) {
		return (int) (d.getSeconds() % 60);
	}

	int getMinute(Duration d) {
		return (int) ((d.getSeconds() % 3600) / 60);
	}

}
