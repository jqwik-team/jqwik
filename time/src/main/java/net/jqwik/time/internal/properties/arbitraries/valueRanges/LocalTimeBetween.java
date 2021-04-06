package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class LocalTimeBetween extends ValueRange<LocalTime> {

	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (parameter.getMax() != null && parameter.getMin() != null && parameter.getMin().isAfter(parameter.getMax())) {
			throw new IllegalArgumentException("Minimum time must not be after maximum time");
		}
	}

}
