package net.jqwik.time.api.dates.yearMonth;

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
		void yearMonthRangeBetween(@ForAll @YearMonthRange(min = "2013-05", max = "2020-08") YearMonth yearMonth) {
			assertThat(yearMonth).isAfterOrEqualTo(YearMonth.of(2013, MAY));
			assertThat(yearMonth).isBeforeOrEqualTo(YearMonth.of(2020, AUGUST));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) YearMonth yearMonth) {
			assertThat(yearMonth.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(yearMonth.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = MARCH, max = JULY) YearMonth yearMonth) {
			assertThat(yearMonth.getMonth()).isGreaterThanOrEqualTo(MARCH);
			assertThat(yearMonth.getMonth()).isLessThanOrEqualTo(JULY);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionDate(@ForAll @YearMonthRange(min = "2013-05-25") YearMonth yearMonth) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionIllegalString(@ForAll @YearMonthRange(min = "foo") YearMonth yearMonth) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionMonthDay(@ForAll @YearMonthRange(max = "--05-25") YearMonth yearMonth) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void yearMonthRangeThrowsExceptionDay(@ForAll @YearMonthRange(max = "13") YearMonth yearMonth) {
				//do nothing
			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void yearMonthRange(@ForAll @YearMonthRange(min = "2013-05", max = "2020-08") Short s) {
			assertThat(s).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void yearMonthRange(@ForAll("yearMonths") @YearMonthRange(min = "2021-03", max = "2025-06") YearMonth yearMonth) {
			assertThat(yearMonth).isBetween(YearMonth.of(2021, MARCH), YearMonth.of(2025, JUNE));
		}

		@Property
		void yearRange(@ForAll("yearMonths") @YearRange(min = 2022, max = 2025) YearMonth yearMonth) {
			assertThat(yearMonth.getYear()).isBetween(2022, 2025);
		}

		@Property
		void monthRange(@ForAll("yearMonths") @MonthRange(min = MARCH, max = MAY) YearMonth yearMonth) {
			assertThat(yearMonth.getMonth()).isBetween(MARCH, MAY);
		}

		@Provide
		Arbitrary<YearMonth> yearMonths() {
			return of(
				YearMonth.of(2020, JANUARY),
				YearMonth.of(2021, FEBRUARY),
				YearMonth.of(2021, MARCH),
				YearMonth.of(2022, MARCH),
				YearMonth.of(2023, APRIL),
				YearMonth.of(2024, MAY),
				YearMonth.of(2025, JUNE),
				YearMonth.of(2025, JULY),
				YearMonth.of(2026, JULY)
			);
		}

	}

}
