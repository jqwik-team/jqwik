package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class PeriodBetween extends ValueRange<Period> {

	public final static long DAYS_PER_MONTH = 31L; // The maximum
	public final static long DAYS_PER_YEAR = 372L; // 31 * 12

	public static long inDays(Period period) {
		return period.getYears() * DAYS_PER_YEAR + period.getMonths() * DAYS_PER_MONTH + period.getDays();
	}

	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (inDays(parameter.getMin()) > inDays(parameter.getMax())) {
			parameter.changeMinMax();
		}
	}

}
