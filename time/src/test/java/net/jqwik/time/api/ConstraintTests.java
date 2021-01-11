package net.jqwik.time.api;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class ConstraintTests {

	@Group
	class DateConstraints {

		@Property
		void yearBetweenMinus500And700(@ForAll @YearRange(min = 500, max = 700) LocalDate date) {
			assertThat(date.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(date.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) LocalDate date) {
			assertThat(date.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(date.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

	}

	@Group
	class YearMonthConstraints {

		@Property
		void yearBetweenMinus500And700(@ForAll @YearRange(min = 500, max = 700) YearMonth yearMonth) {
			assertThat(yearMonth.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(yearMonth.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) YearMonth yearMonth) {
			assertThat(yearMonth.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(yearMonth.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

	}

	@Group
	class MonthDayConstraints {

		@Property
		void monthBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) MonthDay monthDay) {
			assertThat(monthDay.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(monthDay.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

	}

	@Group
	class YearConstraints {

		@Property
		void yearBetweenMinus100And100(@ForAll @YearRange(min = -100, max = 100) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(-100);
			assertThat(year.getValue()).isLessThanOrEqualTo(100);
			assertThat(year).isNotEqualTo(Year.of(0));
		}

		@Property
		void yearBetween3000And3500(@ForAll @YearRange(min = 3000, max = 3500) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(3000);
			assertThat(year.getValue()).isLessThanOrEqualTo(3500);
		}

	}

}
