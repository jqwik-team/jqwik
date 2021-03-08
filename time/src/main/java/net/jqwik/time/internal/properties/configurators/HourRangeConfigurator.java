package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class HourRangeConfigurator {

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

	private static boolean filter(OffsetTime offsetTime, int min, int max) {
		LocalTime time = offsetTime.toLocalTime();
		return filter(time, min, max);
	}

}
