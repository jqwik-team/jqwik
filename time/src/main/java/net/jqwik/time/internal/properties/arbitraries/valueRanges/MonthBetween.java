package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;

public class MonthBetween extends ValueRange<Month, Month> {

	@Override
	protected void minMaxChanger() {
		if (min.compareTo(max) > 0) {
			Month remember = min;
			min = max;
			max = remember;
		}
	}

}
