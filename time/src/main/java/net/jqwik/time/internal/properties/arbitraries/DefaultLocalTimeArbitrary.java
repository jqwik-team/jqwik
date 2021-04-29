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
	private OfPrecision ofPrecision = new OfPrecision();

	@Override
	protected Arbitrary<LocalTime> arbitrary() {

		LocalTime effectiveMin = calculateEffectiveMin(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		LocalTime effectiveMax = calculateEffectiveMax(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);

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
		checkTimeValueAndPrecision(effective, ofPrecision, true);
		if (hourBetween.getMin() > effective.getHour()) {
			effective = effective.withHour(hourBetween.getMin());
			effective = effective.withMinute(0);
			effective = effective.withSecond(0);
			effective = effective.withNano(0);
		}
		if (minuteBetween.getMin() > effective.getMinute()) {
			effective = effective.withMinute(minuteBetween.getMin());
			effective = effective.withSecond(0);
			effective = effective.withNano(0);
		}
		if (secondBetween.getMin() > effective.getSecond()) {
			effective = effective.withSecond(secondBetween.getMin());
			effective = effective.withNano(0);
		}
		return effective;
	}

	public static void checkTimeValueAndPrecision(LocalTime time, OfPrecision ofPrecision, boolean minimum) {
		boolean throwException = false;
		switch (ofPrecision.get()) {
			case HOURS:
				throwException = time.getMinute() != 0;
			case MINUTES:
				throwException = throwException || time.getSecond() != 0;
			case SECONDS:
				throwException = throwException || time.getNano() != 0;
				break;
			case MILLIS:
				throwException = (time.getNano() % 1_000_000) != 0;
				break;
			case MICROS:
				throwException = (time.getNano() % 1_000) != 0;
		}
		if (throwException) {
			throwTimeAndPrecisionException(time.toString(), minimum, ofPrecision.get());
		}
	}

	private static void checkMinValuesAndPrecision(MinuteBetween minuteBetween, SecondBetween secondBetween, OfPrecision ofPrecision) {
		if (ofPrecision.get().compareTo(SECONDS) > 0) {
			if (secondBetween.getMin() > 0) {
				throwValueAndPrecisionException(secondBetween.getMin().toString(), true, "second", ofPrecision.get());
			}
			if (ofPrecision.get().compareTo(MINUTES) > 0 && minuteBetween.getMin() > 0) {
				throwValueAndPrecisionException(minuteBetween.getMin().toString(), true, "minute", ofPrecision.get());
			}
		}
	}

	private static void throwValueAndPrecisionException(String val, boolean minimum, String unit, ChronoUnit precision) {
		String minMax = minimum ? "minimum" : "maximum";
		throw new IllegalArgumentException(String.format("Can't use %s as %s %s with precision %s.", val, minMax, unit, precision));
	}

	private static void throwTimeAndPrecisionException(String val, boolean minimum, ChronoUnit precision) {
		String minMax = minimum ? "minimum" : "maximum";
		throw new IllegalArgumentException(
			String
				.format("Can't use %s as %s time with precision %s.%nYou may want to round the time to %s or change the precision.", val, minMax, precision, precision)
		);
	}

	public static LocalTime calculateEffectiveMax(
		LocalTimeBetween timeBetween,
		HourBetween hourBetween,
		MinuteBetween minuteBetween,
		SecondBetween secondBetween,
		OfPrecision ofPrecision
	) {
		LocalTime effective = timeBetween.getMax() != null ? timeBetween.getMax() : calculateMaxPossibleValue(ofPrecision);
		checkTimeValueAndPrecision(effective, ofPrecision, false);
		if (hourBetween.getMax() < effective.getHour()) {
			effective = effective.withHour(hourBetween.getMax());
			effective = setEffectiveMaxValues(effective, ofPrecision, MINUTES);
		}
		if (minuteBetween.getMax() < effective.getMinute()) {
			effective = effective.withMinute(minuteBetween.getMax());
			effective = setEffectiveMaxValues(effective, ofPrecision, SECONDS);
		}
		if (secondBetween.getMax() < effective.getSecond()) {
			effective = effective.withSecond(secondBetween.getMax());
			effective = setEffectiveMaxValues(effective, ofPrecision, NANOS);
		}
		return effective;
	}

	private static LocalTime setEffectiveMaxValues(LocalTime effective, OfPrecision ofPrecision, ChronoUnit precision) {
		switch (precision) {
			case MINUTES:
				effective = ofPrecision.get().compareTo(MINUTES) <= 0 ? effective.withMinute(59) : effective.withMinute(0);
			case SECONDS:
				effective = ofPrecision.get().compareTo(SECONDS) <= 0 ? effective.withSecond(59) : effective.withSecond(0);
			default:
				switch (ofPrecision.get()) {
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
		}
		return effective;
	}

	public static LocalTime calculateMaxPossibleValue(OfPrecision ofPrecision) {
		switch (ofPrecision.get()) {
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
			clone.ofPrecision = this.ofPrecision.setProgrammatically(ofPrecision);
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
		clone.ofPrecision = this.ofPrecision.set(ofPrecision);
		return clone;
	}

}
