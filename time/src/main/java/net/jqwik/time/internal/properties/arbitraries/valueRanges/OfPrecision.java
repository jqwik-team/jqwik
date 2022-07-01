package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.math.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;

public class OfPrecision {

	public final static ChronoUnit DEFAULT = SECONDS;

	private final static Set<ChronoUnit> ALLOWED_PRECISIONS = new LinkedHashSet<>(Arrays.asList(HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS));

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

	public boolean isGreatherThan(ChronoUnit precision) {
		return this.precision.compareTo(precision) > 0;
	}

	public boolean isLessOrEqualTo(ChronoUnit precision) {
		return this.precision.compareTo(precision) <= 0;
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

	public Duration maxPossibleDuration() {
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

	public LocalTime maxPossibleLocalTime() {
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

	public long longsBetween(LocalTime effectiveMin, LocalTime effectiveMax) {
		switch (precision) {
			case HOURS:
				return HOURS.between(effectiveMin, effectiveMax);
			case MINUTES:
				return MINUTES.between(effectiveMin, effectiveMax);
			case SECONDS:
				return SECONDS.between(effectiveMin, effectiveMax);
			case MILLIS:
				return MILLIS.between(effectiveMin, effectiveMax);
			case MICROS:
				return MICROS.between(effectiveMin, effectiveMax);
			default:
				return NANOS.between(effectiveMin, effectiveMax);
		}
	}

	public LocalTime localTimeFromValue(long longAdd, LocalTime effectiveMin) {
		switch (precision) {
			case HOURS:
				return effectiveMin.plusHours(longAdd);
			case MINUTES:
				return effectiveMin.plusMinutes(longAdd);
			case SECONDS:
				return effectiveMin.plusSeconds(longAdd);
			case MILLIS:
				longAdd *= 1_000;
			case MICROS:
				longAdd *= 1_000;
			default:
				return effectiveMin.plusNanos(longAdd);
		}
	}

	public LocalTime effectiveMaxNanos(LocalTime effective) {
		switch (precision) {
			case MILLIS:
				effective = effective.withNano(999_000_000);
				break;
			case MICROS:
				effective = effective.withNano(999_999_000);
				break;
			case NANOS:
				effective = effective.withNano(999_999_999);
				break;
			default:
				effective = effective.withNano(0);
		}
		return effective;
	}

	public Duration minPossibleDuration() {
		switch (precision) {
			case HOURS:
				return Duration.ofSeconds((Long.MIN_VALUE / (60 * 60)) * (60 * 60), 0);
			case MINUTES:
				return Duration.ofSeconds((Long.MIN_VALUE / 60) * 60, 0);
			default:
				return Duration.ofSeconds(Long.MIN_VALUE, 0);
		}
	}

	public Duration durationFromValue(BigInteger bigInteger) {

		BigInteger helperDivide = new BigInteger(1_000_000_000 + "");
		BigInteger helperMultiply1000 = new BigInteger(1_000 + "");
		BigInteger helperMultiply60 = new BigInteger("60");

		switch (precision) {
			case HOURS:
				bigInteger = bigInteger.multiply(helperMultiply60);
			case MINUTES:
				bigInteger = bigInteger.multiply(helperMultiply60);
			case SECONDS:
				bigInteger = bigInteger.multiply(helperMultiply1000);
			case MILLIS:
				bigInteger = bigInteger.multiply(helperMultiply1000);
			case MICROS:
				bigInteger = bigInteger.multiply(helperMultiply1000);
		}

		BigInteger bigIntegerSeconds = bigInteger.divide(helperDivide);
		long seconds = bigIntegerSeconds.longValue();
		int nanos = bigInteger.subtract(bigIntegerSeconds.multiply(helperDivide)).intValue();

		return Duration.ofSeconds(seconds, nanos);

	}

}
