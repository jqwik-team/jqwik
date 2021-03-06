package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class TimeRangeForLocalTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, TimeRange range) {
		LocalTime min = stringToLocalTime(range.min());
		LocalTime max = stringToLocalTime(range.max());
		if (arbitrary instanceof LocalTimeArbitrary) {
			LocalTimeArbitrary localTimeArbitrary = (LocalTimeArbitrary) arbitrary;
			return localTimeArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter((LocalTime) v, min, max));
		}
	}

	private LocalTime stringToLocalTime(String time) {
		return LocalTime.parse(time);
	}

	private boolean filter(LocalTime time, LocalTime min, LocalTime max) {
		return !time.isBefore(min) && !time.isAfter(max);
	}

}
