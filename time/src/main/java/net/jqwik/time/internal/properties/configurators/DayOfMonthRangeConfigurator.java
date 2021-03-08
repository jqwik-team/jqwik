package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfMonthRangeConfigurator {

	public static class ForLocalDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDate.class);
		}

		public Arbitrary<LocalDate> configure(Arbitrary<LocalDate> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalDateArbitrary) {
				LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
				return localDateArbitrary.dayOfMonthBetween(min, max);
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

		public Arbitrary<Calendar> configure(Arbitrary<Calendar> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof CalendarArbitrary) {
				CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
				return calendarArbitrary.dayOfMonthBetween(min, max);
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

		public Arbitrary<Date> configure(Arbitrary<Date> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof DateArbitrary) {
				DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
				return dateArbitrary.dayOfMonthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForMonthDay extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(MonthDay.class);
		}

		public Arbitrary<MonthDay> configure(Arbitrary<MonthDay> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof MonthDayArbitrary) {
				MonthDayArbitrary monthDayArbitrary = (MonthDayArbitrary) arbitrary;
				return monthDayArbitrary.dayOfMonthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForInteger extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Integer.class);
		}

		public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof IntegerArbitrary) {
				IntegerArbitrary dayOfMonthsArbitrary = (IntegerArbitrary) arbitrary;
				return dayOfMonthsArbitrary.between(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	private static boolean filter(int dayOfMonth, int min, int max) {
		return dayOfMonth >= min && dayOfMonth <= max;
	}

	private static boolean filter(LocalDate date, int min, int max) {
		return filter(date.getDayOfMonth(), min, max);
	}

	private static boolean filter(Calendar date, int min, int max) {
		return filter(date.get(Calendar.DAY_OF_MONTH), min, max);
	}

	private static boolean filter(Date date, int min, int max) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return filter(calendar, min, max);
	}

	private static boolean filter(MonthDay monthDay, int min, int max) {
		return filter(monthDay.getDayOfMonth(), min, max);
	}

}
