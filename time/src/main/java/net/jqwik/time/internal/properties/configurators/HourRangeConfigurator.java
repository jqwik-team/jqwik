package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class HourRangeConfigurator {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, HourRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.hourBetween(min, max);
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

		public Arbitrary<Instant> configure(Arbitrary<Instant> arbitrary, HourRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof InstantArbitrary) {
				InstantArbitrary instantArbitrary = (InstantArbitrary) arbitrary;
				return instantArbitrary.hourBetween(min, max);
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

		public Arbitrary<OffsetDateTime> configure(Arbitrary<OffsetDateTime> arbitrary, HourRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof OffsetDateTimeArbitrary) {
				OffsetDateTimeArbitrary offsetDateTimeArbitrary = (OffsetDateTimeArbitrary) arbitrary;
				return offsetDateTimeArbitrary.hourBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForLocalTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalTime.class);
		}

		public Arbitrary<LocalTime> configure(Arbitrary<LocalTime> arbitrary, HourRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalTimeArbitrary) {
				LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
				return localTimeArbitrary.hourBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	public static class ForOffsetTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(OffsetTime.class);
		}

		public Arbitrary<OffsetTime> configure(Arbitrary<OffsetTime> arbitrary, HourRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof OffsetTimeArbitrary) {
				OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
				return offsetTimeArbitrary.hourBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	private static boolean filter(LocalTime time, int min, int max) {
		return time.getHour() >= min && time.getHour() <= max;
	}

	private static boolean filter(LocalDateTime dateTime, int min, int max) {
		return filter(dateTime.toLocalTime(), min, max);
	}

	private static boolean filter(Instant instant, int min, int max) {
		if (LocalDateTime.MIN.toInstant(ZoneOffset.UTC).isAfter(instant) || LocalDateTime.MAX.toInstant(ZoneOffset.UTC).isBefore(instant)) {
			return false;
		}
		return filter(DefaultInstantArbitrary.instantToLocalDateTime(instant).toLocalTime(), min, max);
	}

	private static boolean filter(OffsetDateTime dateTime, int min, int max) {
		return filter(dateTime.toLocalTime(), min, max);
	}

	private static boolean filter(OffsetTime offsetTime, int min, int max) {
		return filter(offsetTime.toLocalTime(), min, max);
	}

}
