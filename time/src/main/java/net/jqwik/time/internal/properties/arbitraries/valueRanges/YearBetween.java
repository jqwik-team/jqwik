package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class YearBetween extends Between<Year> {

	private boolean useInCalendar = false;
	private boolean allowBelow0 = false;

	public YearBetween useInCalendar() {
		useInCalendar = true;
		return this;
	}

	public YearBetween allowBelow0() {
		allowBelow0 = true;
		return this;
	}

	@Override
	protected void checkValidity(Year min, Year max) {
		if (!allowBelow0 && ((min != null && min.getValue() <= 0) || (max != null && max.getValue() <= 0))) {
			throw new IllegalArgumentException("Minimum year must be > 0");
		}
		if (useInCalendar && ((min != null && min.getValue() > 292_278_993) || (max != null && max.getValue() > 292_278_993))) {
			throw new IllegalArgumentException("Minimum year in a calendar based date must be <= 292278993");
		}
	}

}
