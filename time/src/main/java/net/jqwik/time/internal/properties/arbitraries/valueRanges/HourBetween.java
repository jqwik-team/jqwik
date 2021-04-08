package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public class HourBetween extends Between<Integer> {
	@Override
	protected void checkValidity(Integer min, Integer max) {
		if ((min != null && min < 0) || (max != null && max > 23)) {
			throw new IllegalArgumentException("Hour value must be between 0 and 23.");
		}
	}
}
