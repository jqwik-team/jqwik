package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class YearBetween extends ValueRange<Year> {

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
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin().isAfter(parameter.getMax())) {
			parameter.changeMinMax();
		}
	}

	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (!allowBelow0 && parameter.getMin() != null && parameter.getMin().getValue() <= 0) {
			throw new IllegalArgumentException("Minimum year must be > 0");
		}
		if (!allowBelow0 && parameter.getMax() != null && parameter.getMax().getValue() <= 0) {
			throw new IllegalArgumentException("Maximum year must be > 0");
		}
		if (useInCalendar && parameter.getMin() != null && parameter.getMin().getValue() > 292_278_993) {
			throw new IllegalArgumentException("Minimum year in a calendar based date must be <= 292278993");
		}
		if (useInCalendar && parameter.getMax() != null && parameter.getMax().getValue() > 292_278_993) {
			throw new IllegalArgumentException("Maximum year in a calendar based date must be <= 292278993");
		}
	}
}
