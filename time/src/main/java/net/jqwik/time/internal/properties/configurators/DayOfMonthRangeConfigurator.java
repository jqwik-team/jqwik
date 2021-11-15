package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class DayOfMonthRangeConfigurator {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.dayOfMonthBetween(min, max);
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

		public Arbitrary<Instant> configure(Arbitrary<Instant> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof InstantArbitrary) {
				InstantArbitrary instantArbitrary = (InstantArbitrary) arbitrary;
				return instantArbitrary.dayOfMonthBetween(min, max);
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

		public Arbitrary<OffsetDateTime> configure(Arbitrary<OffsetDateTime> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof OffsetDateTimeArbitrary) {
				OffsetDateTimeArbitrary offsetDateTimeArbitrary = (OffsetDateTimeArbitrary) arbitrary;
				return offsetDateTimeArbitrary.dayOfMonthBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForZonedDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(ZonedDateTime.class);
		}

		public Arbitrary<ZonedDateTime> configure(Arbitrary<ZonedDateTime> arbitrary, DayOfMonthRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof ZonedDateTimeArbitrary) {
				ZonedDateTimeArbitrary zonedDateTimeArbitrary = (ZonedDateTimeArbitrary) arbitrary;
				return zonedDateTimeArbitrary.dayOfMonthBetween(min, max);
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

	private static boolean filter(LocalDateTime dateTime, int min, int max) {
		return filter(dateTime.getDayOfMonth(), min, max);
	}

	private static boolean filter(Instant instant, int min, int max) {
		if (LocalDateTime.MIN.toInstant(ZoneOffset.UTC).isAfter(instant) || LocalDateTime.MAX.toInstant(ZoneOffset.UTC).isBefore(instant)) {
			return false;
		}
		return filter(DefaultInstantArbitrary.instantToLocalDateTime(instant).getDayOfMonth(), min, max);
	}

	private static boolean filter(OffsetDateTime dateTime, int min, int max) {
		return filter(dateTime.getDayOfMonth(), min, max);
	}

	private static boolean filter(ZonedDateTime dateTime, int min, int max) {
		return filter(dateTime.getDayOfMonth(), min, max);
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
