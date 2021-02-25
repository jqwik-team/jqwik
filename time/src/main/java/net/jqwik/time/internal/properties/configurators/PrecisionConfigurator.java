package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PrecisionConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalTime.class)
					   || targetType.isAssignableFrom(OffsetTime.class)
					   || targetType.isAssignableFrom(Duration.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, Precision range) {
		if (arbitrary instanceof LocalTimeArbitrary) {
			LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
			return localTimeArbitrary.ofPrecision(range.ofPrecision());
		} else if (arbitrary instanceof OffsetTimeArbitrary) {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.ofPrecision(range.ofPrecision());
		} else if (arbitrary instanceof DurationArbitrary) {
			DurationArbitrary durationArbitrary = (DurationArbitrary) arbitrary;
			return durationArbitrary.ofPrecision(range.ofPrecision());
		} else {
			return arbitrary;
		}
	}
}
