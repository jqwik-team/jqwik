package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultZoneOffsetArbitrary extends ArbitraryDecorator<ZoneOffset> implements ZoneOffsetArbitrary {

	private int hourMin = -17; //TODO: -18
	private int hourMax = 17; //TODO: 18
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

		int secondsBetween = calculateSecondsBetween();

		Arbitrary<Integer> seconds = Arbitraries.integers()
												.withDistribution(RandomDistribution.uniform())
												.between(0, secondsBetween)
												.edgeCases(edgeCases -> {
													edgeCases.includeOnly(0, secondsBetween);
												});

		Arbitrary<ZoneOffset> zoneOffsets = seconds.map(this::calculateZoneOffset);

		return zoneOffsets;

	}

	private ZoneOffset calculateZoneOffset(int val) {

		//TODO: -00:xx:xx is not possible at the moment
		//TODO: -00:00:xx is not possible at the moment

		int hours = hourMin;
		int minutes = minuteMin;
		int seconds = secondMin;

		int hoursAdd = val / helperMinuteSecond;
		hours += hoursAdd;

		val -= hoursAdd * helperMinuteSecond;

		int minutesAdd = val / secondPeriod;
		minutes += minutesAdd;

		int secondsAdd = val - (minutesAdd * secondPeriod);
		seconds += secondsAdd;

		if (hours < 0) {
			minutes *= -1;
		}
		if (minutes < 0 || hours < 0) {
			seconds *= -1;
		}

		return ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds);

	}

	private int calculateSecondsBetween() {

		hourPeriod = 1 + hourMax - hourMin;
		minutePeriod = 1 + minuteMax - minuteMin;
		secondPeriod = 1 + secondMax - secondMin;

		helperMinuteSecond = minutePeriod * secondPeriod;

		return (hourPeriod * helperMinuteSecond) - 1;

	}

}
