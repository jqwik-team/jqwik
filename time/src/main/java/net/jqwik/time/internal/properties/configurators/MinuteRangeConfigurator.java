package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class MinuteRangeConfigurator {

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

	private static boolean filter(OffsetTime offsetTime, int min, int max) {
		LocalTime time = offsetTime.toLocalTime();
		return filter(time, min, max);
	}

}
