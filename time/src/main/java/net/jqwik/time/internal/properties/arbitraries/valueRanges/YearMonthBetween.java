package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class YearMonthBetween extends Between<YearMonth> {

	@Override
	protected void checkValidity(YearMonth min, YearMonth max) {
		if ((min != null && min.getYear() <= 0) || (max != null && max.getYear() <= 0)) {
			throw new IllegalArgumentException("Minimum year in a year month must be > 0");
		}
	}

	public void setYearBetween(YearBetween yearBetween) {
		YearMonth min = YearMonth.of(yearBetween.getMin().getValue(), 1);
		YearMonth max = YearMonth.of(yearBetween.getMax().getValue(), 12);
		set(min, max);
	}

}
