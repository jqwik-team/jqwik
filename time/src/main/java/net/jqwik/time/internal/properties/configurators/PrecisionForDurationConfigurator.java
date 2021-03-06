package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PrecisionForDurationConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Duration.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, Precision range) {
		ChronoUnit ofPrecision = range.ofPrecision();
		if (arbitrary instanceof DurationArbitrary) {
			DurationArbitrary durationArbitrary = (DurationArbitrary) arbitrary;
			return durationArbitrary.ofPrecision(ofPrecision);
		} else {
			return arbitrary.filter(v -> filter((Duration) v, ofPrecision));
		}
	}

	private boolean filter(Duration duration, ChronoUnit ofPrecision) {
		switch (ofPrecision) {
			case HOURS:
				if (duration.getSeconds() % 3600 != 0) return false;
			case MINUTES:
				if (duration.getSeconds() % 60 != 0) return false;
			case SECONDS:
				if (duration.getNano() != 0) return false;
				break;
			case MILLIS:
				if (duration.getNano() % 1_000_000 != 0) return false;
				break;
			case MICROS:
				if (duration.getNano() % 1_000 != 0) return false;
		}
		return true;
	}

}
