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
			void timeRangeMin(@ForAll @TimeRange(min = "01:32:21.113943") LocalTime time) {
				assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943));
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
				assertThat(time.toLocalTime()).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943));
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
		void test(@ForAll @OffsetRange(min = "Z") ZoneOffset offset) {
			System.out.println(offset);
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

			int getSecond(Duration d) {
				return (int) (d.getSeconds() % 60);
			}

			int getMinute(Duration d) {
				return (int) ((d.getSeconds() % 3600) / 60);
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

}
