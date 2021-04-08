package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public class SecondBetween extends Between<Integer> {
	@Override
	protected void checkValidity(Integer min, Integer max) {
		if ((min != null && min < 0) || (max != null && max > 59)) {
			throw new IllegalArgumentException("Second value must be between 0 and 59.");
		}
	}
}
