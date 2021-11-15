package net.jqwik.time.api.dateTimes.zonedDateTime.constraint;

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
		void dateTimeRangeMin(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalDateTime()).isAfterOrEqualTo(LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000));
		}

		@Property
		void dateTimeRangeMax(@ForAll @DateTimeRange(max = "2020-08-23T01:32:21.113943") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalDateTime()).isBeforeOrEqualTo(LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000));
		}

		@Property
		void dateTimeRangeDefaultNotAffectDefaultPrecision(@ForAll @DateTimeRange ZonedDateTime dateTime) {
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void dateRangeMin(@ForAll @DateRange(min = "2013-05-25") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
		}

		@Property
		void dateRangeMax(@ForAll @DateRange(max = "2020-08-23") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) ZonedDateTime dateTime) {
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) ZonedDateTime dateTime) {
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) ZonedDateTime dateTime) {
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) ZonedDateTime dateTime) {
			assertThat(dateTime.getDayOfWeek()).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(dateTime.getDayOfWeek()).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		@Property
		void timeRangeMin(@ForAll @TimeRange(min = "01:32:21") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalTime()).isAfterOrEqualTo(LocalTime.of(1, 32, 21));
		}

		@Property
		void timeRangeMax(@ForAll @TimeRange(max = "03:49:32") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalTime()).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
		}

		@Property
		void hourRangeBetween(@ForAll @HourRange(min = 11, max = 13) ZonedDateTime dateTime) {
			assertThat(dateTime.getHour()).isBetween(11, 13);
		}

		@Property
		void minuteRangeBetween(@ForAll @MinuteRange(min = 11, max = 13) ZonedDateTime dateTime) {
			assertThat(dateTime.getMinute()).isBetween(11, 13);
		}

		@Property
		void secondRangeBetween(@ForAll @SecondRange(min = 11, max = 13) ZonedDateTime dateTime) {
			assertThat(dateTime.getSecond()).isBetween(11, 13);
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll @Precision(value = HOURS) ZonedDateTime dateTime) {
			assertThat(dateTime.getMinute()).isZero();
			assertThat(dateTime.getSecond()).isZero();
			assertThat(dateTime.getNano()).isZero();
		}

	}

}
