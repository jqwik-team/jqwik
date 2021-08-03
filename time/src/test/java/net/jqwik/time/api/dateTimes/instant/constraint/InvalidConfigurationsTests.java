package net.jqwik.time.api.dateTimes.instant.constraint;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

@Group
public class InvalidConfigurationsTests {

	@Group
	class InstantConstraint {

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyDateTime(@ForAll @InstantRange(min = "2013-05-25T01:32:21.113943") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyDateTime(@ForAll @InstantRange(max = "2013-05-25T01:32:21.113943") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyTime(@ForAll @InstantRange(min = "03:43:21") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyTime(@ForAll @InstantRange(max = "03:43:21") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyTimeWithT(@ForAll @InstantRange(min = "T03:43:21") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyTimeWithT(@ForAll @InstantRange(max = "T03:43:21") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyDate(@ForAll @InstantRange(min = "2013-05-25") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyDate(@ForAll @InstantRange(max = "2013-05-25") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionYearMonth(@ForAll @InstantRange(min = "2013-05") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionYearMonth(@ForAll @InstantRange(max = "2013-05") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionIllegalString(@ForAll @InstantRange(min = "foo") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionIllegalString(@ForAll @InstantRange(max = "foo") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionMonthDay(@ForAll @InstantRange(min = "--05-25") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionMonthDay(@ForAll @InstantRange(max = "--05-25") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionDay(@ForAll @InstantRange(min = "13") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionDay(@ForAll @InstantRange(max = "13") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionPicoseconds(@ForAll @InstantRange(min = "2020-08-23T01:32:21.1139432111") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionPicoseconds(@ForAll @InstantRange(max = "2020-08-23T01:32:21.1139432111") Instant instant) {
			//do nothing
		}

	}

	@Group
	class DateRangeConstraint {

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionYearMonth(@ForAll @DateRange(min = "2013-05") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionYearMonth(@ForAll @DateRange(min = "2013-05") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionIllegalString(@ForAll @DateRange(min = "foo") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionIllegalString(@ForAll @DateRange(max = "foo") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionMonthDay(@ForAll @DateRange(min = "--05-25") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionMonthDay(@ForAll @DateRange(max = "--05-25") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionDay(@ForAll @DateRange(min = "13") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionDay(@ForAll @DateRange(max = "13") Instant instant) {
			//do nothing
		}

	}

	@Group
	class TimeRangeConstraint {

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionWrongFormat(@ForAll @TimeRange(min = "1:3:5") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionWrongFormat(@ForAll @TimeRange(max = "1:3:5") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionIllegalString(@ForAll @TimeRange(min = "foo") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionIllegalString(@ForAll @TimeRange(max = "foo") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnly1Part(@ForAll @TimeRange(min = "09") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnly1Part(@ForAll @TimeRange(max = "09") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionPicoseconds(@ForAll @TimeRange(min = "09:21:30.9992123437") Instant instant) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionPicoseconds(@ForAll @TimeRange(max = "09:21:30.9992123437") Instant instant) {
			//do nothing
		}

	}

}
