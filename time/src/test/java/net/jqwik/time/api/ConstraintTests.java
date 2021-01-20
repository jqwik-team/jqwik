package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

@Group
public class ConstraintTests {

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

	}

	@Group
	class CalendarConstraints {

		@Property
		void dateRangeBetween(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") Calendar calendar) {
			Calendar calendarStart = CalendarTests.getCalendar(2013, Calendar.MAY, 25);
			Calendar calendarEnd = CalendarTests.getCalendar(2020, Calendar.AUGUST, 23);
			assertThat(calendar).isGreaterThanOrEqualTo(calendarStart);
			assertThat(calendar).isLessThanOrEqualTo(calendarEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) Calendar calendar) {
			assertThat(calendar.get(Calendar.YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(calendar.get(Calendar.YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(calendar)).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) Calendar calendar) {
			assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll @DayOfWeekRange(max = DayOfWeek.MONDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll @DayOfWeekRange(min = DayOfWeek.SUNDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Calendar calendar) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar)).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

	}

	@Group
	class DateConstraints {

		@Property
		void dateRangeBetween(@ForAll @DateRange(min = "2013-05-25", max = "2020-08-23") Date date) {
			Date dateStart = DateTests.getDate(2013, Calendar.MAY, 25);
			Date dateEnd = DateTests.getDate(2020, Calendar.AUGUST, 23);
			assertThat(date).isAfterOrEqualTo(dateStart);
			assertThat(date).isBeforeOrEqualTo(dateEnd);
		}

		@Property
		void yearRangeBetween500And700(@ForAll @YearRange(min = 500, max = 700) Date date) {
			assertThat(dateToCalendar(date).get(Calendar.YEAR)).isGreaterThanOrEqualTo(500);
			assertThat(dateToCalendar(date).get(Calendar.YEAR)).isLessThanOrEqualTo(700);
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRangeBetween15And20Integer(@ForAll @DayOfMonthRange(min = 15, max = 20) Date date) {
			assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(15);
			assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRangeOnlyMonday(@ForAll @DayOfWeekRange(max = DayOfWeek.MONDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isEqualTo(DayOfWeek.MONDAY);
		}

		@Property
		void dayOfWeekRangeOnlySunday(@ForAll @DayOfWeekRange(min = DayOfWeek.SUNDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isEqualTo(DayOfWeek.SUNDAY);
		}

		@Property
		void dayOfWeekRangeBetweenTuesdayAndFriday(@ForAll @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Date date) {
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date)))
					.isGreaterThanOrEqualTo(DayOfWeek.TUESDAY);
			assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isLessThanOrEqualTo(DayOfWeek.FRIDAY);
		}

		private Calendar dateToCalendar(Date date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
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

	}

	@Group
	class MonthDayConstraints {

		@Property
		void monthDayRangeBetween(@ForAll @MonthDayRange(min = "05-25", max = "08-23") MonthDay monthDay) {
			assertThat(monthDay).isGreaterThanOrEqualTo(MonthDay.of(Month.MAY, 25));
			assertThat(monthDay).isLessThanOrEqualTo(MonthDay.of(Month.AUGUST, 23));
		}

		@Property
		void monthRangeBetweenMarchAndJuly(@ForAll @MonthRange(min = Month.MARCH, max = Month.JULY) MonthDay monthDay) {
			assertThat(monthDay.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(monthDay.getMonth()).isLessThanOrEqualTo(Month.JULY);
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
		void yearRangeBetweenMinus200And200(@ForAll @PeriodYearRange(min = -200, max = 200) Period period) {
			assertThat(period.getYears()).isGreaterThanOrEqualTo(-200);
			assertThat(period.getYears()).isLessThanOrEqualTo(200);
		}

		@Property
		void yearRangeBetween1000And2000(@ForAll @PeriodYearRange(min = 1000, max = 2000) Period period) {
			assertThat(period.getYears()).isGreaterThanOrEqualTo(1000);
			assertThat(period.getYears()).isLessThanOrEqualTo(2000);
		}

		@Property
		void monthRangeBetweenMinus200And200(@ForAll @PeriodMonthRange(min = -200, max = 200) Period period) {
			assertThat(period.getMonths()).isGreaterThanOrEqualTo(-200);
			assertThat(period.getMonths()).isLessThanOrEqualTo(200);
		}

		@Property
		void monthRangeBetween1000And2000(@ForAll @PeriodMonthRange(min = 1000, max = 2000) Period period) {
			assertThat(period.getMonths()).isGreaterThanOrEqualTo(1000);
			assertThat(period.getMonths()).isLessThanOrEqualTo(2000);
		}

		@Property
		void dayRangeBetweenMinus200And200(@ForAll @PeriodDayRange(min = -200, max = 200) Period period) {
			assertThat(period.getDays()).isGreaterThanOrEqualTo(-200);
			assertThat(period.getDays()).isLessThanOrEqualTo(200);
		}

		@Property
		void dayRangeBetween1000And2000(@ForAll @PeriodDayRange(min = 1000, max = 2000) Period period) {
			assertThat(period.getDays()).isGreaterThanOrEqualTo(1000);
			assertThat(period.getDays()).isLessThanOrEqualTo(2000);
		}

	}

}
