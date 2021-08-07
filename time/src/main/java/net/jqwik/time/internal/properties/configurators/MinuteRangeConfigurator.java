package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class MinuteRangeConfigurator {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, MinuteRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.minuteBetween(min, max);
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

		public Arbitrary<Instant> configure(Arbitrary<Instant> arbitrary, MinuteRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof InstantArbitrary) {
				InstantArbitrary instantArbitrary = (InstantArbitrary) arbitrary;
				return instantArbitrary.minuteBetween(min, max);
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

		public Arbitrary<LocalTime> configure(Arbitrary<LocalTime> arbitrary, MinuteRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof LocalTimeArbitrary) {
				LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
				return localTimeArbitrary.minuteBetween(min, max);
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

		public Arbitrary<OffsetTime> configure(Arbitrary<OffsetTime> arbitrary, MinuteRange range) {
			int min = range.min();
			int max = range.max();
			if (arbitrary instanceof OffsetTimeArbitrary) {
				OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
				return offsetTimeArbitrary.minuteBetween(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	private static boolean filter(LocalTime time, int min, int max) {
		return time.getMinute() >= min && time.getMinute() <= max;
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

	private static boolean filter(OffsetTime offsetTime, int min, int max) {
		return filter(offsetTime.toLocalTime(), min, max);
	}

}
