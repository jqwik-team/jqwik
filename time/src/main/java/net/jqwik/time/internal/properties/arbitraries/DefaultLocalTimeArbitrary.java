package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalTimeArbitrary extends ArbitraryDecorator<LocalTime> implements LocalTimeArbitrary {

	private LocalTime timeMin = LocalTime.MIN;
	private LocalTime timeMax = LocalTime.MAX;

	private int hourMin = 0;
	private int hourMax = 23;

	private int minuteMin = 0;
	private int minuteMax = 59;

	private int secondMin = 0;
	private int secondMax = 59;

	private ChronoUnit ofPrecision = SECONDS;

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
										   .between(0L, longEnd);

		Arbitrary<LocalTime> localTimes = longs.map(v -> calculateLocalTime(v, effectiveMin));

		localTimes = localTimes
							 .filter(v -> v.getMinute() >= minuteMin && v.getMinute() <= minuteMax && v.getSecond() >= secondMin && v.getSecond() <= secondMax);

		localTimes = localTimes.edgeCases(edgeCases -> {
			edgeCases.includeOnly(effectiveMin, effectiveMax);
		});

		return localTimes;

	}

	private LocalTime calculateLocalTime(long longAdd, LocalTime effectiveMin) {
		switch (ofPrecision) {
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
		effective = calculateEffectiveMinWithPrecision(effective);
		return effective;
	}

	private LocalTime calculateEffectiveMinWithPrecision(LocalTime effective) {
		LocalTime startEffective = effective;
		int compareVal = calculateCompareValue();
		if (compareVal >= 1) {
			if (effective.getNano() % 1_000 != 0) {
				effective = effective.plusNanos(1_000 - (effective.getNano() % 1_000));
			}
			if (compareVal >= 2) {
				if (effective.getNano() % 1_000_000 != 0) {
					effective = effective.plusNanos(1_000_000 - (effective.getNano() % 1_000_000));
				}
				if (compareVal >= 3) {
					if (effective.getNano() != 0) {
						effective = effective.plusNanos(1_000_000_000 - effective.getNano());
					}
					if (compareVal >= 4) {
						if (effective.getSecond() != 0) {
							effective = effective.plusSeconds(60 - effective.getSecond());
						}
						if (compareVal >= 5) {
							if (effective.getMinute() != 0) {
								effective = effective.plusMinutes(60 - effective.getMinute());
							}
						}
					}
				}
			}
		}
		if (startEffective.isAfter(effective)) {
			throw new IllegalArgumentException("Hour is 23 and must be increased by 1.");
		}
		return effective;
	}

	private int calculateCompareValue() {
		switch (ofPrecision) {
			case HOURS:
				return 5;
			case MINUTES:
				return 4;
			case SECONDS:
				return 3;
			case MILLIS:
				return 2;
			case MICROS:
				return 1;
			default:
				return 0;
		}
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
		effective = calculateEffectiveMaxWithPrecision(effective);
		return effective;
	}

	private LocalTime calculateEffectiveMaxWithPrecision(LocalTime effective) {
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

	@Override
	public LocalTimeArbitrary atTheEarliest(LocalTime min) {
		if ((timeMax != null) && min.isAfter(timeMax)) {
			throw new IllegalArgumentException("Minimum time must not be after maximum time");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.timeMin = min;
		return clone;
	}

	@Override
	public LocalTimeArbitrary atTheLatest(LocalTime max) {
		if ((timeMin != null) && max.isBefore(timeMin)) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
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
		if (!(ofPrecision.equals(HOURS) || ofPrecision.equals(MINUTES) || ofPrecision.equals(SECONDS) || ofPrecision
																												 .equals(MILLIS) || ofPrecision
																																			.equals(MICROS) || ofPrecision
																																									   .equals(NANOS))) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}

		DefaultLocalTimeArbitrary clone = typedClone();
		clone.ofPrecision = ofPrecision;
		return clone;
	}

}
