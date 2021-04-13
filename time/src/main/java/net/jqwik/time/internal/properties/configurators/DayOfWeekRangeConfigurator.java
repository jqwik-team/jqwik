package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class DayOfWeekRangeConfigurator {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, DayOfWeekRange range) {
			DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.onlyDaysOfWeek(dayOfWeeks);
			} else {
				return arbitrary.filter(v -> filter(v, dayOfWeeks));
			}
		}

	}

	public static class ForLocalDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDate.class);
		}

		public Arbitrary<LocalDate> configure(Arbitrary<LocalDate> arbitrary, DayOfWeekRange range) {
			DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
			if (arbitrary instanceof LocalDateArbitrary) {
				LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
				return localDateArbitrary.onlyDaysOfWeek(dayOfWeeks);
			} else {
				return arbitrary.filter(v -> filter(v, dayOfWeeks));
			}
		}

	}

	public static class ForCalendar extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Calendar.class);
		}

		public Arbitrary<Calendar> configure(Arbitrary<Calendar> arbitrary, DayOfWeekRange range) {
			DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
			if (arbitrary instanceof CalendarArbitrary) {
				CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
				return calendarArbitrary.onlyDaysOfWeek(dayOfWeeks);
			} else {
				return arbitrary.filter(v -> filter(v, dayOfWeeks));
			}
		}

	}

	public static class ForDate extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(Date.class);
		}

		public Arbitrary<Date> configure(Arbitrary<Date> arbitrary, DayOfWeekRange range) {
			DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
			if (arbitrary instanceof DateArbitrary) {
				DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
				return dateArbitrary.onlyDaysOfWeek(dayOfWeeks);
			} else {
				return arbitrary.filter(v -> filter(v, dayOfWeeks));
			}
		}

	}

	private static DayOfWeek[] createDayOfWeekArray(DayOfWeekRange range) {
		List<DayOfWeek> dayOfWeeks = new ArrayList<>();
		for (int i = range.min().getValue(); i <= range.max().getValue(); i++) {
			dayOfWeeks.add(DayOfWeek.of(i));
		}
		return dayOfWeeks.toArray(new DayOfWeek[]{});
	}

	private static boolean filter(DayOfWeek dayOfWeek, DayOfWeek[] dayOfWeeks) {
		for (DayOfWeek dow : dayOfWeeks) {
			if (dayOfWeek == dow) {
				return true;
			}
		}
		return false;
	}

	private static boolean filter(LocalDateTime dateTime, DayOfWeek[] dayOfWeeks) {
		return filter(dateTime.getDayOfWeek(), dayOfWeeks);
	}

	private static boolean filter(LocalDate date, DayOfWeek[] dayOfWeeks) {
		return filter(date.getDayOfWeek(), dayOfWeeks);
	}

	private static boolean filter(Calendar date, DayOfWeek[] dayOfWeeks) {
		DayOfWeek dayOfWeek = DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(date.get(Calendar.DAY_OF_WEEK));
		return filter(dayOfWeek, dayOfWeeks);
	}

	private static boolean filter(Date date, DayOfWeek[] dayOfWeeks) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return filter(calendar, dayOfWeeks);
	}

}
