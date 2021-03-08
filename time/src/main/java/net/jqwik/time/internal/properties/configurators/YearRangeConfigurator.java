package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearRangeConfigurator {

	public static class ForLocalDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDate.class);
		}

		public Arbitrary<LocalDate> configure(Arbitrary<LocalDate> arbitrary, YearRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalDateArbitrary) {
				LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
				return localDateArbitrary.yearBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForCalendar extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Calendar.class);
		}

		public Arbitrary<Calendar> configure(Arbitrary<Calendar> arbitrary, YearRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof CalendarArbitrary) {
				CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
				return calendarArbitrary.yearBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Date.class);
		}

		public Arbitrary<Date> configure(Arbitrary<Date> arbitrary, YearRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof DateArbitrary) {
				DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
				return dateArbitrary.yearBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForYearMonth extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(YearMonth.class);
		}

		public Arbitrary<YearMonth> configure(Arbitrary<YearMonth> arbitrary, YearRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof YearMonthArbitrary) {
				YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
				return yearMonthArbitrary.yearBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForYear extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Year.class);
		}

		public Arbitrary<Year> configure(Arbitrary<Year> arbitrary, YearRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof YearArbitrary) {
				YearArbitrary yearArbitrary = (YearArbitrary) arbitrary;
				return yearArbitrary.between(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	private static boolean filter(int year, int min, int max) {
		return year >= min && year <= max;
	}

	private static boolean filter(LocalDate date, int min, int max) {
		return filter(date.getYear(), min, max);
	}

	private static boolean filter(Calendar date, int min, int max) {
		int year = date.get(Calendar.YEAR);
		if (date.get(Calendar.ERA) == GregorianCalendar.BC) {
			year *= -1;
		}
		return filter(year, min, max);
	}

	private static boolean filter(Date date, int min, int max) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return filter(calendar, min, max);
	}

	private static boolean filter(YearMonth yearMonth, int min, int max) {
		return filter(yearMonth.getYear(), min, max);
	}

	private static boolean filter(Year year, int min, int max) {
		return filter(year.getValue(), min, max);
	}

}
