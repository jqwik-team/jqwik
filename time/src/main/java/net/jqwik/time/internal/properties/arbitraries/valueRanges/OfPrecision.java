package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;

public class OfPrecision {

	public final static ChronoUnit DEFAULT = SECONDS;

	private final static Set<ChronoUnit> ALLOWED_PRECISIONS = new HashSet<>(Arrays.asList(HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS));

	private ChronoUnit precision = DEFAULT;
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

	public boolean valueWithPrecisionIsAllowed(int minutes, int seconds, int nanos) {
		boolean retVal = true;
		switch (precision) {
			case HOURS:
				retVal = minutes == 0;
			case MINUTES:
				retVal = retVal && seconds == 0;
			case SECONDS:
				retVal = retVal && nanos == 0;
				break;
			case MILLIS:
				retVal = (nanos % 1_000_000) == 0;
				break;
			case MICROS:
				retVal = (nanos % 1_000) == 0;
		}
		return retVal;
	}

	public Duration calculateMaxPossibleDuration() {
		switch (precision) {
			case HOURS:
				return Duration.ofSeconds((Long.MAX_VALUE / (60 * 60)) * (60 * 60), 0);
			case MINUTES:
				return Duration.ofSeconds((Long.MAX_VALUE / 60) * 60, 0);
			case MILLIS:
				return Duration.ofSeconds(Long.MAX_VALUE, 999_000_000);
			case MICROS:
				return Duration.ofSeconds(Long.MAX_VALUE, 999_999_000);
			case NANOS:
				return Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
			default:
				return Duration.ofSeconds(Long.MAX_VALUE, 0);
		}
	}

	public LocalTime calculateMaxPossibleLocalTime() {
		switch (precision) {
			case HOURS:
				return LocalTime.of(23, 0, 0, 0);
			case MINUTES:
				return LocalTime.of(23, 59, 0, 0);
			case MILLIS:
				return LocalTime.of(23, 59, 59, 999_000_000);
			case MICROS:
				return LocalTime.of(23, 59, 59, 999_999_000);
			case NANOS:
				return LocalTime.of(23, 59, 59, 999_999_999);
			default:
				return LocalTime.of(23, 59, 59, 0);
		}
	}

}
