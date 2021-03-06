package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class OffsetRangeForZoneOffsetConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(ZoneOffset.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, OffsetRange range) {
		ZoneOffset min = ZoneOffset.of(range.min());
		ZoneOffset max = ZoneOffset.of(range.max());
		if (arbitrary instanceof ZoneOffsetArbitrary) {
			ZoneOffsetArbitrary zoneOffsetArbitrary = (ZoneOffsetArbitrary) arbitrary;
			return zoneOffsetArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter((ZoneOffset) v, min, max));
		}
	}

	private boolean filter(ZoneOffset offset, ZoneOffset min, ZoneOffset max) {
		if (min.getTotalSeconds() > max.getTotalSeconds()) {
			ZoneOffset remember = min;
			min = max;
			max = remember;
		}
		return offset.getTotalSeconds() >= min.getTotalSeconds() && offset.getTotalSeconds() <= max.getTotalSeconds();
	}

}
