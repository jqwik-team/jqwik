package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PrecisionForLocalTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, Precision range) {
		ChronoUnit ofPrecision = range.ofPrecision();
		if (arbitrary instanceof LocalTimeArbitrary) {
			LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
			return localTimeArbitrary.ofPrecision(ofPrecision);
		} else {
			return arbitrary.filter(v -> filter((LocalTime) v, ofPrecision));
		}
	}

	private boolean filter(LocalTime time, ChronoUnit ofPrecision) {
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
