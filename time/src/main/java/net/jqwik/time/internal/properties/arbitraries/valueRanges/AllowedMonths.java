package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public class AllowedMonths extends AllowedUnits<Month> {

	@Override
	protected void setDefaultAllowed() {
		allowed = new HashSet<>(Arrays.asList(Month.values()));
	}

	public void setBetween(MonthBetween monthBetween) {
		allowed = Arrays.stream(Month.values())
						.filter(m -> m.compareTo(monthBetween.getMin()) >= 0 && m.compareTo(monthBetween.getMax()) <= 0)
						.collect(Collectors.toSet());
	}

}
