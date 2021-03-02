package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class DatesConstraintTests {

	@Group
	class LocalDateConstraints {

		@Property
		void dateRangeBetween(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") LocalDate date) {
			assertThat(date).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
			assertThat(date).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) LocalDate date) {
			assertThat(date.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(date.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) LocalDate date) {
			assertThat(date.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(date.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) LocalDate date) {
			assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll @DayOfWeekRange(max = DayOfWeek.MONDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll @DayOfWeekRange(min = DayOfWeek.SUNDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) LocalDate date) {
			assertThat(date.getDayOfWeek()).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(date.getDayOfWeek()).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
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
	class CalendarConstraints {

		//TODO: use default generation when it is available again
		@Provide
		CalendarArbitrary dates() {
			return Dates.datesAsCalendar();
		}

		@Property
		void dateRangeBetween(@ForAll("dates") @DateRange(min = "2013-05-25", max = "2020-08-23") Calendar calendar) {
			Calendar calendarStart = CalendarTests.getCalendar(2013, Calendar.MAY, 25);
			Calendar calendarEnd = CalendarTests.getCalendar(2020, Calendar.AUGUST, 23);
			assertThat(calendar).isGreaterThanOrEqualTo(calendarStart);
			assertThat(calendar).isLessThanOrEqualTo(calendarEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll("dates") @YearRange(min = 500, max = 700) Calendar calendar) {
			assertThat(calendar.get(Calendar.YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(calendar.get(Calendar.YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.JULY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll("dates") @DayOfMonthRange(min = 15, max = 20) Calendar calendar) {
			assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll("dates") @DayOfWeekRange(max = DayOfWeek.MONDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.SUNDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionYearMonth(@ForAll("dates") @DateRange(min = "2013-05") Calendar date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionIllegalString(@ForAll("dates") @DateRange(min = "foo") Calendar date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionMonthDay(@ForAll("dates") @DateRange(max = "--05-25") Calendar date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionDay(@ForAll("dates") @DateRange(max = "13") Calendar date) {
				//do nothing
			}

		}

	}

	@Group
	class DateConstraints {

		//TODO: use default generation when it is available again
		@Provide
		DateArbitrary dates() {
			return Dates.datesAsDate();
		}

		@Property
		void dateRangeBetween(@ForAll("dates") @DateRange(min = "2013-05-25", max = "2020-08-23") Date date) {
			Date dateStart = DateTests.getDate(2013, Calendar.MAY, 25);
			Date dateEnd = DateTests.getDate(2020, Calendar.AUGUST, 23);
			assertThat(date).isAfterOrEqualTo(dateStart);
			assertThat(date).isBeforeOrEqualTo(dateEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll("dates") @YearRange(min = 500, max = 700) Date date) {
			assertThat(dateToCalendar(date).get(Calendar.YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(dateToCalendar(date).get(Calendar.YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll("dates") @MonthRange(min = Month.MARCH, max = Month.JULY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll("dates") @DayOfMonthRange(min = 15, max = 20) Date date) {
			assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll("dates") @DayOfWeekRange(max = DayOfWeek.MONDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.SUNDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll("dates") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date)))
					.isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		private Calendar dateToCalendar(Date date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionYearMonth(@ForAll("dates") @DateRange(min = "2013-05") Date date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionIllegalString(@ForAll("dates") @DateRange(min = "foo") Date date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionMonthDay(@ForAll("dates") @DateRange(max = "--05-25") Date date) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void dateRangeThrowsExceptionDay(@ForAll("dates") @DateRange(max = "13") Date date) {
				//do nothing
			}

		}

	}

	@Group
	class YearMonthConstraints {

		@Property
		void yearMonthRangeBetween(@ForAll @YearMonthRange(min = "2013-05", max = "2020-08") YearMonth yearMonth) {
			assertThat(yearMonth).isGreaterThanOrEqualTo(YearMonth.of(2013, Month.MAY));
			assertThat(yearMonth).isLessThanOrEqualTo(YearMonth.of(2020, Month.AUGUST));
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) YearMonth yearMonth) {
			assertThat(yearMonth.getYear()).isGreaterThanOrEqualTo(500);
			assertThat(yearMonth.getYear()).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) YearMonth yearMonth) {
			assertThat(yearMonth.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(yearMonth.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void noLeapYearsAreGenerated(@ForAll @LeapYears(withLeapYears = false) YearMonth yearMonth) {
			assertThat(new GregorianCalendar().isLeapYear(yearMonth.getYear())).isFalse();
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
	class MonthDayConstraints {

		@Property
		void monthDayRangeBetween(@ForAll @MonthDayRange(min = "--05-25", max = "--08-23") MonthDay monthDay) {
			assertThat(monthDay).isGreaterThanOrEqualTo(MonthDay.of(Month.MAY, 25));
			assertThat(monthDay).isLessThanOrEqualTo(MonthDay.of(Month.AUGUST, 23));
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) MonthDay monthDay) {
			assertThat(monthDay.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(monthDay.getMonth()).isLessThanOrEqualTo(Month.JULY);
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
	class YearConstraints {

		@Property
		void yearRangeBetweenMinus100And100(@ForAll @YearRange(min = -100, max = 100) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(-100);
			assertThat(year.getValue()).isLessThanOrEqualTo(100);
			assertThat(year).isNotEqualTo(Year.of(0));
		}

		@Property
		void yearRangeBetween3000And3500(@ForAll @YearRange(min = 3000, max = 3500) Year year) {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(3000);
			assertThat(year.getValue()).isLessThanOrEqualTo(3500);
		}

	}

	@Group
	class DayOfMonthConstraints {

		@Property
		void dayOfMonthRangeBetween15And20(@ForAll @DayOfMonthRange(min = 15, max = 20) int dayOfMonth) {
			assertThat(dayOfMonth).isGreaterThanOrEqualTo(15);
			assertThat(dayOfMonth).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) Integer dayOfMonth) {
			assertThat(dayOfMonth).isGreaterThanOrEqualTo(15);
			assertThat(dayOfMonth).isLessThanOrEqualTo(20);
		}

	}

	@Group
	class PeriodConstraints {

		@Property
		void defaultBetweenMinus1000And1000Years(@ForAll Period period) {
			assertThat(period.getYears()).isBetween(-1000, 1000);
		}

		@Property
		void range(@ForAll @PeriodRange(min = "P1Y2M", max = "P1Y5M3D") Period period) {
			assertThat(period.getYears()).isEqualTo(1);
			assertThat(period.getMonths()).isBetween(2, 5);
		}

		@Example
		@ExpectFailure(failureType = DateTimeParseException.class)
		void nonIsoPeriodThrowsException4(@ForAll @PeriodRange(max = "13") Period period) {
		}

	}

}
