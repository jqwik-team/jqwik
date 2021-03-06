package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PrecisionForOffsetTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(OffsetTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, Precision range) {
		ChronoUnit ofPrecision = range.ofPrecision();
		if (arbitrary instanceof OffsetTimeArbitrary) {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.ofPrecision(ofPrecision);
		} else {
			return arbitrary.filter(v -> filter((OffsetTime) v, ofPrecision));
		}
	}

	private boolean filter(OffsetTime time, ChronoUnit ofPrecision) {
		switch (ofPrecision) {
			case HOURS:
				if (time.getMinute() != 0) return false;
			case MINUTES:
				if (time.getSecond() != 0) return false;
			case SECONDS:
				if (time.getNano() != 0) return false;
				break;
			case MILLIS:
				if (time.getNano() % 1_000_000 != 0) return false;
				break;
			case MICROS:
				if (time.getNano() % 1_000 != 0) return false;
		}
		return true;
	}

}
