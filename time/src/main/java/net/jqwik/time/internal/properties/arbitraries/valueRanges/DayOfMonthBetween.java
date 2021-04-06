package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public class DayOfMonthBetween extends ValueRange<Integer> {

	@Override
	public void set(Integer min, Integer max) {
		min = Math.max(1, Math.min(31, min));
		max = Math.max(1, Math.min(31, max));
		super.set(min, max);
	}

	@Override
	protected void minMaxChanger(Parameter parameter) {
		if (parameter.getMin() > parameter.getMax()) {
			parameter.changeMinMax();
		}
	}

}
