package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class DurationBetween extends ValueRange<Duration> {
	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin().compareTo(parameter.getMax()) > 0) {
			parameter.changeMinMax();
		}
	}
}
