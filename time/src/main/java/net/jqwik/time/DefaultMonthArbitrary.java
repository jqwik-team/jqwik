package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultMonthArbitrary extends ArbitraryDecorator<Month> implements MonthArbitrary {

	private Month min = Month.JANUARY;
	private Month max = Month.DECEMBER;

	@Override
	protected Arbitrary<Month> arbitrary() {
		Arbitrary<Month> months = Arbitraries.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER);
		months = months.filter(v -> v.compareTo(min) >= 0 && v.compareTo(max) <= 0);
		return months;
	}

	public static Month getMonthFromInt(int month){
		switch (month){
			case 1:
				return Month.JANUARY;
			case 2:
				return Month.FEBRUARY;
			case 3:
				return Month.MARCH;
			case 4:
				return Month.APRIL;
			case 5:
				return Month.MAY;
			case 6:
				return Month. JUNE;
			case 7:
				return Month.JULY;
			case 8:
				return Month.AUGUST;
			case 9:
				return Month.SEPTEMBER;
			case 10:
				return Month.OCTOBER;
			case 11:
				return Month.NOVEMBER;
			case 12:
				return Month.DECEMBER;
			default:
				return null;
		}
	}

	@Override
	public MonthArbitrary atTheEarliest(Month min) {
		DefaultMonthArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public MonthArbitrary atTheLatest(Month max) {
		DefaultMonthArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

}
