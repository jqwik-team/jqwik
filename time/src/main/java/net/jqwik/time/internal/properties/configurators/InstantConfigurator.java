package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class InstantConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Instant.class);
	}

	public Arbitrary<Instant> configure(Arbitrary<Instant> arbitrary, InstantRange range) {
		Instant min = stringToInstant(range.min());
		Instant max = stringToInstant(range.max());
		if (arbitrary instanceof InstantArbitrary) {
			InstantArbitrary instantArbitrary = (InstantArbitrary) arbitrary;
			return instantArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private static Instant stringToInstant(String instant) {
		return Instant.parse(instant);
	}

	private static boolean filter(Instant instant, Instant min, Instant max) {
		return !instant.isBefore(min) && !instant.isAfter(max);
	}

}
