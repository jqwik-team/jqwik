package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DurationRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Duration.class);
	}

	public Arbitrary<?> configure(Arbitrary<Duration> arbitrary, DurationRange range) {
		Duration min = Duration.parse(range.min());
		Duration max = Duration.parse(range.max());
		if (arbitrary instanceof DurationArbitrary) {
			DurationArbitrary durationArbitrary = (DurationArbitrary) arbitrary;
			return durationArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(Duration duration, Duration min, Duration max) {
		return duration.compareTo(min) >= 0 && duration.compareTo(max) <= 0;
	}

}
