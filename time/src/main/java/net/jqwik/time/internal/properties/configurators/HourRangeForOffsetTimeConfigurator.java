package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class HourRangeForOffsetTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(OffsetTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, HourRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof OffsetTimeArbitrary) {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.hourBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter((OffsetTime) v, min, max));
		}
	}

	private boolean filter(OffsetTime offsetTime, int min, int max) {
		LocalTime time = offsetTime.toLocalTime();
		return time.getHour() >= min && time.getHour() <= max;
	}

}
