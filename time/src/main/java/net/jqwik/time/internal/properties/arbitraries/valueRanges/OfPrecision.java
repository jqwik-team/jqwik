package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.temporal.*;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;

public class OfPrecision {

	private final static Set<ChronoUnit> ALLOWED_PRECISIONS = new HashSet<>(Arrays.asList(HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS));

	private ChronoUnit precision = SECONDS;
	private boolean precisionSet = false;

	public void set(ChronoUnit precision) {
		setProgrammatically(precision);
		this.precisionSet = true;
	}

	public void setProgrammatically(ChronoUnit precision) {
		if (!ALLOWED_PRECISIONS.contains(precision)) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}
		this.precision = precision;
	}

	public ChronoUnit get() {
		return precision;
	}

	public boolean isSet() {
		return precisionSet;
	}

}
