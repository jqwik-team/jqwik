package net.jqwik.time.api.times.localTime.constraint;

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
		void centuries(@ForAll @Precision(value = CENTURIES) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void days(@ForAll @Precision(value = DAYS) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void decades(@ForAll @Precision(value = DECADES) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void eras(@ForAll @Precision(value = ERAS) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void forever(@ForAll @Precision(value = FOREVER) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void halfDays(@ForAll @Precision(value = HALF_DAYS) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void millennia(@ForAll @Precision(value = MILLENNIA) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void months(@ForAll @Precision(value = MONTHS) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void weeks(@ForAll @Precision(value = WEEKS) LocalTime time) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = IllegalArgumentException.class)
		void years(@ForAll @Precision(value = YEARS) LocalTime time) {
			//do nothing
		}

	}

}
