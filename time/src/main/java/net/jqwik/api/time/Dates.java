package net.jqwik.api.time;

import net.jqwik.time.*;

public class Dates {

	private Dates() {
		// Must never be called
	}

	public static DateArbitrary dates() {
		return new DefaultDateArbitrary();
	}

	public static YearArbitrary years() {
		return new DefaultYearArbitrary();
	}

	public static MonthArbitrary months() {
		return new DefaultMonthArbitrary();
	}

	public static DaysOfMonthArbitrary daysOfMonth() {
		return new DefaultDaysOfMonthArbitrary();
	}

	public static DateArbitrary yearsAndMonths() {
		return null;
	}

	public static DateArbitrary monthsAndDays() {
		return null;
	}

	public static DaysOfWeekArbitrary daysOfWeek() {
		return new DefaultDaysOfWeekArbitrary();
	}

}
