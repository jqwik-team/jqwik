package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class YearMonthBetween extends ValueRange<YearMonth> {

	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (parameter.getMin() != null && parameter.getMin().getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a year month must be > 0");
		}
		if (parameter.getMax() != null && parameter.getMax().getYear() <= 0) {
			throw new IllegalArgumentException("Minimum year in a year month must be > 0");
		}
		if (parameter.getMin() != null && parameter.getMax() != null && parameter.getMin().isAfter(parameter.getMax())) {
			throw new IllegalArgumentException("Minimum year month must not be after maximum year month");
		}
	}

	public void setYearBetween(YearBetween yearBetween) {
		YearMonth min = YearMonth.of(yearBetween.getMin().getValue(), 1);
		YearMonth max = YearMonth.of(yearBetween.getMax().getValue(), 12);
		set(min, max);
	}

}
