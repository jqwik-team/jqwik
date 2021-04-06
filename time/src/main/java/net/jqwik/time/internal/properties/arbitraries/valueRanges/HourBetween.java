package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public class HourBetween extends ValueRange<Integer> {

	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin() > parameter.getMax()) {
			parameter.changeMinMax();
		}
	}

	@Override
	protected void exceptionCheck(Parameter parameter) {
		if ((parameter.getMin() != null && parameter.getMin() < 0) || (parameter.getMax() != null && parameter.getMax() > 23)) {
			throw new IllegalArgumentException("Hour value must be between 0 and 23.");
		}
	}
}
