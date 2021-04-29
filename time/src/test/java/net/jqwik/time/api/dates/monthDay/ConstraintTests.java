package net.jqwik.time.api.dates.monthDay;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.of;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void monthDayRangeBetween(@ForAll @MonthDayRange(min = "--05-25", max = "--08-23") MonthDay monthDay) {
			assertThat(monthDay).isGreaterThanOrEqualTo(MonthDay.of(MAY, 25));
			assertThat(monthDay).isLessThanOrEqualTo(MonthDay.of(AUGUST, 23));
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = MARCH, max = JULY) MonthDay monthDay) {
			assertThat(monthDay.getMonth()).isGreaterThanOrEqualTo(MARCH);
			assertThat(monthDay.getMonth()).isLessThanOrEqualTo(JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) MonthDay monthDay) {
			assertThat(monthDay.getDayOfMonth()).isBetween(15, 20);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionDate(@ForAll @MonthDayRange(min = "2013-05-25") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionIllegalString(@ForAll @MonthDayRange(min = "foo") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionYearMonth(@ForAll @MonthDayRange(max = "2013-05") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionDay(@ForAll @MonthDayRange(max = "13") MonthDay monthDay) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void monthDayRangeThrowsExceptionWrongFormat(@ForAll @MonthDayRange(max = "05-25") MonthDay monthDay) {
				//do nothing
			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void monthDayRange(@ForAll @MonthDayRange(min = "--05-25", max = "--08-23") Long l) {
			assertThat(l).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void monthDayRange(@ForAll("monthDays") @MonthDayRange(min = "--02-15", max = "--06-19") MonthDay monthDay) {
			assertThat(monthDay).isBetween(MonthDay.of(FEBRUARY, 15), MonthDay.of(JUNE, 19));
		}

		@Property
		void monthRange(@ForAll("monthDays") @MonthRange(min = MARCH, max = MAY) MonthDay monthDay) {
			assertThat(monthDay.getMonth()).isBetween(MARCH, MAY);
		}

		@Property
		void dayOfMonthRange(@ForAll("monthDays") @DayOfMonthRange(min = 15, max = 19) MonthDay monthDay) {
			assertThat(monthDay.getDayOfMonth()).isBetween(15, 19);
		}

		@Provide
		Arbitrary<MonthDay> monthDays() {
			return of(
				MonthDay.of(JANUARY, 14),
				MonthDay.of(FEBRUARY, 14),
				MonthDay.of(FEBRUARY, 15),
				MonthDay.of(MARCH, 16),
				MonthDay.of(APRIL, 17),
				MonthDay.of(MAY, 18),
				MonthDay.of(JUNE, 19),
				MonthDay.of(JUNE, 20),
				MonthDay.of(JULY, 20)
			);
		}

	}

}
