package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class TimeRangeForOffsetTimeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(OffsetTime.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, TimeRange range) {
		LocalTime min = stringToLocalTime(range.min());
		LocalTime max = stringToLocalTime(range.max());
		if (arbitrary instanceof OffsetTimeArbitrary) {
			OffsetTimeArbitrary offsetTimeArbitrary = (OffsetTimeArbitrary) arbitrary;
			return offsetTimeArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter((OffsetTime) v, min, max));
		}
	}

	private LocalTime stringToLocalTime(String time) {
		return LocalTime.parse(time);
	}

	private boolean filter(OffsetTime time, LocalTime min, LocalTime max) {
		return !time.toLocalTime().isBefore(min) && !time.toLocalTime().isAfter(max);
	}

}
