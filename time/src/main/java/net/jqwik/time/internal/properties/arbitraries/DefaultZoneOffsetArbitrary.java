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

		ZoneOffset effectiveMin = effectiveMin();
		ZoneOffset effectiveMax = effectiveMax();

		if (effectiveMin.compareTo(effectiveMax) > 0) {
			throw new IllegalArgumentException("With this configuration is no value possible.");
		}

		int maxInt = indexOf(effectiveMin);
		int minInt = indexOf(effectiveMax);

		Arbitrary<Integer> indexes = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(minInt, maxInt)
												.edgeCases(edgeCases -> edgeCases.includeOnly(minInt, 0, maxInt));

		return indexes.map(DefaultZoneOffsetArbitrary::offsetFromValue);

	}

	private ZoneOffset effectiveMax() {
		ZoneOffset offsetMax = zoneOffsetBetween.getMax() != null ? zoneOffsetBetween.getMax() : DEFAULT_MAX;
		boolean isNegative = offsetMax.getTotalSeconds() < 0;
		int hour = hourValue(offsetMax);
		int minute = minuteValue(offsetMax);
		int second = secondValue(offsetMax);
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

	private ZoneOffset effectiveMin() {
		ZoneOffset offsetMin = zoneOffsetBetween.getMin() != null ? zoneOffsetBetween.getMin() : DEFAULT_MIN;
		boolean isNegative = offsetMin.getTotalSeconds() < 0;
		int hour = hourValue(offsetMin);
		int minute = minuteValue(offsetMin);
		int second = secondValue(offsetMin);
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

	private int indexOf(ZoneOffset effective) {
		boolean isNegative = effective.getTotalSeconds() < 0;
		int hour = hourValue(effective);
		int minuteVal = minuteValue(effective) / 15;
		if (isNegative) {
			minuteVal = -minuteVal;
		}
		return hour * 4 + minuteVal;
	}

	static private ZoneOffset offsetFromValue(int index) {
		int hour = index / 4;
		index -= hour * 4;
		int minute = index * 15;
		return ZoneOffset.ofHoursMinutes(hour, minute);
	}

	private int hourValue(ZoneOffset offset) {
		return offset.getTotalSeconds() / 3600;
	}

	private int minuteValue(ZoneOffset offset) {
		return Math.abs((offset.getTotalSeconds() % 3600) / 60);
	}

	private int secondValue(ZoneOffset offset) {
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
