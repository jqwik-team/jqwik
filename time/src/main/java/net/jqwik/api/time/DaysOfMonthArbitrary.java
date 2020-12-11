package net.jqwik.api.time;

import net.jqwik.api.*;

public interface DaysOfMonthArbitrary extends Arbitrary<Integer> {

	//TODO Documentation
	default DaysOfMonthArbitrary between(int startDayOfWeek, int endDayOfWeek){
		return greaterOrEqual(startDayOfWeek).lessOrEqual(endDayOfWeek);
	};
	DaysOfMonthArbitrary greaterOrEqual(int min);
	DaysOfMonthArbitrary lessOrEqual(int max);

}
