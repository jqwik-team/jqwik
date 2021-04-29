package net.jqwik.time.api.dateTimes.localDateTime.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
public class ConstraintsTests {

	@Group
	class Ranges {

		@Property
		void dateTimeRangeMin(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943") LocalDateTime dateTime) {
			assertThat(dateTime).isAfterOrEqualTo(LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000));
		}

		@Property
		void dateTimeRangeMax(@ForAll @DateTimeRange(max = "2020-08-23T01:32:21.113943") LocalDateTime dateTime) {
			assertThat(dateTime).isBeforeOrEqualTo(LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000));
		}

		@Property
		void dateTimeRangeDefaultNotAffectDefaultPrecision(@ForAll @DateTimeRange LocalDateTime dateTime) {
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void dateRangeMin(@ForAll @DateRange(min = "2013-05-25") LocalDateTime dateTime) {
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
		}

		@Property
		void dateRangeMax(@ForAll @DateRange(max = "2020-08-23") LocalDateTime dateTime) {
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) LocalDateTime dateTime) {
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) LocalDateTime dateTime) {
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) LocalDateTime dateTime) {
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll @DayOfWeekRange(max = DayOfWeek.MONDAY) LocalDateTime dateTime) {
			assertThat(dateTime.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll @DayOfWeekRange(min = DayOfWeek.SUNDAY) LocalDateTime dateTime) {
			assertThat(dateTime.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) LocalDateTime dateTime) {
			assertThat(dateTime.getDayOfWeek()).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(dateTime.getDayOfWeek()).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll @Precision(value = HOURS) LocalDateTime dateTime) {
			assertThat(dateTime.getMinute()).isZero();
			assertThat(dateTime.getSecond()).isZero();
			assertThat(dateTime.getNano()).isZero();
		}

		@Property
		void minutes(@ForAll @Precision(value = MINUTES) LocalDateTime dateTime) {
			assertThat(dateTime.getSecond()).isZero();
			assertThat(dateTime.getNano()).isZero();
		}

		@Property
		void seconds(@ForAll @Precision(value = SECONDS) LocalDateTime dateTime) {
			assertThat(dateTime.getNano()).isZero();
		}

		@Property
		void millis(@ForAll @Precision(value = MILLIS) LocalDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000_000).isZero();
		}

		@Property
		void micros(@ForAll @Precision(value = MICROS) LocalDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000).isZero();
		}

		@Property
		void nanos(@ForAll @Precision(value = NANOS) LocalDateTime dateTime) {
			assertThat(dateTime).isNotNull();
		}

	}

}
