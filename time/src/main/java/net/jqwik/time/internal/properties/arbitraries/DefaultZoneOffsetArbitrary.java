package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultZoneOffsetArbitrary extends ArbitraryDecorator<ZoneOffset> implements ZoneOffsetArbitrary {

	public static final ZoneOffset DEFAULT_MIN = ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0);
	public static final ZoneOffset DEFAULT_MAX = ZoneOffset.ofHoursMinutesSeconds(14, 0, 0);

	private ZoneOffset offsetMin = DEFAULT_MIN;
	private ZoneOffset offsetMax = DEFAULT_MAX;

	@Override
	protected Arbitrary<ZoneOffset> arbitrary() {

		ZoneOffset effectiveMin = calculateEffectiveMin();
		ZoneOffset effectiveMax = calculateEffectiveMax();

		if (effectiveMin.getTotalSeconds() > effectiveMax.getTotalSeconds()) {
			throw new IllegalArgumentException("With this configuration is no value possible.");
		}

		int min = calculateIndex(effectiveMin);
		int max = calculateIndex(effectiveMax);

		Arbitrary<Integer> indexes = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(min, max)
												.edgeCases(edgeCases -> edgeCases.includeOnly(min, 0, max));

		return indexes.map(this::calculateOffset);

	}

	private ZoneOffset calculateEffectiveMin() {
		boolean isNegative = offsetMin.getTotalSeconds() < 0;
		int hour = calculateHourValue(offsetMin);
		int minute = calculateMinuteValue(offsetMin);
		int second = calculateSecondValue(offsetMin);
		if (second != 0) {
			if (!isNegative) {
				minute++;
				if (minute > 59) {
					minute -= 60;
					hour++;
				}
			}
		}
		while (minute % 15 != 0) {
			minute += isNegative ? -1 : 1;
			if (!isNegative && minute == 60) {
				minute = 0;
				hour++;
			}
		}
		if (isNegative) {
			minute = -minute;
		}
		return ZoneOffset.ofHoursMinutes(hour, minute);
	}

	private ZoneOffset calculateEffectiveMax() {
		boolean isNegative = offsetMax.getTotalSeconds() < 0;
		int hour = calculateHourValue(offsetMax);
		int minute = calculateMinuteValue(offsetMax);
		int second = calculateSecondValue(offsetMax);
		if (second != 0) {
			if (isNegative) {
				minute++;
				if (minute > 59) {
					minute -= 60;
					hour--;
				}
			}
		}
		while (minute % 15 != 0) {
			minute -= isNegative ? -1 : 1;
			if (isNegative && minute == 60) {
				minute = 0;
				hour--;
			}
		}
		if (isNegative) {
			minute = -minute;
		}
		return ZoneOffset.ofHoursMinutes(hour, minute);
	}

	private int calculateIndex(ZoneOffset effective) {
		boolean isNegative = effective.getTotalSeconds() < 0;
		int hour = calculateHourValue(effective);
		int minuteVal = calculateMinuteValue(effective) / 15;
		if (isNegative) {
			minuteVal = -minuteVal;
		}
		return hour * 4 + minuteVal;
	}

	private ZoneOffset calculateOffset(int index) {
		int hour = index / 4;
		index -= hour * 4;
		int minute = index * 15;
		return ZoneOffset.ofHoursMinutes(hour, minute);
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
	public ZoneOffsetArbitrary between(ZoneOffset min, ZoneOffset max) {
		if (min.getTotalSeconds() > max.getTotalSeconds()) {
			ZoneOffset remember = min;
			min = max;
			max = remember;
		}

		if (min.getTotalSeconds() < DEFAULT_MIN.getTotalSeconds()
					|| min.getTotalSeconds() > DEFAULT_MAX.getTotalSeconds()
					|| max.getTotalSeconds() > DEFAULT_MAX.getTotalSeconds()) {
			throw new IllegalArgumentException("Offset must be between -12:00:00 and +14:00:00.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.offsetMin = min;
		clone.offsetMax = max;
		return clone;
	}

}
