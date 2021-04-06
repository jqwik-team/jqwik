package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class MonthBetween extends ValueRange<Month> {
	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin().compareTo(parameter.getMax()) > 0) {
			parameter.changeMinMax();
		}
	}
}
