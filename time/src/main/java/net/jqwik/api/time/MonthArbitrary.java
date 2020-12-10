package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.*;

public interface MonthArbitrary extends Arbitrary<Month> {

	//TODO: Documentation
	default MonthArbitrary between(Month startMonth, Month endMonth){
		return atTheEarliest(startMonth).atTheLatest(endMonth);
	}
	default MonthArbitrary between(int startMonth, int endMonth){
		return between(DefaultMonthArbitrary.getMonthFromInt(startMonth), DefaultMonthArbitrary.getMonthFromInt(endMonth));
	}
	MonthArbitrary atTheEarliest(Month min);
	default MonthArbitrary atTheEarliest(int min){
		return atTheEarliest(DefaultMonthArbitrary.getMonthFromInt(min));
	}
	MonthArbitrary atTheLatest(Month max);
	default MonthArbitrary atTheLatest(int max){
		return atTheLatest(DefaultMonthArbitrary.getMonthFromInt(max));
	}
	MonthArbitrary only(Month... months);

}
