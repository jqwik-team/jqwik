package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class LeapYearsConfigurator {

	public static class ForLocalDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDate.class);
		}

		public Arbitrary<LocalDate> configure(Arbitrary<LocalDate> arbitrary, LeapYears leapYears) {
			boolean withLeapYears = leapYears.withLeapYears();
			if (arbitrary instanceof LocalDateArbitrary) {
				LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
				return localDateArbitrary.leapYears(withLeapYears);
			} else {
				return arbitrary.filter(v -> filter(v, withLeapYears));
			}
		}

	}

	public static class ForCalendar extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Calendar.class);
		}

		public Arbitrary<Calendar> configure(Arbitrary<Calendar> arbitrary, LeapYears leapYears) {
			boolean withLeapYears = leapYears.withLeapYears();
			if (arbitrary instanceof CalendarArbitrary) {
				CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
				return calendarArbitrary.leapYears(withLeapYears);
			} else {
				return arbitrary.filter(v -> filter(v, withLeapYears));
			}
		}

	}

	public static class ForDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Date.class);
		}

		public Arbitrary<Date> configure(Arbitrary<Date> arbitrary, LeapYears leapYears) {
			boolean withLeapYears = leapYears.withLeapYears();
			if (arbitrary instanceof DateArbitrary) {
				DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
				return dateArbitrary.leapYears(withLeapYears);
			} else {
				return arbitrary.filter(v -> filter(v, withLeapYears));
			}
		}

	}

	public static class ForYearMonth extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(YearMonth.class);
		}

		public Arbitrary<YearMonth> configure(Arbitrary<YearMonth> arbitrary, LeapYears leapYears) {
			boolean withLeapYears = leapYears.withLeapYears();
			if (arbitrary instanceof YearMonthArbitrary) {
				YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
				return yearMonthArbitrary.leapYears(leapYears.withLeapYears());
			} else {
				return arbitrary.filter(v -> filter(v, withLeapYears));
			}
		}

	}

	private static boolean filter(int year, boolean withLeapYears) {
		return withLeapYears || !(new GregorianCalendar().isLeapYear(year));
	}

	private static boolean filter(LocalDate date, boolean withLeapYears) {
		return filter(date.getYear(), withLeapYears);
	}

	private static boolean filter(Calendar date, boolean withLeapYears) {
		int year = date.get(Calendar.YEAR);
		if (date.get(Calendar.ERA) == GregorianCalendar.BC) {
			year = -year;
		}
		return filter(year, withLeapYears);
	}

	private static boolean filter(Date date, boolean withLeapYears) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return filter(calendar, withLeapYears);
	}

	private static boolean filter(YearMonth yearMonth, boolean withLeapYears) {
		return filter(yearMonth.getYear(), withLeapYears);
	}

}
