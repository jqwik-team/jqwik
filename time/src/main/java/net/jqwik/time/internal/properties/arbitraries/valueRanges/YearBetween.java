package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class YearBetween extends ValueRange<Year> {

	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin().isAfter(parameter.getMax())) {
			parameter.changeMinMax();
		}
	}

	@Override
	protected void exceptionCheck(Parameter parameter) {
		if (parameter.getMin().getValue() <= 0 || parameter.getMax().getValue() <= 0) {
			throw new IllegalArgumentException("Minimum year must be > 0");
		}
	}
}
