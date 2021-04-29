package net.jqwik.time.api.dates.localDate;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.DayOfWeek.*;
import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.of;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void dateRangeBetween(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") LocalDate date) {
			assertThat(date).isAfterOrEqualTo(LocalDate.of(2013, MAY, 25));
			assertThat(date).isBeforeOrEqualTo(LocalDate.of(2020, AUGUST, 23));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) LocalDate date) {
			assertThat(date.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(date.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = MARCH, max = JULY) LocalDate date) {
			assertThat(date.getMonth()).isGreaterThanOrEqualTo(MARCH);
			assertThat(date.getMonth()).isLessThanOrEqualTo(JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) LocalDate date) {
			assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll @DayOfWeekRange(max = MONDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isEqualTo(MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll @DayOfWeekRange(min = SUNDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isEqualTo(SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = TUESDAY, max = FRIDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isGreaterThanOrEqualTo(TUESDAY);
			assertThat(date.getDayOfWeek()).isLessThanOrEqualTo(FRIDAY);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionYearMonth(@ForAll @DateRange(min = "2013-05") LocalDate date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionIllegalString(@ForAll @DateRange(min = "foo") LocalDate date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionMonthDay(@ForAll @DateRange(max = "--05-25") LocalDate date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionDay(@ForAll @DateRange(max = "13") LocalDate date) {
				//do nothing
			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void dateRange(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") String string) {
			assertThat(string).isNotNull();
		}

		@Property
		void monthRange(@ForAll @MonthRange(min = MARCH, max = JULY) Boolean b) {
			assertThat(b).isNotNull();
		}

		@Property
		void dayOfWeekRange(@ForAll @DayOfWeekRange char c) {
			assertThat(c).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Property
		void dateRange(@ForAll("localDates") @DateRange(min = "2021-02-15", max = "2021-02-19") LocalDate date) {
			assertThat(date).isBetween(LocalDate.of(2021, FEBRUARY, 15), LocalDate.of(2021, FEBRUARY, 19));
		}

		@Property
		void yearRange(@ForAll("localDates") @YearRange(min = 2022, max = 2025) LocalDate date) {
			assertThat(date.getYear()).isBetween(2022, 2025);
		}

		@Property
		void monthRange(@ForAll("localDates") @MonthRange(min = MARCH, max = MAY) LocalDate date) {
			assertThat(date.getMonth()).isBetween(MARCH, MAY);
		}

		@Property
		void dayOfMonthRange(@ForAll("localDates") @DayOfMonthRange(min = 15, max = 19) LocalDate date) {
			assertThat(date.getDayOfMonth()).isBetween(15, 19);
		}

		@Property
		void dayOfWeekRange(@ForAll("localDates") @DayOfWeekRange(min = TUESDAY, max = FRIDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isBetween(TUESDAY, FRIDAY);
		}

		@Provide
		Arbitrary<LocalDate> localDates() {
			return of(
				LocalDate.of(2021, FEBRUARY, 14),
				LocalDate.of(2021, FEBRUARY, 15),
				LocalDate.of(2021, FEBRUARY, 16),
				LocalDate.of(2021, FEBRUARY, 17),
				LocalDate.of(2021, FEBRUARY, 18),
				LocalDate.of(2021, FEBRUARY, 19),
				LocalDate.of(2021, FEBRUARY, 20),
				LocalDate.of(2022, MARCH, 15),
				LocalDate.of(2023, APRIL, 16),
				LocalDate.of(2024, MAY, 17),
				LocalDate.of(2025, JUNE, 18),
				LocalDate.of(2026, JULY, 19),
				LocalDate.of(2027, AUGUST, 20)
			);
		}

	}

}
