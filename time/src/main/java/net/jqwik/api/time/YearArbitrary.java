package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface YearArbitrary extends Arbitrary<Year> {

	//TODO: Documentation
	default YearArbitrary between(Year startYear, Year endYear){
		return greaterOrEqual(startYear).lessOrEqual(endYear);
	}
	default YearArbitrary between(int startYear, int endYear){
		return between(Year.of(startYear), Year.of(endYear));
	}
	YearArbitrary greaterOrEqual(Year min);
	default YearArbitrary greaterOrEqual(int min){
		return greaterOrEqual(Year.of(min));
	};
	YearArbitrary lessOrEqual(Year max);
	default YearArbitrary lessOrEqual(int max){
		return lessOrEqual(Year.of(max));
	};

}
