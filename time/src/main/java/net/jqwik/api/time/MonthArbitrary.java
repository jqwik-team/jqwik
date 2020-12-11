package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface MonthArbitrary extends Arbitrary<Month> {

	//TODO: Documentation
	default MonthArbitrary between(Month startMonth, Month endMonth){
		return atTheEarliest(startMonth).atTheLatest(endMonth);
	}
	default MonthArbitrary between(int startMonth, int endMonth){
		return between(Month.of(startMonth), Month.of(endMonth));
	}
	MonthArbitrary atTheEarliest(Month min);
	default MonthArbitrary atTheEarliest(int min){
		return atTheEarliest(Month.of(min));
	}
	MonthArbitrary atTheLatest(Month max);
	default MonthArbitrary atTheLatest(int max){
		return atTheLatest(Month.of(max));
	}
	MonthArbitrary only(Month... months);

}
