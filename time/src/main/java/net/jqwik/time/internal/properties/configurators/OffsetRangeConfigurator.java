package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class OffsetRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(ZoneOffset.class) || targetType.isAssignableFrom(OffsetTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, OffsetRange range) {
		if (arbitrary instanceof ZoneOffsetArbitrary) {
			ZoneOffsetArbitrary zoneOffsetArbitrary = (ZoneOffsetArbitrary) arbitrary;
			return zoneOffsetArbitrary.between(ZoneOffset.of(range.min()), ZoneOffset.of(range.max()));
		} else {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.offsetBetween(ZoneOffset.of(range.min()), ZoneOffset.of(range.max()));
		}
	}

}
