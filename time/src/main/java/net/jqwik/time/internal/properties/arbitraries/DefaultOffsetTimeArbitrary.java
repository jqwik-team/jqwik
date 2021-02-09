package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultOffsetTimeArbitrary extends ArbitraryDecorator<OffsetTime> implements OffsetTimeArbitrary {

	private OffsetTime timeMin = null;
	private OffsetTime timeMax = null;

	private int hourMin = -1;
	private int hourMax = -1;

	private int minuteMin = -1;
	private int minuteMax = -1;

	private int secondMin = -1;
	private int secondMax = -1;

	private ChronoUnit precision = null;

	@Override
	protected Arbitrary<OffsetTime> arbitrary() {

		LocalTimeArbitrary localTimes = Times.times();

		if (timeMin != null) {
			localTimes = localTimes.atTheEarliest(timeMin.toLocalTime());
		}

		if (timeMax != null) {
			localTimes = localTimes.atTheLatest(timeMax.toLocalTime());
		}

		if (hourMin != -1 && hourMax != -1) {
			localTimes = localTimes.hourBetween(hourMin, hourMax);
		}

		if (minuteMin != -1 && minuteMax != -1) {
			localTimes = localTimes.minuteBetween(minuteMin, minuteMax);
		}

		if (secondMin != -1 && secondMax != -1) {
			localTimes = localTimes.secondBetween(secondMin, secondMax);
		}

		if (precision != null) {
			localTimes = localTimes.constrainPrecision(precision);
		}

		return localTimes.map(v -> OffsetTime.of(v, ZoneOffset.MIN));

	}

	@Override
	public OffsetTimeArbitrary atTheEarliest(OffsetTime min) {
		if ((timeMax != null) && min.isAfter(timeMax)) {
			throw new IllegalArgumentException("Minimum time must not be after maximum time");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.timeMin = min;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary atTheLatest(OffsetTime max) {
		if ((timeMin != null) && max.isBefore(timeMin)) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.timeMax = max;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary hourBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 23) {
			throw new IllegalArgumentException("Hour value must be between 0 and 23.");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.hourMin = min;
		clone.hourMax = max;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary minuteBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Minute value must be between 0 and 59.");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.minuteMin = min;
		clone.minuteMax = max;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary secondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Second value must be between 0 and 59.");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.secondMin = min;
		clone.secondMax = max;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary constrainPrecision(ChronoUnit precision) {
		if (!(precision.equals(HOURS) || precision.equals(MINUTES) || precision.equals(SECONDS) || precision.equals(MILLIS) || precision
																																	   .equals(MICROS) || precision
																																								  .equals(NANOS))) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.precision = precision;
		return clone;
	}

}
