package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class MonthRangeConfigurator {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.monthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForInstant extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Instant.class);
		}

		public Arbitrary<Instant> configure(Arbitrary<Instant> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof InstantArbitrary) {
				InstantArbitrary instantArbitrary = (InstantArbitrary) arbitrary;
				return instantArbitrary.monthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForOffsetDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(OffsetDateTime.class);
		}

		public Arbitrary<OffsetDateTime> configure(Arbitrary<OffsetDateTime> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof OffsetDateTimeArbitrary) {
				OffsetDateTimeArbitrary offsetDateTimeArbitrary = (OffsetDateTimeArbitrary) arbitrary;
				return offsetDateTimeArbitrary.monthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForLocalDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDate.class);
		}

		public Arbitrary<LocalDate> configure(Arbitrary<LocalDate> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof LocalDateArbitrary) {
				LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
				return localDateArbitrary.monthBetween(min, max);
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

		public Arbitrary<Calendar> configure(Arbitrary<Calendar> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof CalendarArbitrary) {
				CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
				return calendarArbitrary.monthBetween(min, max);
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

		public Arbitrary<Date> configure(Arbitrary<Date> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof DateArbitrary) {
				DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
				return dateArbitrary.monthBetween(min, max);
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

		public Arbitrary<YearMonth> configure(Arbitrary<YearMonth> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof YearMonthArbitrary) {
				YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
				return yearMonthArbitrary.monthBetween(min, max);
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

		public Arbitrary<MonthDay> configure(Arbitrary<MonthDay> arbitrary, MonthRange range) {
			Month min = range.min();
			Month max = range.max();
			if (arbitrary instanceof MonthDayArbitrary) {
				MonthDayArbitrary monthDayArbitrary = (MonthDayArbitrary) arbitrary;
				return monthDayArbitrary.monthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	private static boolean filter(Month month, Month min, Month max) {
		return month.compareTo(min) >= 0 && month.compareTo(max) <= 0;
	}

	private static boolean filter(LocalDateTime dateTime, Month min, Month max) {
		return filter(dateTime.getMonth(), min, max);
	}

	private static boolean filter(Instant instant, Month min, Month max) {
		if (LocalDateTime.MIN.toInstant(ZoneOffset.UTC).isAfter(instant) || LocalDateTime.MAX.toInstant(ZoneOffset.UTC).isBefore(instant)) {
			return false;
		}
		return filter(DefaultInstantArbitrary.instantToLocalDateTime(instant), min, max);
	}

	private static boolean filter(OffsetDateTime dateTime, Month min, Month max) {
		return filter(dateTime.getMonth(), min, max);
	}

	private static boolean filter(LocalDate date, Month min, Month max) {
		return filter(date.getMonth(), min, max);
	}

	private static boolean filter(Calendar date, Month min, Month max) {
		Month month = DefaultCalendarArbitrary.calendarMonthToMonth(date);
		return filter(month, min, max);
	}

	private static boolean filter(Date date, Month min, Month max) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return filter(calendar, min, max);
	}

	private static boolean filter(YearMonth yearMonth, Month min, Month max) {
		return filter(yearMonth.getMonth(), min, max);
	}

	private static boolean filter(MonthDay monthDay, Month min, Month max) {
		return filter(monthDay.getMonth(), min, max);
	}

}
