package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface DaysOfWeekArbitrary extends Arbitrary<DayOfWeek> {

	//TODO Documentation
	DaysOfWeekArbitrary only(DayOfWeek... dayOfWeeks);

}
