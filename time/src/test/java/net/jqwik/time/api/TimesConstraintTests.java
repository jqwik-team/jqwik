package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class TimesConstraintTests {

	@Group
	class LocalTimeConstraints {

		@Property
		void timeRangeBetween(@ForAll @TimeRange(min = "01:32:21.113943", max = "03:49:32") LocalTime time) {
			assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943));
			assertThat(time).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
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

		}

	}

	@Group
	class OffsetTimeConstraints {

		@Property
		void timeRangeBetween(@ForAll @TimeRange(min = "01:32:21.113943", max = "03:49:32") OffsetTime time) {
			assertThat(time.toLocalTime()).isAfterOrEqualTo(LocalTime.of(1, 32, 21, 113943));
			assertThat(time.toLocalTime()).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
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

		}

	}

}
