package net.jqwik.time.api.dateTimes.localDateTime.constraint;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

@Group
public class InvalidConfigurationsTests {

	@Group
	class DateTimeRangeConstraint {

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyTime(@ForAll @DateTimeRange(min = "03:43:21") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyTime(@ForAll @DateTimeRange(max = "03:43:21") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyTimeWithT(@ForAll @DateTimeRange(min = "T03:43:21") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyTimeWithT(@ForAll @DateTimeRange(max = "T03:43:21") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnlyDate(@ForAll @DateTimeRange(min = "2013-05-25") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnlyDate(@ForAll @DateTimeRange(max = "2013-05-25") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionYearMonth(@ForAll @DateTimeRange(min = "2013-05") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionYearMonth(@ForAll @DateTimeRange(max = "2013-05") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionIllegalString(@ForAll @DateTimeRange(min = "foo") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionIllegalString(@ForAll @DateTimeRange(max = "foo") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionMonthDay(@ForAll @DateTimeRange(min = "--05-25") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionMonthDay(@ForAll @DateTimeRange(max = "--05-25") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionDay(@ForAll @DateTimeRange(min = "13") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionDay(@ForAll @DateTimeRange(max = "13") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionPicoseconds(@ForAll @DateTimeRange(min = "2020-08-23T01:32:21.1139432111") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionPicoseconds(@ForAll @DateTimeRange(max = "2020-08-23T01:32:21.1139432111") LocalDateTime dateTime) {
			//do nothing
		}

	}

	@Group
	class DateRangeConstraint {

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionYearMonth(@ForAll @DateRange(min = "2013-05") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionYearMonth(@ForAll @DateRange(min = "2013-05") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionIllegalString(@ForAll @DateRange(min = "foo") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionIllegalString(@ForAll @DateRange(max = "foo") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionMonthDay(@ForAll @DateRange(min = "--05-25") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionMonthDay(@ForAll @DateRange(max = "--05-25") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionDay(@ForAll @DateRange(min = "13") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionDay(@ForAll @DateRange(max = "13") LocalDateTime dateTime) {
			//do nothing
		}

	}

	@Group
	class TimeRangeConstraint {

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionWrongFormat(@ForAll @TimeRange(min = "1:3:5") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionWrongFormat(@ForAll @TimeRange(max = "1:3:5") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionIllegalString(@ForAll @TimeRange(min = "foo") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionIllegalString(@ForAll @TimeRange(max = "foo") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionOnly1Part(@ForAll @TimeRange(min = "09") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionOnly1Part(@ForAll @TimeRange(max = "09") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void minThrowsExceptionPicoseconds(@ForAll @TimeRange(min = "09:21:30.9992123437") LocalDateTime dateTime) {
			//do nothing
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void maxThrowsExceptionPicoseconds(@ForAll @TimeRange(max = "09:21:30.9992123437") LocalDateTime dateTime) {
			//do nothing
		}

	}

}
