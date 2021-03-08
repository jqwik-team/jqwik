package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class OffsetRangeForOffsetTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(OffsetTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<OffsetTime> arbitrary, OffsetRange range) {
		ZoneOffset min = ZoneOffset.of(range.min());
		ZoneOffset max = ZoneOffset.of(range.max());
		if (arbitrary instanceof OffsetTimeArbitrary) {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.offsetBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(OffsetTime offsetTime, ZoneOffset min, ZoneOffset max) {
		ZoneOffset offset = offsetTime.getOffset();
		if (min.getTotalSeconds() > max.getTotalSeconds()) {
			ZoneOffset remember = min;
			min = max;
			max = remember;
		}
		return offset.getTotalSeconds() >= min.getTotalSeconds() && offset.getTotalSeconds() <= max.getTotalSeconds();
	}

}
