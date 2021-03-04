package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class HourRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalTime.class) || targetType.isAssignableFrom(OffsetTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, HourRange range) {
		if (arbitrary instanceof LocalTimeArbitrary) {
			LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
			return localTimeArbitrary.hourBetween(range.min(), range.max());
		} else if (arbitrary instanceof OffsetTimeArbitrary) {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.hourBetween(range.min(), range.max());
		} else {
			return arbitrary;
		}
	}
}
