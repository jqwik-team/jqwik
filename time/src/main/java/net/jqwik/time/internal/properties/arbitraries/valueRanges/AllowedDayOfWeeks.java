package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.time.*;
import java.util.*;

public class AllowedDayOfWeeks extends AllowedUnits<DayOfWeek> {

	@Override
	protected void setDefaultAllowed() {
		allowed = new LinkedHashSet<>(Arrays.asList(DayOfWeek.values()));
	}

}
