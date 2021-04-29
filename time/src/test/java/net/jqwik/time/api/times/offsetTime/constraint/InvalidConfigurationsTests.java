package net.jqwik.time.api.times.offsetTime.constraint;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;

@Group
public class InvalidConfigurationsTests {

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
		void centuries(@ForAll @Precision(value = CENTURIES) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void days(@ForAll @Precision(value = DAYS) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void decades(@ForAll @Precision(value = DECADES) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void eras(@ForAll @Precision(value = ERAS) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void forever(@ForAll @Precision(value = FOREVER) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void halfDays(@ForAll @Precision(value = HALF_DAYS) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void millennia(@ForAll @Precision(value = MILLENNIA) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void months(@ForAll @Precision(value = MONTHS) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void weeks(@ForAll @Precision(value = WEEKS) OffsetTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void years(@ForAll @Precision(value = YEARS) OffsetTime time) {
			//do nothing
		}

	}

}
