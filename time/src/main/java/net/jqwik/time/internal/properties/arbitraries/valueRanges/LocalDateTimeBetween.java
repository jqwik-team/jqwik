package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class LocalDateTimeBetween extends ValueRange<LocalDateTime> {
	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (parameter.getMin() != null && parameter.getMin().getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date time must be > 0");
		}
		if (parameter.getMax() != null && parameter.getMax().getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date time must be > 0");
		}
		if (parameter.getMin() != null && parameter.getMax() != null && parameter.getMin().isAfter(parameter.getMax())) {
			throw new IllegalArgumentException("Minimum date time must not be after maximum date time");
		}
		if (parameter.getMin() != null && parameter.getMax() != null && parameter.getMax().isBefore(parameter.getMin())) {
			throw new IllegalArgumentException("Maximum date time must not be before minimum date time");
		}
	}
}
