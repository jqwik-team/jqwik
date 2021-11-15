package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DateTimeRangeConfigurator extends ArbitraryConfiguratorBase {

	public static class ForLocalDateTime extends ArbitraryConfiguratorBase {

		@Override
		protected boolean acceptTargetType(TypeUsage targetType) {
			return targetType.isAssignableFrom(LocalDateTime.class);
		}

		public Arbitrary<LocalDateTime> configure(Arbitrary<LocalDateTime> arbitrary, DateTimeRange range) {
			LocalDateTime min = stringToLocalDateTime(range.min());
			LocalDateTime max = stringToLocalDateTime(range.max());
			if (arbitrary instanceof LocalDateTimeArbitrary) {
				LocalDateTimeArbitrary localDateTimeArbitrary = (LocalDateTimeArbitrary) arbitrary;
				return localDateTimeArbitrary.between(min, max);
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

		public Arbitrary<OffsetDateTime> configure(Arbitrary<OffsetDateTime> arbitrary, DateTimeRange range) {
			LocalDateTime min = stringToLocalDateTime(range.min());
			LocalDateTime max = stringToLocalDateTime(range.max());
			if (arbitrary instanceof OffsetDateTimeArbitrary) {
				OffsetDateTimeArbitrary offsetDateTimeArbitrary = (OffsetDateTimeArbitrary) arbitrary;
				return offsetDateTimeArbitrary.between(min, max);
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

		public Arbitrary<ZonedDateTime> configure(Arbitrary<ZonedDateTime> arbitrary, DateTimeRange range) {
			LocalDateTime min = stringToLocalDateTime(range.min());
			LocalDateTime max = stringToLocalDateTime(range.max());
			if (arbitrary instanceof ZonedDateTimeArbitrary) {
				ZonedDateTimeArbitrary zonedDateTimeArbitrary = (ZonedDateTimeArbitrary) arbitrary;
				return zonedDateTimeArbitrary.between(min, max);
			} else {
				return arbitrary.filter(v -> filter(v, min, max));
			}
		}

	}

	private static LocalDateTime stringToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime);
	}

	private static boolean filter(LocalDateTime dateTime, LocalDateTime min, LocalDateTime max) {
		return !dateTime.isBefore(min) && !dateTime.isAfter(max);
	}

	private static boolean filter(OffsetDateTime dateTime, LocalDateTime min, LocalDateTime max) {
		return filter(dateTime.toLocalDateTime(), min, max);
	}

	private static boolean filter(ZonedDateTime dateTime, LocalDateTime min, LocalDateTime max) {
		return filter(dateTime.toLocalDateTime(), min, max);
	}

}
