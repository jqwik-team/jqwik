package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public class DayOfMonthBetween extends Between<Integer> {
	@Override
	public DayOfMonthBetween set(Integer min, Integer max) {
		min = Math.max(1, Math.min(31, min));
		max = Math.max(1, Math.min(31, max));
		return (DayOfMonthBetween) super.set(min, max);
	}
}
