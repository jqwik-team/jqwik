package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalTimeArbitrary extends ArbitraryDecorator<LocalTime> implements LocalTimeArbitrary {

	public final static ChronoUnit DEFAULT_PRECISION = SECONDS;
	public final static Set<ChronoUnit> ALLOWED_PRECISIONS = new HashSet<>(Arrays.asList(HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS));

	private LocalTime timeMin = LocalTime.MIN;
	private LocalTime timeMax = LocalTime.MAX;

	private int hourMin = 0;
	private int hourMax = 23;

	private int minuteMin = 0;
	private int minuteMax = 59;

	private int secondMin = 0;
	private int secondMax = 59;

	private ChronoUnit ofPrecision = DEFAULT_PRECISION;
	private boolean ofPrecisionSet = false;

	@Override
	protected Arbitrary<LocalTime> arbitrary() {

		LocalTime effectiveMin = calculateEffectiveMin();
		LocalTime effectiveMax = calculateEffectiveMax();
		if (effectiveMax.isBefore(effectiveMin)) {
			throw new IllegalArgumentException("The maximum time is too soon after the minimum time.");
		}

		long longEnd = calculateLongEnd(effectiveMin, effectiveMax);

		Arbitrary<Long> longs = Arbitraries.longs()
										   .withDistribution(RandomDistribution.uniform())
										   .between(0L, longEnd)
										   .edgeCases(config -> config.includeOnly(0L, longEnd));

		Arbitrary<LocalTime> localTimes = longs.map(v -> calculateLocalTime(v, effectiveMin, ofPrecision));

		localTimes = localTimes.filter(
				v -> v.getMinute() >= minuteMin
							 && v.getMinute() <= minuteMax
							 && v.getSecond() >= secondMin
							 && v.getSecond() <= secondMax
		);

		return localTimes;

	}

	static private LocalTime calculateLocalTime(long longAdd, LocalTime effectiveMin, ChronoUnit precision) {
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

	private long calculateLongEnd(LocalTime effectiveMin, LocalTime effectiveMax) {
		switch (ofPrecision) {
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

	private LocalTime calculateEffectiveMin() {
		LocalTime effective = timeMin;
		if (hourMin > effective.getHour()) {
			effective = effective.withHour(hourMin);
			if (minuteMin > effective.getMinute()) {
				effective = effective.withMinute(minuteMin);
				if (secondMin > effective.getSecond()) {
					effective = effective.withSecond(secondMin);
					effective = effective.withNano(0);
				}
			}
		}
		effective = calculateEffectiveMinWithPrecision(effective, ofPrecision);
		return effective;
	}

	@SuppressWarnings("OverlyComplexMethod")
	public static LocalTime calculateEffectiveMinWithPrecision(LocalTime effective, ChronoUnit precision) {
		LocalTime startEffective = effective;
		if (precision.compareTo(NANOS) >= 1) {
			if (effective.getNano() % 1_000 != 0) {
				effective = effective.plusNanos(1_000 - (effective.getNano() % 1_000));
			}
			if (precision.compareTo(MICROS) >= 1) {
				if (effective.getNano() % 1_000_000 != 0) {
					effective = effective.plusNanos(1_000_000 - (effective.getNano() % 1_000_000));
				}
				if (precision.compareTo(MILLIS) >= 1) {
					if (effective.getNano() != 0) {
						effective = effective.plusNanos(1_000_000_000 - effective.getNano());
					}
					if (precision.compareTo(SECONDS) >= 1) {
						if (effective.getSecond() != 0) {
							effective = effective.plusSeconds(60 - effective.getSecond());
						}
						if (precision.compareTo(MINUTES) >= 1) {
							if (effective.getMinute() != 0) {
								effective = effective.plusMinutes(60 - effective.getMinute());
							}
						}
					}
				}
			}
		}
		if (startEffective.isAfter(effective)) {
			throw new IllegalArgumentException("Cannot use this min value with precision " + precision);
		}
		return effective;
	}

	private LocalTime calculateEffectiveMax() {
		LocalTime effective = timeMax;
		if (hourMax < effective.getHour()) {
			effective = effective.withHour(hourMax);
			if (minuteMax < effective.getMinute()) {
				effective = effective.withMinute(minuteMax);
				if (secondMax < effective.getSecond()) {
					effective = effective.withSecond(secondMax);
					effective = effective.withNano(999_999_999);
				}
			}
		}
		effective = calculateEffectiveMaxWithPrecision(effective, ofPrecision);
		return effective;
	}

	public static LocalTime calculateEffectiveMaxWithPrecision(LocalTime effective, ChronoUnit ofPrecision) {
		switch (ofPrecision) {
			case HOURS:
				effective = effective.withMinute(0);
			case MINUTES:
				effective = effective.withSecond(0);
			case SECONDS:
				effective = effective.withNano(effective.getNano() % 1_000_000);
			case MILLIS:
				effective = effective.withNano((effective.getNano() / 1_000_000) * 1_000_000 + effective.getNano() % 1_000);
			case MICROS:
				effective = effective.withNano(effective.getNano() - effective.getNano() % 1_000);
		}
		return effective;
	}

	public static ChronoUnit calculateOfPrecisionFromNanos(int nanos) {
		ChronoUnit ofPrecision = DEFAULT_PRECISION;
		if (nanos % 1_000 != 0) {
			ofPrecision = NANOS;
		} else if ((nanos / 1_000) % 1_000 != 0) {
			ofPrecision = MICROS;
		} else if (nanos / 1_000_000 != 0) {
			ofPrecision = MILLIS;
		}
		return ofPrecision;
	}

	public static ChronoUnit calculateOfPrecisionFromTime(LocalTime time) {
		int nanos = time.getNano();
		return calculateOfPrecisionFromNanos(nanos);
	}

	private void setOfPrecisionImplicitly(DefaultLocalTimeArbitrary clone, LocalTime time) {
		if (clone.ofPrecisionSet) {
			return;
		}
		ChronoUnit ofPrecision = calculateOfPrecisionFromTime(time);
		if (clone.ofPrecision.compareTo(ofPrecision) > 0) {
			clone.ofPrecision = ofPrecision;
		}
	}

	@Override
	public LocalTimeArbitrary atTheEarliest(LocalTime min) {
		if ((timeMax != null) && min.isAfter(timeMax)) {
			throw new IllegalArgumentException("Minimum time must not be after maximum time");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		setOfPrecisionImplicitly(clone, min);
		clone.timeMin = min;
		return clone;
	}

	@Override
	public LocalTimeArbitrary atTheLatest(LocalTime max) {
		if ((timeMin != null) && max.isBefore(timeMin)) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		setOfPrecisionImplicitly(clone, max);
		clone.timeMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary hourBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 23) {
			throw new IllegalArgumentException("Hour value must be between 0 and 23.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.hourMin = min;
		clone.hourMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary minuteBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Minute value must be between 0 and 59.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.minuteMin = min;
		clone.minuteMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary secondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Second value must be between 0 and 59.");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.secondMin = min;
		clone.secondMax = max;
		return clone;
	}

	@Override
	public LocalTimeArbitrary ofPrecision(ChronoUnit ofPrecision) {
		if (!ALLOWED_PRECISIONS.contains(ofPrecision)) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.ofPrecisionSet = true;
		clone.ofPrecision = ofPrecision;
		return clone;
	}

}
