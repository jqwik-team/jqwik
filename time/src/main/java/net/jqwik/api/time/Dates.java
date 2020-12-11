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

	public static YearMonthArbitrary yearMonths() {
		return new DefaultYearMonthArbitrary();
	}

	public static DateArbitrary monthDays() {
		return null;
	}

	public static DaysOfWeekArbitrary daysOfWeek() {
		return new DefaultDaysOfWeekArbitrary();
	}

}
