package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.temporal.*;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;

public class OfPrecision {

	public final static ChronoUnit DEFAULT = SECONDS;

	private final static Set<ChronoUnit> ALLOWED_PRECISIONS = new HashSet<>(Arrays.asList(HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS));

	private ChronoUnit precision;
	private boolean precisionSet;

	public OfPrecision() {
		this(DEFAULT, false);
	}

	private OfPrecision(ChronoUnit precision, boolean precisionSet) {
		this.precision = precision;
		this.precisionSet = precisionSet;
	}

	public OfPrecision set(ChronoUnit precision) {
		return set(precision, true);
	}

	public OfPrecision setProgrammatically(ChronoUnit precision) {
		return set(precision, false);
	}

	private OfPrecision set(ChronoUnit precision, boolean precisionSet) {
		if (!ALLOWED_PRECISIONS.contains(precision)) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}
		if (this.precision.equals(precision) && this.precisionSet == precisionSet) {
			return this;
		}
		return new OfPrecision(precision, precisionSet);
	}

	public ChronoUnit get() {
		return precision;
	}

	public boolean isSet() {
		return precisionSet;
	}

}
