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

	private LocalTime timeMin = LocalTime.MIN;
	private LocalTime timeMax = LocalTime.MAX;

	private int hourMin = -1;
	private int hourMax = -1;

	private int minuteMin = -1;
	private int minuteMax = -1;

	private int secondMin = -1;
	private int secondMax = -1;

	private ZoneOffset offsetMin = null;
	private ZoneOffset offsetMax = null;

	private ChronoUnit ofPrecision = null;

	@Override
	protected Arbitrary<OffsetTime> arbitrary() {

		LocalTimeArbitrary localTimes = Times.times();
		ZoneOffsetArbitrary zoneOffsets = Times.zoneOffsets();

		if (timeMin != null && timeMax != null) {
			localTimes = localTimes.between(timeMin, timeMax);
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

		if (ofPrecision != null) {
			localTimes = localTimes.constrainPrecision(ofPrecision);
		}

		if (offsetMin != null && offsetMax != null) {
			zoneOffsets = zoneOffsets.between(offsetMin, offsetMax);
		}

		Arbitrary<OffsetTime> offsetTimes = Combinators.combine(localTimes, zoneOffsets).as(OffsetTime::of);

		return offsetTimes;

	}

	@Override
	public OffsetTimeArbitrary atTheEarliest(LocalTime min) {
		if (min.isAfter(timeMax)) {
			throw new IllegalArgumentException("Minimum time must not be after maximum time");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.timeMin = min;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary atTheLatest(LocalTime max) {
		if (max.isBefore(timeMin)) {
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
	public OffsetTimeArbitrary offsetBetween(ZoneOffset min, ZoneOffset max) {
		if (min.getTotalSeconds() > max.getTotalSeconds()) {
			ZoneOffset remember = min;
			min = max;
			max = remember;
		}

		if (min.getTotalSeconds() < DefaultZoneOffsetArbitrary.DEFAULT_MIN
											.getTotalSeconds() || min.getTotalSeconds() > DefaultZoneOffsetArbitrary.DEFAULT_MAX
																								  .getTotalSeconds()) {
			throw new IllegalArgumentException("Offset must be between -12:00:00 and +14:00:00.");
		}

		if (max.getTotalSeconds() > DefaultZoneOffsetArbitrary.DEFAULT_MAX.getTotalSeconds()) {
			throw new IllegalArgumentException("Offset must be between -12:00:00 and +14:00:00.");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.offsetMin = min;
		clone.offsetMax = max;
		return clone;
	}

	@Override
	public OffsetTimeArbitrary constrainPrecision(ChronoUnit ofPrecision) {
		if (!(ofPrecision.equals(HOURS) || ofPrecision.equals(MINUTES) || ofPrecision.equals(SECONDS) || ofPrecision
																												 .equals(MILLIS) || ofPrecision
																																			.equals(MICROS) || ofPrecision
																																									   .equals(NANOS))) {
			throw new IllegalArgumentException("Precision value must be one of these ChronoUnit values: HOURS, MINUTES, SECONDS, MILLIS, MICROS, NANOS");
		}

		DefaultOffsetTimeArbitrary clone = typedClone();
		clone.ofPrecision = ofPrecision;
		return clone;
	}

}
