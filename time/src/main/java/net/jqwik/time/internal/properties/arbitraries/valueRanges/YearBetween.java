package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class YearBetween extends ValueRange<Year> {

	private boolean useInCalendar;

	public YearBetween() {
		this(false);
	}

	public YearBetween(boolean useInCalendar) {
		this.useInCalendar = useInCalendar;
	}

	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin().isAfter(parameter.getMax())) {
			parameter.changeMinMax();
		}
	}

	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (parameter.getMin() != null && parameter.getMin().getValue() <= 0) {
			throw new IllegalArgumentException("Minimum year must be > 0");
		}
		if (parameter.getMax() != null && parameter.getMax().getValue() <= 0) {
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
