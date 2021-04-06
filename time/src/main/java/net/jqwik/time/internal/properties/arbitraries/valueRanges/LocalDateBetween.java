package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class LocalDateBetween extends ValueRange<LocalDate> {
	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (parameter.getMin().isAfter(parameter.getMax())) {
			throw new IllegalArgumentException("Minimum date must not be after maximum date");
		}
		if (parameter.getMin().getYear() <= 0 || parameter.getMax().getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
	}
}
