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
	private int hourPeriod;

	private int minuteMin = 0;
	private int minuteMax = 59;
	private int minutePeriod;

	private int secondMin = 0;
	private int secondMax = 59;
	private int secondPeriod;

	private int helperMinuteSecond;

	@Override
	protected Arbitrary<ZoneOffset> arbitrary() {

		/*if (hourMin == hourMax && hourMin == -18) {
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

		int secondsBetween = calculateSecondsBetween();

		Arbitrary<Integer> seconds = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(0, secondsBetween)
												.edgeCases(EdgeCases.Config::none);

		Arbitrary<ZoneOffset> zoneOffsets = seconds.map(this::calculateZoneOffset)
												   .filter(v -> v.getTotalSeconds() >= offsetMin
																							   .getTotalSeconds() && v.getTotalSeconds() <= offsetMax
																																					.getTotalSeconds())
												   .edgeCases(edgeCases -> {
													   edgeCases.add(offsetMin, offsetMax);
													   edgeCases.includeOnly(offsetMin, offsetMax);
												   });*/

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

		Arbitrary<Integer> seconds = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(offsetMin.getTotalSeconds(), offsetMax.getTotalSeconds());

		Arbitrary<ZoneOffset> zoneOffsets = seconds.map(ZoneOffset::ofTotalSeconds)
												   .filter(this::filterOffsets);

		return zoneOffsets;

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

	/*private void calculateEffectiveValues() {
		hourMin = offsetMin.getTotalSeconds()/3600;
		hourMax = offsetMax.getTotalSeconds()/3600;
		if (hourMin == hourMax) {
			minuteMin = Math.abs((offsetMin.getTotalSeconds()%3600)/60);
			minuteMax = Math.abs((offsetMax.getTotalSeconds()%3600)/60);
			if (minuteMin == minuteMax) {
				secondMin = Math.abs(offsetMin.getTotalSeconds()%60);
				secondMax = Math.abs(offsetMax.getTotalSeconds()%60);
			}
		}
	}

	private ZoneOffset calculateZoneOffset(int val) {

		int hours = hourMin;
		if (hours == -18) {
			hours++;
		}
		int minutes = minuteMin;
		int seconds = secondMin;
		boolean negativeZero = false;

		int hoursAdd = val / helperMinuteSecond;
		hours += hoursAdd;
		int effectiveHourMax = hourMax != 18 ? hourMax : hourMax - 1;
		if (hours > effectiveHourMax) {
			hours = 0;
			negativeZero = true;
		}

		val -= hoursAdd * helperMinuteSecond;

		int minutesAdd = val / secondPeriod;
		minutes += minutesAdd;

		int secondsAdd = val - (minutesAdd * secondPeriod);
		seconds += secondsAdd;

		if (hours < 0 || negativeZero) {
			minutes *= -1;
		}
		if (minutes < 0 || hours < 0 || negativeZero) {
			seconds *= -1;
		}

		return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);

	}

	private int calculateSecondsBetween() {

		hourPeriod = 1 + hourMax - hourMin;
		minutePeriod = 1 + minuteMax - minuteMin;
		secondPeriod = 1 + secondMax - secondMin;

		if (hourMin == -18) {
			hourPeriod--;
		}
		if (hourMax == 18) {
			hourPeriod--;
		}

		if (hourMin <= 0 && hourMax >= 0) {
			hourPeriod++;
		}

		helperMinuteSecond = minutePeriod * secondPeriod;

		return (hourPeriod * helperMinuteSecond) - 1;

	}*/

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
