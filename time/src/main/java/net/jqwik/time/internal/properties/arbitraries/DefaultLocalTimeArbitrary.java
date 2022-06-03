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

		LocalTime effectiveMin = effectiveMin(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);
		LocalTime effectiveMax = effectiveMax(timeBetween, hourBetween, minuteBetween, secondBetween, ofPrecision);

		long longEnd = ofPrecision.longsBetween(effectiveMin, effectiveMax);

		Arbitrary<Long> longs = Arbitraries.longs()
										   .withDistribution(RandomDistribution.uniform())
										   .between(0L, longEnd)
										   .edgeCases(config -> config.includeOnly(0L, longEnd));

		Arbitrary<LocalTime> localTimes = longs.map(v -> ofPrecision.localTimeFromValue(v, effectiveMin));

		localTimes = localTimes.filter(
			20000,
			v -> v.getMinute() >= minuteBetween.getMin()
					 && v.getMinute() <= minuteBetween.getMax()
					 && v.getSecond() >= secondBetween.getMin()
					 && v.getSecond() <= secondBetween.getMax()
		);

		return localTimes;

	}

	public static LocalTime effectiveMin(
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
		if (!ofPrecision.valueWithPrecisionIsAllowed(time.getMinute(), time.getSecond(), time.getNano())) {
			throwTimeAndPrecisionException(time.toString(), minimum, ofPrecision.get());
		}
	}

	private static void checkMinValuesAndPrecision(MinuteBetween minuteBetween, SecondBetween secondBetween, OfPrecision ofPrecision) {
		if (ofPrecision.isGreatherThan(SECONDS)) {
			if (secondBetween.getMin() > 0) {
				throwValueAndPrecisionException(secondBetween.getMin().toString(), true, "second", ofPrecision.get());
			}
			if (ofPrecision.isGreatherThan(MINUTES) && minuteBetween.getMin() > 0) {
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

	public static LocalTime effectiveMax(
		LocalTimeBetween timeBetween,
		HourBetween hourBetween,
		MinuteBetween minuteBetween,
		SecondBetween secondBetween,
		OfPrecision ofPrecision
	) {
		LocalTime effective = timeBetween.getMax() != null ? timeBetween.getMax() : ofPrecision.maxPossibleLocalTime();
		checkTimeValueAndPrecision(effective, ofPrecision, false);
		if (hourBetween.getMax() < effective.getHour()) {
			effective = effective.withHour(hourBetween.getMax());
			effective = effectiveMaxValues(effective, ofPrecision, MINUTES);
		}
		if (minuteBetween.getMax() < effective.getMinute()) {
			effective = effective.withMinute(minuteBetween.getMax());
			effective = effectiveMaxValues(effective, ofPrecision, SECONDS);
		}
		if (secondBetween.getMax() < effective.getSecond()) {
			effective = effective.withSecond(secondBetween.getMax());
			effective = effectiveMaxValues(effective, ofPrecision, NANOS);
		}
		return effective;
	}

	private static LocalTime effectiveMaxValues(LocalTime effective, OfPrecision ofPrecision, ChronoUnit precision) {
		switch (precision) {
			case MINUTES:
				effective = ofPrecision.isLessOrEqualTo(MINUTES) ? effective.withMinute(59) : effective.withMinute(0);
			case SECONDS:
				effective = ofPrecision.isLessOrEqualTo(SECONDS) ? effective.withSecond(59) : effective.withSecond(0);
			default:
				effective = ofPrecision.effectiveMaxNanos(effective);
		}
		return effective;
	}

	public static ChronoUnit ofPrecisionFromNanos(int nanos) {
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

	public static ChronoUnit ofPrecisionFromTime(LocalTime time) {
		int nanos = time.getNano();
		return ofPrecisionFromNanos(nanos);
	}

	private void setOfPrecisionImplicitly(DefaultLocalTimeArbitrary clone, LocalTime time) {
		if (clone.ofPrecision.isSet()) {
			return;
		}
		ChronoUnit ofPrecision = ofPrecisionFromTime(time);
		if (clone.ofPrecision.isGreatherThan(ofPrecision)) {
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
