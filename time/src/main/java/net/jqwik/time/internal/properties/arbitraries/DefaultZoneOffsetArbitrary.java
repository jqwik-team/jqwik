package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultZoneOffsetArbitrary extends ArbitraryDecorator<ZoneOffset> implements ZoneOffsetArbitrary {

	public static final ZoneOffset DEFAULT_MIN = ZoneOffset.ofHoursMinutesSeconds(14, 0, 0);
	public static final ZoneOffset DEFAULT_MAX = ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0);

	private final ZoneOffsetBetween zoneOffsetBetween = new ZoneOffsetBetween();

	@Override
	protected Arbitrary<ZoneOffset> arbitrary() {

		ZoneOffset effectiveMin = calculateEffectiveMin();
		ZoneOffset effectiveMax = calculateEffectiveMax();

		if (effectiveMin.compareTo(effectiveMax) > 0) {
			throw new IllegalArgumentException("With this configuration is no value possible.");
		}

		int maxInt = calculateIndex(effectiveMin);
		int minInt = calculateIndex(effectiveMax);

		Arbitrary<Integer> indexes = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(minInt, maxInt)
												.edgeCases(edgeCases -> edgeCases.includeOnly(minInt, 0, maxInt));

		return indexes.map(DefaultZoneOffsetArbitrary::calculateOffset);

	}

	private ZoneOffset calculateEffectiveMax() {
		ZoneOffset offsetMax = zoneOffsetBetween.getMax() != null ? zoneOffsetBetween.getMax() : DEFAULT_MAX;
		boolean isNegative = offsetMax.getTotalSeconds() < 0;
		int hour = calculateHourValue(offsetMax);
		int minute = calculateMinuteValue(offsetMax);
		int second = calculateSecondValue(offsetMax);
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

	private ZoneOffset calculateEffectiveMin() {
		ZoneOffset offsetMin = zoneOffsetBetween.getMin() != null ? zoneOffsetBetween.getMin() : DEFAULT_MIN;
		boolean isNegative = offsetMin.getTotalSeconds() < 0;
		int hour = calculateHourValue(offsetMin);
		int minute = calculateMinuteValue(offsetMin);
		int second = calculateSecondValue(offsetMin);
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

	static private ZoneOffset calculateOffset(int index) {
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
		if (min.compareTo(DEFAULT_MAX) > 0
				|| max.compareTo(DEFAULT_MAX) > 0
				|| min.compareTo(DEFAULT_MIN) < 0
				|| max.compareTo(DEFAULT_MIN) < 0) {
			throw new IllegalArgumentException("Offset must be between -12:00:00 and +14:00:00.");
		}

		DefaultZoneOffsetArbitrary clone = typedClone();
		clone.zoneOffsetBetween.set(min, max);
		return clone;
	}

}
