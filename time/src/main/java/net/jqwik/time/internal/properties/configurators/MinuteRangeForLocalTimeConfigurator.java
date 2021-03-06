package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class MinuteRangeForLocalTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, MinuteRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof LocalTimeArbitrary) {
			LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
			return localTimeArbitrary.minuteBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter((LocalTime) v, min, max));
		}
	}

	private boolean filter(LocalTime time, int min, int max) {
		return time.getMinute() >= min && time.getMinute() <= max;
	}

}
