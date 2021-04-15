package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultLocalTimeArbitrary extends ArbitraryDecorator<LocalTime> implements LocalTimeArbitrary {

	private final LocalTimeBetween timeBetween = new LocalTimeBetween();
	private final HourBetween hourBetween = (HourBetween) new HourBetween().set(0, 23);
	private final MinuteBetween minuteBetween = (MinuteBetween) new MinuteBetween().set(0, 59);
	private final SecondBetween secondBetween = (SecondBetween) new SecondBetween().set(0, 59);
	private final OfPrecision ofPrecision = new OfPrecision();

	@Override
	protected Arbitrary<LocalTime> arbitrary() {

		LocalTime effectiveMin = calculateEffectiveMin(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		LocalTime effectiveMax = calculateEffectiveMax(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
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
			v -> v.getMinute() >= minuteBetween.getMin()
					 && v.getMinute() <= minuteBetween.getMax()
					 && v.getSecond() >= secondBetween.getMin()
					 && v.getSecond() <= secondBetween.getMax()
		);

		return localTimes;

	}

	static private LocalTime calculateLocalTime(long longAdd, LocalTime effectiveMin, OfPrecision precision) {
		switch (precision.get()) {
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
		switch (ofPrecision.get()) {
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

	public static LocalTime calculateEffectiveMin(
		LocalTimeBetween timeBetween,
		HourBetween hourBetween,
		MinuteBetween minuteBetween,
		SecondBetween secondBetween,
		OfPrecision ofPrecision
	) {
		checkMinValuesAndPrecision(minuteBetween, secondBetween, ofPrecision);
		LocalTime effective = timeBetween.getMin() != null ? timeBetween.getMin() : LocalTime.MIN;
		if (hourBetween.getMin() > effective.getHour()) {
			effective = effective.withHour(hourBetween.getMin());
			effective = effective.withMinute(0);
			effective = effective.withSecond(0);
			effective = effective.withNano(0);
			if (minuteBetween.getMin() > effective.getMinute()) {
				effective = effective.withMinute(minuteBetween.getMin());
				if (secondBetween.getMin() > effective.getSecond()) {
					effective = effective.withSecond(secondBetween.getMin());
				}
			}
		}
		effective = calculateEffectiveMinWithPrecision(effective, ofPrecision);
		return effective;
	}

	private static void checkMinValuesAndPrecision(MinuteBetween minuteBetween, SecondBetween secondBetween, OfPrecision ofPrecision) {
		if (ofPrecision.get().compareTo(SECONDS) > 0) {
			if (secondBetween.getMin() > 0) {
				throwMinValueAndPrecisionException(secondBetween.getMin(), "second", ofPrecision.get());
			}
			if (ofPrecision.get().compareTo(MINUTES) > 0 && minuteBetween.getMin() > 0) {
				throwMinValueAndPrecisionException(minuteBetween.getMin(), "minute", ofPrecision.get());
			}
		}
	}

	private static void throwMinValueAndPrecisionException(int val, String unit, ChronoUnit precision) {
		throw new IllegalArgumentException("Can't use " + val + " as minimum " + unit + " with precision " + precision + ".");
	}

	@SuppressWarnings("OverlyComplexMethod")
	public static LocalTime calculateEffectiveMinWithPrecision(LocalTime effective, OfPrecision ofPrecision) {
		LocalTime startEffective = effective;
		if (ofPrecision.get().compareTo(NANOS) >= 1) {
			if (effective.getNano() % 1_000 != 0) {
				effective = effective.plusNanos(1_000 - (effective.getNano() % 1_000));
			}
			if (ofPrecision.get().compareTo(MICROS) >= 1) {
				if (effective.getNano() % 1_000_000 != 0) {
					effective = effective.plusNanos(1_000_000 - (effective.getNano() % 1_000_000));
				}
				if (ofPrecision.get().compareTo(MILLIS) >= 1) {
					if (effective.getNano() != 0) {
						effective = effective.plusNanos(1_000_000_000 - effective.getNano());
					}
					if (ofPrecision.get().compareTo(SECONDS) >= 1) {
						if (effective.getSecond() != 0) {
							effective = effective.plusSeconds(60 - effective.getSecond());
						}
						if (ofPrecision.get().compareTo(MINUTES) >= 1) {
							if (effective.getMinute() != 0) {
								effective = effective.plusMinutes(60 - effective.getMinute());
							}
						}
					}
				}
			}
		}
		if (startEffective.isAfter(effective)) {
			throw new IllegalArgumentException("Cannot use this min value with precision " + ofPrecision);
		}
		return effective;
	}

	public static LocalTime calculateEffectiveMax(
		LocalTimeBetween timeBetween,
		HourBetween hourBetween,
		MinuteBetween minuteBetween,
		SecondBetween secondBetween,
		OfPrecision ofPrecision
	) {
		LocalTime effective = timeBetween.getMax() != null ? timeBetween.getMax() : LocalTime.MAX;
		if (hourBetween.getMax() < effective.getHour()) {
			effective = effective.withHour(hourBetween.getMax());
			effective = effective.withMinute(59);
			effective = effective.withSecond(59);
			effective = effective.withNano(999_999_999);
			if (minuteBetween.getMax() < effective.getMinute()) {
				effective = effective.withMinute(minuteBetween.getMax());
				if (secondBetween.getMax() < effective.getSecond()) {
					effective = effective.withSecond(secondBetween.getMax());
				}
			}
		}
		effective = calculateEffectiveMaxWithPrecision(effective, ofPrecision);
		return effective;
	}

	public static LocalTime calculateEffectiveMaxWithPrecision(LocalTime effective, OfPrecision ofPrecision) {
		switch (ofPrecision.get()) {
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
		ChronoUnit ofPrecision = OfPrecision.DEFAULT;
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
		if (clone.ofPrecision.isSet()) {
			return;
		}
		ChronoUnit ofPrecision = calculateOfPrecisionFromTime(time);
		if (clone.ofPrecision.get().compareTo(ofPrecision) > 0) {
			clone.ofPrecision.setProgrammatically(ofPrecision);
		}
	}

	@Override
	public LocalTimeArbitrary atTheEarliest(LocalTime min) {
		DefaultLocalTimeArbitrary clone = typedClone();
		clone.timeBetween.set(min, null);
		setOfPrecisionImplicitly(clone, min);
		return clone;
	}

	@Override
	public LocalTimeArbitrary atTheLatest(LocalTime max) {
		DefaultLocalTimeArbitrary clone = typedClone();
		clone.timeBetween.set(null, max);
		setOfPrecisionImplicitly(clone, max);
		return clone;
	}

	@Override
	public LocalTimeArbitrary hourBetween(int min, int max) {
		DefaultLocalTimeArbitrary clone = typedClone();
		clone.hourBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalTimeArbitrary minuteBetween(int min, int max) {
		DefaultLocalTimeArbitrary clone = typedClone();
		clone.minuteBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalTimeArbitrary secondBetween(int min, int max) {
		DefaultLocalTimeArbitrary clone = typedClone();
		clone.secondBetween.set(min, max);
		return clone;
	}

	@Override
	public LocalTimeArbitrary ofPrecision(ChronoUnit ofPrecision) {
		DefaultLocalTimeArbitrary clone = typedClone();
		clone.ofPrecision.set(ofPrecision);
		return clone;
	}

}
