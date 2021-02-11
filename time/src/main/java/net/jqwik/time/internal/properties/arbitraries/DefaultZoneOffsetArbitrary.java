package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultZoneOffsetArbitrary extends ArbitraryDecorator<ZoneOffset> implements ZoneOffsetArbitrary {

	private ZoneOffset offsetMin = ZoneOffset.MIN;
	private ZoneOffset offsetMax = ZoneOffset.MAX;

	private int hourMin = -18;
	private int hourMax = 18;

	private int minuteMin = 0;
	private int minuteMax = 59;

	private int secondMin = 0;
	private int secondMax = 59;

	@Override
	protected Arbitrary<ZoneOffset> arbitrary() {

		if (hourMin == hourMax && hourMin == -18) {
			if (minuteMin != 0 || secondMin != 0) {
				throw new IllegalArgumentException("Minutes and Seconds must be 0 because hour value is -18.");
			} else if (offsetMin.getTotalSeconds() != ZoneOffset.MIN.getTotalSeconds()) {
				throw new IllegalArgumentException("Hour value can only be -18, but minimum zone offset value is not ZoneOffset.MIN");
			}
			return Arbitraries.just(ZoneOffset.MIN);
		} else if (hourMin == hourMax && hourMax == 18) {
			if (minuteMin != 0 || secondMin != 0) {
				throw new IllegalArgumentException("Minutes and Seconds must be 0 because hour value is 18.");
			} else if (offsetMax.getTotalSeconds() != ZoneOffset.MAX.getTotalSeconds()) {
				throw new IllegalArgumentException("Hour value can only be 18, but maximum zone offset value is not ZoneOffset.MAX");
			}
			return Arbitraries.just(ZoneOffset.MAX);
		}

		ZoneOffset effectiveMin = calculateEffectiveMin();
		ZoneOffset effectiveMax = calculateEffectiveMax();

		Arbitrary<Integer> seconds = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(effectiveMin.getTotalSeconds(), effectiveMax.getTotalSeconds())
												.edgeCases(edgeCases -> edgeCases
																				.includeOnly(effectiveMin.getTotalSeconds(), 0, effectiveMax
																																		.getTotalSeconds()));

		Arbitrary<ZoneOffset> zoneOffsets = seconds.map(ZoneOffset::ofTotalSeconds)
												   .filter(this::filterOffsets);

		return zoneOffsets;

	}

	private ZoneOffset calculateEffectiveMin() {

		int hours = calculateHourValue(offsetMin);
		int minutes = calculateMinuteValue(offsetMin);
		int seconds = calculateSecondValue(offsetMin);

		if (hours < hourMin) {
			hours = hourMin;
			if (hours > 0) {
				if (minutes < minuteMin) {
					minutes = minuteMin;
					if (seconds < secondMin) {
						seconds = secondMin;
					}
				}
			} else if (hours < 0) {
				minutes = minuteMax;
				seconds = secondMax;
			} else {
				//TODO: Value 0
			}
		}

		if (hours < 0) {
			minutes = -minutes;
			seconds = -seconds;
		}

		System.out.println("Min: " + ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds));

		return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);

	}

	private ZoneOffset calculateEffectiveMax() {

		int hours = calculateHourValue(offsetMax);
		int minutes = calculateMinuteValue(offsetMax);
		int seconds = calculateSecondValue(offsetMax);

		if (hours > hourMax) {
			hours = hourMax;
			if (hours > 0) {
				minutes = minuteMax;
				seconds = secondMax;
			} else if (hours < 0) {
				if (minutes < minuteMin) {
					minutes = minuteMin;
					if (seconds < secondMin) {
						seconds = secondMin;
					}
				}
			}
		}

		if (hours < 0) {
			minutes = -minutes;
			seconds = -seconds;
		}

		System.out.println("Max: " + ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds));

		return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);

	}

	private boolean filterOffsets(ZoneOffset offset) {

		int hour = calculateHourValue(offset);
		int minute = calculateMinuteValue(offset);
		int second = calculateSecondValue(offset);

		return hour >= hourMin && hour <= hourMax && minute >= minuteMin && minute <= minuteMax && second >= secondMin && second <= secondMax;

	}

	private int calculateHourValue(ZoneOffset offset) {
		return offset.getTotalSeconds() / 3600;
	}

	private int calculateMinuteValue(ZoneOffset offset) {
		return Math.abs((offset.getTotalSeconds() % 3600) / 60);
	}

	private int calculateSecondValue(ZoneOffset offset) {
		return Math.abs(offset.getTotalSeconds() % 60);
	}

	@Override
	public ZoneOffsetArbitrary atTheEarliest(ZoneOffset min) {
		if ((offsetMax != null) && min.getTotalSeconds() > offsetMax.getTotalSeconds()) {
			throw new IllegalArgumentException("Minimum offset must not be after maximum offset");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMin = min;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary atTheLatest(ZoneOffset max) {
		if ((offsetMin != null) && max.getTotalSeconds() < offsetMin.getTotalSeconds()) {
			throw new IllegalArgumentException("Maximum time must not be before minimum time");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMax = max;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary hourBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < -18 || max > 18) {
			throw new IllegalArgumentException("Hour value must be between -18 and 18.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.hourMin = min;
		clone.hourMax = max;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary minuteBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Minute value must be between 0 and 59.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.minuteMin = min;
		clone.minuteMax = max;
		return clone;
	}

	@Override
	public ZoneOffsetArbitrary secondBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}

		if (min < 0 || max > 59) {
			throw new IllegalArgumentException("Second value must be between 0 and 59.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.secondMin = min;
		clone.secondMax = max;
		return clone;
	}
}
