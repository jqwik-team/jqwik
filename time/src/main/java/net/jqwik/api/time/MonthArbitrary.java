package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface MonthArbitrary extends Arbitrary<Month> {

	//TODO: Documentation
	//TODO: nur bestimmte Monate
	default MonthArbitrary between(Month startMonth, Month endMonth){
		return atTheEarliest(startMonth).atTheLatest(endMonth);
	}
	MonthArbitrary atTheEarliest(Month min);
	MonthArbitrary atTheLatest(Month max);

}
