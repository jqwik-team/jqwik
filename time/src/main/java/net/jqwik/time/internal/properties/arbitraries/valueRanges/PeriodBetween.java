package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class PeriodBetween extends Between<Period> {

	public final static long DAYS_PER_MONTH = 31L; // The maximum
	public final static long DAYS_PER_YEAR = 372L; // 31 * 12

	public static long inDays(Period period) {
		return period.getYears() * DAYS_PER_YEAR + period.getMonths() * DAYS_PER_MONTH + period.getDays();
	}

	@Override
	protected boolean minIsBeforeMax(Period min, Period max) {
		return inDays(min) <= inDays(max);
	}

}
