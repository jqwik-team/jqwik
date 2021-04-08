package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class LocalDateTimeBetween extends Between<LocalDateTime> {
	@Override
	protected void checkValidity(LocalDateTime min, LocalDateTime max) {
		if ((min != null && min.getYear() <= 0) || (max != null && max.getYear() <= 0)) {
			throw new IllegalArgumentException("Minimum year in a date time must be > 0");
		}
	}
}
