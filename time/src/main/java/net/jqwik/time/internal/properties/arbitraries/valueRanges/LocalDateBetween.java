package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class LocalDateBetween extends Between<LocalDate> {

	@Override
	protected void checkValidity(LocalDate min, LocalDate max) {
		if ((min != null && min.getYear() <= 0) || (max != null && max.getYear() <= 0)) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
	}

	public void setYearBetween(YearBetween yearBetween) {
		LocalDate min = LocalDate.of(yearBetween.getMin().getValue(), 1, 1);
		LocalDate max = LocalDate.of(yearBetween.getMax().getValue(), 12, 31);
		set(min, max);
	}

}
