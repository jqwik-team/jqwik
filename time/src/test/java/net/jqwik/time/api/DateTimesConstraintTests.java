package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class DateTimesConstraintTests {

	@Group
	class LocalDateTimeConstraints {

		@Property
		void dateRangeMin(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943") LocalDateTime dateTime) {
			assertThat(dateTime).isAfterOrEqualTo(LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000));
		}

		@Property
		void dateRangeMax(@ForAll @DateTimeRange(max = "2020-08-23T01:32:21.113943") LocalDateTime dateTime) {
			assertThat(dateTime).isBeforeOrEqualTo(LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000));
		}

		@Group
		class InvalidConfigurations {

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

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void dateTimeRange(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") String string) {
			assertThat(string).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Group
		class DateTimeRangeConstraint {

			@Property
			void localDateTime(@ForAll("localDateTimes") @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") LocalDateTime dateTime) {
				LocalDateTime min = LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000);
				LocalDateTime max = LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000);
				assertThat(dateTime).isBetween(min, max);
			}

			@Provide
			Arbitrary<LocalDateTime> localDateTimes() {
				return of(
						LocalDateTime.MIN,
						LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113942000),
						LocalDateTime.of(2017, Month.FEBRUARY, 24, 2, 43, 59),
						LocalDateTime.of(2019, Month.JULY, 11, 4, 31, 48, 1),
						LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113944000),
						LocalDateTime.MAX
				);
			}

		}

	}

}
